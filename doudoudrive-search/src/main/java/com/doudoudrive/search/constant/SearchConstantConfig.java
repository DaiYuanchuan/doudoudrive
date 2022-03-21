package com.doudoudrive.search.constant;

/**
 * <p>搜索服务相关常量配置</p>
 * <p>2022-03-21 00:45</p>
 *
 * @author Dan
 **/
public interface SearchConstantConfig {

    /**
     * es索引名称相关常量
     */
    interface IndexName {
        String USERINFO = "userinfo";
    }

    /**
     * ik分词器相关常量
     */
    interface IkConstant {

        /**
         * 最粗粒度的拆分
         * <p>比如会将“中华人民共和国国歌”拆分为“中华人民共和国,国歌”，适合 Phrase 查询</p>
         */
        String IK_SMART = "ik_smart";

        /**
         * 最大化会将文本做最细粒度的拆分
         * <p>比如会将“中华人民共和国国歌”拆分为“中华人民共和国,中华人民,中华,华人,人民共和国,人民,人,民,共和国,共和,和,国国,国歌”，会穷尽各种可能的组合，适合 Term Query；</p>
         */
        String IK_MAX_WORD = "ik_max_word";
    }
}
