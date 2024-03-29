package com.ssx.maxwell.kafka.enjoy.enumerate;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/3 17:34
 * @description: maxwell解析相关常量
 */
public final class MaxwellBinlogConstants {
    //清除REDIS单条主键缓存
    public static final Integer REDIS_ONLY_CLEAR_TABLE_ROW_CACHE = 1;
    //清除REDIS全表缓存
    public static final Integer REDIS_ONLY_CLEAR_TABLE_ALL_CACHE = 2;
    //清除REDIS全表缓存&单条缓存&自定义缓存(如果有)
    public static final Integer REDIS_CLEAR_TABLE_ALL_AND_ROW_CACHE = 3;
    //等待
    public static final Integer REDIS_CLEAR_WAITING_CACHE = 4;

    //无缓存
    public static final String REDIS_RULE_0 = "0";
    //单表主键引导缓存
    public static final String REDIS_RULE_1 = "1";
    //全表缓存
    public static final String REDIS_RULE_2 = "2";
    //自定义缓存
    public static final String REDIS_RULE_3 = "3";

    /**
     * 自定义缓存空值填充
     */
    public static final String REDIS_VAL_NONE_MAGIC = "NONE";


    public enum MaxwellBinlogEnum {
        INSERT("insert", MaxwellBinlogConstants.REDIS_CLEAR_TABLE_ALL_AND_ROW_CACHE),
        UPDATE("update", MaxwellBinlogConstants.REDIS_CLEAR_TABLE_ALL_AND_ROW_CACHE),
        DELETE("delete", MaxwellBinlogConstants.REDIS_CLEAR_TABLE_ALL_AND_ROW_CACHE),
        BOOTSTRAP_START("bootstrap-start", MaxwellBinlogConstants.REDIS_CLEAR_WAITING_CACHE),
        BOOTSTRAP_INSERT("bootstrap-insert", MaxwellBinlogConstants.REDIS_CLEAR_TABLE_ALL_AND_ROW_CACHE),
        BOOTSTRAP_UPDATE("bootstrap-update", MaxwellBinlogConstants.REDIS_CLEAR_TABLE_ALL_AND_ROW_CACHE),
        BOOTSTRAP_DELETE("bootstrap-delete", MaxwellBinlogConstants.REDIS_CLEAR_TABLE_ALL_AND_ROW_CACHE),
        BOOTSTRAP_COMPLETE("bootstrap-complete", MaxwellBinlogConstants.REDIS_CLEAR_WAITING_CACHE),
        ;
        @Setter
        @Getter
        public String type;
        @Setter
        @Getter
        public Integer operate;

        MaxwellBinlogEnum(String type, Integer operate) {
            this.type = type;
            this.operate = operate;
        }

        public static MaxwellBinlogEnum getMaxwellBinlogEnum(String type) {
            if (StringUtils.isEmpty(type)) {
                return null;
            }
            for (MaxwellBinlogEnum e : MaxwellBinlogEnum.values()) {
                if (type.equals(e.getType())) {
                    return e;
                }
            }
            return null;
        }

    }
    public static final String KEY_ITEM = REDIS_RULE_1 + ":item";
    public static final String KEY_LIST = REDIS_RULE_2 + ":list";
    public static final String KEY_CUSTOM = REDIS_RULE_3 + ":custom";

    /**
     * 环境+database+table
     */
    public static final String REDIS_CACHE_KEY_TEMPLATE_PREFIX = "{0}:{1}:{2}:";
    /**
     * 环境+database+table+1+item+主键
     */
    public static final String REDIS_CACHE_KEY_TEMPLATE_ITEM_PK_ID = REDIS_CACHE_KEY_TEMPLATE_PREFIX + KEY_ITEM + ":{3}";
    /**
     * 环境+database+table+2+list
     */
    public static final String REDIS_CACHE_KEY_TEMPLATE_PREFIX_LIST = REDIS_CACHE_KEY_TEMPLATE_PREFIX + KEY_LIST;
    /**
     * 环境+database+table+3+自定义+custom
     */
    public static final String REDIS_CACHE_KEY_TEMPLATE_PREFIX_CUSTOM = REDIS_CACHE_KEY_TEMPLATE_PREFIX + KEY_CUSTOM;

    public enum RedisCacheKeyTemplateEnum {
        /**
         * {0} 环境
         * {1} database
         * {2} table
         * {3} id
         * 默认存储list<对象>格式
         */
        REDIS_CACHE_KEY_TEMPLATE_ITEM_PK_ID(MaxwellBinlogConstants.REDIS_CACHE_KEY_TEMPLATE_ITEM_PK_ID, "REDIS单条主键缓存"),
        /**
         * {0} 环境
         * {1} database
         * {2} table
         * 默认存储list<对象>格式
         */
        REDIS_CACHE_KEY_TEMPLATE_PREFIX_LIST(MaxwellBinlogConstants.REDIS_CACHE_KEY_TEMPLATE_PREFIX_LIST, "REDIS全表缓存"),
        /**
         * {0} 环境
         * {1} database
         * {2} table
         * 默认存储list<对象>格式
         */
        REDIS_CACHE_KEY_TEMPLATE_PREFIX_CUSTOM(MaxwellBinlogConstants.REDIS_CACHE_KEY_TEMPLATE_PREFIX_CUSTOM, "REDIS自定义缓存"),
        ;
        @Setter
        @Getter
        private String template;

        @Setter
        @Getter
        private String desc;

        private RedisCacheKeyTemplateEnum(String template, String desc) {
            this.template = template;
            this.desc = desc;
        }

    }

    public enum RedisRunSqlTemplateEnum {
        /**
         * {0} dbTable
         * {1} id
         */
        SQL_PRIMARY_ID(SqlConstants.SQL_PRIMARY_ID, "主键ID查询SQL"),
        /**
         * {0} dbTable
         * {1} order by ...
         */
        SQL_ALL(SqlConstants.SQL_ALL, "全表查询"),
        /**
         * {0} dbTable
         * {1} 查询条件
         * {2} order by ...
         */
        SQL_CUSTOM(SqlConstants.SQL_CUSTOM, "自定义缓存查询"),
        ;
        @Setter
        @Getter
        private String template;

        @Setter
        @Getter
        private String desc;

        private RedisRunSqlTemplateEnum(String template, String desc) {
            this.template = template;
            this.desc = desc;
        }

    }
}
