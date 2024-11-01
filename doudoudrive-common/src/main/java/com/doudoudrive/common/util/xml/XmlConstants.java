package com.doudoudrive.common.util.xml;

import java.util.regex.Pattern;

/**
 * <p>XML相关常量</p>
 * <p>2024-04-23 12:02</p>
 *
 * @author Dan
 **/
public class XmlConstants {

    /**
     * 字符串常量：XML 不间断空格转义 {@code "&nbsp;" -> " "}
     */
    public static final String NBSP = "&nbsp;";
    /**
     * 字符串常量：XML And 符转义 {@code "&amp;" -> "&"}
     */
    public static final String AMP = "&amp;";
    /**
     * The Character '&amp;'.
     */
    public static final Character C_AMP = '&';
    /**
     * 字符串常量：XML 双引号转义 {@code "&quot;" -> "\""}
     */
    public static final String QUOTE = "&quot;";
    /**
     * 字符串常量：XML 单引号转义 {@code "&apos" -> "'"}
     */
    public static final String APOS = "&apos;";
    /**
     * The Character '''.
     */
    public static final Character C_APOS = '\'';
    /**
     * 字符串常量：XML 小于号转义 {@code "&lt;" -> "<"}
     */
    public static final String LT = "&lt;";
    /**
     * The Character '&lt;'.
     */
    public static final Character C_LT = '<';
    /**
     * 字符串常量：XML 大于号转义 {@code "&gt;" -> ">"}
     */
    public static final String GT = "&gt;";
    /**
     * The Character '&gt;'.
     */
    public static final Character C_GT = '>';
    /**
     * The Character '!'.
     */
    public static final Character C_BANG = '!';
    /**
     * The Character '?'.
     */
    public static final Character C_QUEST = '?';
    /**
     * 用于匹配除了可打印字符以外的任何 ASCII 控制字符的正则表达式
     * <pre>
     *     \\x00-\\x08: 从 ASCII 0 到 ASCII 8，这包括 ASCII 控制字符 NUL（空字符）到退格符（\x00 到 \x08）
     *     \\x0b-\\x0c: 从 ASCII 11 到 ASCII 12，这包括垂直制表符（\x0b）和换页符（\x0c）
     *     \\x0e-\\x1f: 从 ASCII 14 到 ASCII 31，这包括换行符（\x0a）、回车符（\x0d）和其他非打印字符
     * </pre>
     */
    public static final Pattern INVALID_PATTERN = Pattern.compile("[\\x00-\\x08\\x0b-\\x0c\\x0e-\\x1f]");
    /**
     * 在XML中注释的内容 正则
     */
    public static final Pattern COMMENT_PATTERN = Pattern.compile("(?s)<!--.+?-->");
    /**
     * XML格式化输出默认缩进量
     */
    public static final int INDENT_DEFAULT = 2;

}
