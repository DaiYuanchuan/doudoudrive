package com.doudoudrive.commonservice.config;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.util.lang.CollectionUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * <p>配置拦截 SQL语句</p>
 * <p>2020-09-13 14:20</p>
 *
 * @author Dan
 **/
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
@Slf4j
public class SqlPrintInterceptor implements Interceptor {

    private static final String NULL_STRING = "null";
    private static final String SINGLE_QUOTE = "'";

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 获取xml中的一个 select/update/insert/delete节点,主要描述的是一条SQL语句
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameterObject = null;
        // 获取参数，if语句成立，表示sql语句有参数，参数格式是map形式
        if (invocation.getArgs().length > 1) {
            parameterObject = invocation.getArgs()[1];
        }

        long start = System.currentTimeMillis();

        Object result = invocation.proceed();

        // 获取到节点的id,即sql语句的id
        String statementId = mappedStatement.getId();
        // BoundSql就是封装myBatis最终产生的sql类
        BoundSql boundSql = mappedStatement.getBoundSql(parameterObject);
        // 获取节点的配置
        Configuration configuration = mappedStatement.getConfiguration();
        // 获取到最终的sql语句
        String sql = getSql(boundSql, parameterObject, configuration);

        long end = System.currentTimeMillis();
        long timing = end - start;
        if (log.isInfoEnabled()) {
            log.info("执行sql耗时:" + timing + " ms" + " - id:" + statementId + " - Sql:");
            log.info("语句:" + sql);
        }

        return result;
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    /**
     * 格式化SQL字符串<br>
     * 此方法只是简单将指定占位符 按照顺序替换为参数<br>
     *
     * @param strPattern sql字符串模板
     * @param argArray   参数列表
     * @return 结果
     */
    public static String formatSql(String strPattern, List<Object> argArray) {
        if (StringUtils.isBlank(strPattern) || CollectionUtil.isEmpty(argArray)) {
            return strPattern;
        }

        final int strPatternLength = strPattern.length();
        final int placeHolderLength = ConstantConfig.SpecialSymbols.QUESTION_MARK.length();

        // 初始化定义好的长度以获得更好的性能
        final StringBuilder strBuilder = new StringBuilder(strPatternLength + NumberConstant.INTEGER_FIFTY);

        // 记录已经处理到的位置
        int handledPosition = NumberConstant.INTEGER_ZERO;
        // 占位符所在位置
        int delimIndex;
        for (int argIndex = NumberConstant.INTEGER_ZERO; argIndex < argArray.size(); argIndex++) {
            delimIndex = strPattern.indexOf(ConstantConfig.SpecialSymbols.QUESTION_MARK, handledPosition);
            // 剩余部分无占位符
            if (delimIndex == NumberConstant.INTEGER_MINUS_ONE) {
                // 不带占位符的模板直接返回
                if (handledPosition == NumberConstant.INTEGER_ZERO) {
                    return strPattern;
                }
                // 字符串模板剩余部分不再包含占位符，加入剩余部分后返回结果
                strBuilder.append(strPattern, handledPosition, strPatternLength);
                return strBuilder.toString();
            }

            // 转义符
            if (delimIndex > NumberConstant.INTEGER_ZERO && strPattern.charAt(delimIndex - NumberConstant.INTEGER_ONE) == StrUtil.C_BACKSLASH) {
                // 双转义符
                if (delimIndex > NumberConstant.INTEGER_ONE && strPattern.charAt(delimIndex - NumberConstant.INTEGER_TWO) == StrUtil.C_BACKSLASH) {
                    // 转义符之前还有一个转义符，占位符依旧有效
                    strBuilder.append(strPattern, handledPosition, delimIndex - NumberConstant.INTEGER_ONE);
                    strBuilder.append(StrUtil.utf8Str(argArray.get(argIndex)));
                    handledPosition = delimIndex + placeHolderLength;
                } else {
                    // 占位符被转义
                    argIndex--;
                    strBuilder.append(strPattern, handledPosition, delimIndex - NumberConstant.INTEGER_ONE);
                    strBuilder.append(ConstantConfig.SpecialSymbols.QUESTION_MARK.charAt(NumberConstant.INTEGER_ZERO));
                    handledPosition = delimIndex + NumberConstant.INTEGER_ONE;
                }
            } else {
                // 正常占位符
                strBuilder.append(strPattern, handledPosition, delimIndex);
                strBuilder.append(StrUtil.utf8Str(argArray.get(argIndex)));
                handledPosition = delimIndex + placeHolderLength;
            }
        }

        // 加入最后一个占位符后所有的字符
        strBuilder.append(strPattern, handledPosition, strPatternLength);

        return strBuilder.toString();
    }

    private String getSql(BoundSql boundSql, Object parameterObject, Configuration configuration) {
        String sql = boundSql.getSql().replaceAll("\\s+", StringUtils.SPACE);
        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        if (boundSql.getParameterMappings() != null) {
            List<Object> parameter = Lists.newArrayListWithExpectedSize(boundSql.getParameterMappings().size());
            for (ParameterMapping parameterMapping : boundSql.getParameterMappings()) {
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    Object value;
                    String propertyName = parameterMapping.getProperty();
                    if (boundSql.hasAdditionalParameter(propertyName)) {
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (parameterObject == null) {
                        value = null;
                    } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                        value = parameterObject;
                    } else {
                        MetaObject metaObject = configuration.newMetaObject(parameterObject);
                        value = metaObject.getValue(propertyName);
                    }
                    parameter.add(formatProperty(value));
                }
            }
            sql = formatSql(sql, parameter);
        }
        return sql;
    }

    /**
     * 格式化属性值
     *
     * @param propertyValue 属性值
     * @return 转为字符串后的属性值
     */
    private String formatProperty(Object propertyValue) {
        String result;
        if (propertyValue != null) {
            if (propertyValue instanceof String) {
                result = SINGLE_QUOTE + propertyValue + SINGLE_QUOTE;
            } else if (propertyValue instanceof Date) {
                Locale locale = Locale.getDefault(Locale.Category.FORMAT);
                DateFormat dateFormat = new SimpleDateFormat(DatePattern.NORM_DATETIME_PATTERN, locale);
                result = SINGLE_QUOTE + dateFormat.format(propertyValue) + SINGLE_QUOTE;
            } else if (propertyValue instanceof LocalDateTime) {
                String format = LocalDateTimeUtil.format((LocalDateTime) propertyValue, DatePattern.NORM_DATETIME_PATTERN);
                result = SINGLE_QUOTE + format + SINGLE_QUOTE;
            } else {
                result = propertyValue.toString();
            }
        } else {
            result = NULL_STRING;
        }
        return result;
    }
}
