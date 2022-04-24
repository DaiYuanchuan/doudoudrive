package com.doudoudrive.common.util.lang;

import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;

/**
 * <p>通过Java实体类的get方法名获取对应的属性名</p>
 * <p>2022-04-05 17:40</p>
 *
 * @author Dan
 **/
@Slf4j
public class ReflectUtil {

    public final static String IS = "is";
    public final static String GET = "get";
    public final static String SET = "set";

    private ReflectUtil() {
    }

    /**
     * <p>通过Java实体类某个属性的getter方法获取该属性的属性名称</p>
     *
     * <b>Java实体:</b>
     * <pre>
     * public class Person {
     *     private String name;
     *     private Integer age;
     *     private String hobby;
     *     private Boolean is;
     *     private Boolean isEnable;
     *     // 省略getter, setter
     * }
     * </pre>
     * <b>使用实例:</b>
     * <pre>
     * ReflectUtil.property(Person::getName)      = name
     * ReflectUtil.property(Person::getAge)       = age
     * ReflectUtil.property(Person::getHobby)     = hobby
     * ReflectUtil.property(Person::getIs)        = is
     * ReflectUtil.property(Person::getIsEnable)  = isEnable
     * </pre>
     *
     * @param domainLambdaExpression 属性名称lambda表达式
     * @param <T>                    Java Bean的类型
     * @return 属性名称
     */
    public static <T> String property(FieldFunction<T, Object> domainLambdaExpression) {
        try {
            Method writeReplace = domainLambdaExpression.getClass().getDeclaredMethod("writeReplace");
            writeReplace.setAccessible(true);
            Object invokeObj = writeReplace.invoke(domainLambdaExpression);
            SerializedLambda serializedLambda = (SerializedLambda) invokeObj;
            // 传入方法名
            return methodToProperty(Objects.requireNonNull(serializedLambda.getImplMethodName()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("Failed to get Java Bean property name");
        }
    }

    /**
     * 将实体类里指定字段属性由"驼峰"命名风格转为"下划线"命名风格
     * <p>该方法比写死在 @JsonProperty 里面的灵活</p>
     * <b>Java实体:</b>
     * <pre>
     * public static class SocialUser implements Serializable {
     *     private static final long serialVersionUID = 1L;
     *
     *     private String accessToken;
     *     private Long expiresIn;
     *     private String uid;
     *     private String isRealName;
     *     // 省略getter, setter, 构造方法, 建议使用lombok
     * }
     * </pre>
     * <b>使用示例:</b>
     * <pre>
     * ReflectUtil.propertyToUnderline(SocialUser::getAccessToken) = access_token
     * ReflectUtil.propertyToUnderline(SocialUser::getExpiresIn)   = expires_in
     * ReflectUtil.propertyToUnderline(SocialUser::getUid)         = uid
     * ReflectUtil.propertyToUnderline(SocialUser::getIsRealName)  = is_real_name
     * </pre>
     *
     * @param domainLambdaExpression 属性名称lambda表达式
     * @param <T>                    Java Bean的类型
     * @return 实体类属性名的下划线风格
     */
    public static <T> String propertyToUnderline(FieldFunction<T, Object> domainLambdaExpression) {
        String property = property(domainLambdaExpression);
        return translate(property);
    }

    /**
     * 属性名转下划线
     * <ul>
     *     <li>
     *       If the Java property name starts with an underscore, then that underscore is not included in the translated name,
     *       unless the Java property name is just one character in length, i.e.,
     *       it is the underscore character. This applies only to the first character of the Java property name.
     *     </li>
     * </ul>
     * <p>
     * These rules result in the following additional example translations from
     * Java property names to JSON element names.
     * <ul><li>"userName" is translated to "user_name"</li>
     * <li>"UserName" is translated to "user_name"</li>
     * <li>"USER_NAME" is translated to "user_name"</li>
     * <li>"user_name" is translated to "user_name" (unchanged)</li>
     * <li>"user" is translated to "user" (unchanged)</li>
     * <li>"User" is translated to "user"</li>
     * <li>"USER" is translated to "user"</li>
     * <li>"_user" is translated to "user"</li>
     * <li>"_User" is translated to "user"</li>
     * <li>"__user" is translated to "_user"
     * (the first of two underscores was removed)</li>
     * <li>"user__name" is translated to "user__name"
     * (unchanged, with two underscores)</li></ul>
     */
    private static String translate(String input) {
        // garbage in, garbage out
        if (input == null) {
            return null;
        }
        int length = input.length();
        StringBuilder result = new StringBuilder(length * 2);
        int resultLength = 0;
        boolean wasPrevTranslated = false;
        for (int i = 0; i < length; i++) {
            char c = input.charAt(i);
            // skip first starting underscore
            if (i > 0 || c != '_') {
                if (Character.isUpperCase(c)) {
                    if (!wasPrevTranslated && resultLength > 0 && result.charAt(resultLength - 1) != '_') {
                        result.append('_');
                        resultLength++;
                    }
                    c = Character.toLowerCase(c);
                    wasPrevTranslated = true;
                } else {
                    wasPrevTranslated = false;
                }
                result.append(c);
                resultLength++;
            }
        }
        return resultLength > 0 ? result.toString() : input;
    }

    /**
     * Java方法名转属性名
     *
     * @param methodName Java Bean某个属性的getter或者setter方法
     * @return 返回属性值名称
     */
    private static String methodToProperty(String methodName) {
        if (methodName == null) {
            return null;
        }
        if (methodName.startsWith(IS)) {
            methodName = methodName.substring(2);
        } else {
            if (methodName.startsWith(GET) || methodName.startsWith(SET)) {
                methodName = methodName.substring(3);
            } else {
                log.error("Error parsing property name '{}'.  Didn't start with 'is', 'get' or 'set'.", methodName);
                throw new RuntimeException("Error parsing property name '" + methodName + "'.  Didn't start with 'is', 'get' or 'set'.");
            }
        }
        // 以小写字母开头
        boolean startWithLowercase = methodName.length() == 1 || (methodName.length() > 1 && !Character.isUpperCase(methodName.charAt(1)));
        if (startWithLowercase) {
            methodName = methodName.substring(0, 1).toLowerCase(Locale.ENGLISH) + methodName.substring(1);
        }
        return methodName;
    }

    @FunctionalInterface
    public interface FieldFunction<T, R> extends Function<T, R>, Serializable {
    }
}
