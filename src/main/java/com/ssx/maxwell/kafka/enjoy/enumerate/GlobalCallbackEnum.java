package com.ssx.maxwell.kafka.enjoy.enumerate;

public enum GlobalCallbackEnum {

    SUCCESS(200, "操作成功"),
    SERVICE_ERROR(500, "抱歉，您的操作出问题了，请反馈客服处理，谢谢"),


    REDIS_MAPPING_NO_DEFIEND(100000001, "redis_mapping未配置该表对应缓存模板"),
    KEY_NOT_RECOGNIZED(100000002, "该条件下不存在数据"),
    JSON_PARSE_ERROR(100000003, "JSON转换异常"),
    PARAMETER_ERROR(100000004, "key参数格式错误"),
    PARAMETER_RULE_ERROR(100000005, "缓存规则未配置"),
    PARAMETER_TEMPLATE_ERROR(100000006, "缓存未命中,自定义缓存模板参数不能为空"),
    SYSTEM_ENCODE_ERROR(100000008, "编码异常"),
    LOSE_DATA_ERROR(100000009, "redis_mapping缺失数据库表对应记录"),

    ;
    /**
     * 编码
     */
    private Integer value;

    /**
     * 说明
     */
    private String intro;

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    private GlobalCallbackEnum(Integer value, String intro) {
        this.value = value;
        this.intro = intro;
    }

}
