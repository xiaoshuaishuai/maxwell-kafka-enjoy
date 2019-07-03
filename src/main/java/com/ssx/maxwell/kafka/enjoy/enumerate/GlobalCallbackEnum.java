package com.ssx.maxwell.kafka.enjoy.enumerate;

public enum GlobalCallbackEnum {

    SUCCESS(200, "操作成功"),
    SERVICE_ERROR(500, "抱歉，您的操作出问题了，请反馈客服处理，谢谢"),


    REDIS_MAPPING_NO_DEFIEND(100000001, "redis_mapping未配置该表对应缓存模板"),
    KEY_NOT_RECOGNIZED(100000002, "不识别key"),
    JSON_PARSE_ERROR(100000003, "JSON转换异常"),

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
