package com.doudoudrive.common.util.xml;

import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.util.lang.CollectionUtil;
import com.doudoudrive.common.util.lang.ConvertUtil;
import com.google.common.collect.Maps;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.List;
import java.util.Map;

/**
 * <p>XML转换器，用于转换Map或Bean等</p>
 * <p>2024-04-23 13:07</p>
 *
 * @author Dan
 **/
public class XmlMapper {

    private final Node node;

    /**
     * 构造
     *
     * @param node {@link Node}XML节点
     */
    public XmlMapper(final Node node) {
        this.node = node;
    }

    /**
     * 创建XmlMapper
     *
     * @param node {@link Node}XML节点
     * @return XmlMapper
     */
    public static XmlMapper of(final Node node) {
        return new XmlMapper(node);
    }

    /**
     * XML节点转Map
     *
     * @param result 结果Map
     * @return map
     */
    private static Map<String, Object> toMap(final Node node, Map<String, Object> result) {
        if (null == result) {
            result = Maps.newHashMapWithExpectedSize(NumberConstant.INTEGER_TEN);
        }
        final NodeList nodeList = node.getChildNodes();
        final int length = nodeList.getLength();
        Node childNode;
        Element childEle;
        for (int i = 0; i < length; ++i) {
            childNode = nodeList.item(i);
            if (!XmlUtil.isElement(childNode)) {
                continue;
            }

            childEle = (Element) childNode;
            final Object newValue;
            if (childEle.hasChildNodes()) {
                // 子节点继续递归遍历
                final Map<String, Object> map = toMap(childEle, Maps.newLinkedHashMapWithExpectedSize(NumberConstant.INTEGER_TEN));
                if (CollectionUtil.isNotEmpty(map)) {
                    newValue = map;
                } else {
                    newValue = childEle.getTextContent();
                }
            } else {
                newValue = childEle.getTextContent();
            }

            if (null != newValue) {
                final Object value = result.get(childEle.getNodeName());
                if (null != value) {
                    if (value instanceof List) {
                        List<Object> list = ConvertUtil.convert(value);
                        list.add(newValue);
                    } else {
                        result.put(childEle.getNodeName(), CollectionUtil.toList(value, newValue));
                    }
                } else {
                    result.put(childEle.getNodeName(), newValue);
                }
            }
        }
        return result;
    }

    /**
     * XML转Java Bean<br>
     * 如果XML根节点只有一个，且节点名和Bean的名称一致，则直接转换子节点
     *
     * @param <T>  bean类型
     * @param bean bean类
     * @return bean
     * @since 5.2.4
     */
    public <T> T toBean(final Class<T> bean) {
        final Map<String, Object> map = toMap();
        try {
            if (CollectionUtil.isEmpty(map) || bean == null) {
                return null;
            }
            return ConvertUtil.convertMapToBean(bean, map);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * XML节点转Map
     *
     * @return map
     */
    public Map<String, Object> toMap() {
        return toMap(Maps.newHashMapWithExpectedSize(NumberConstant.INTEGER_TEN));
    }

    /**
     * XML节点转Map
     *
     * @param result 结果Map
     * @return map
     */
    public Map<String, Object> toMap(final Map<String, Object> result) {
        return toMap(this.node, result);
    }
}
