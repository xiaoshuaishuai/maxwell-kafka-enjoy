package com.ssx.maxwell.kafka.enjoy.common.model.query;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/6 15:29
 * @description:
 */
@Data
@Document(indexName = "maxwell_es_mapping", type = "_doc")
public class ElasticsearchMapping {
    /**
     * ID
     * database_table_id
     */
    private String id;
    /**
     * 数据库
     */
    @Field(type = FieldType.Keyword)
    private String dbDatabase;
    /**
     * 表
     */
    @Field(type = FieldType.Keyword)
    private String dbTable;

    /**
     * json
     */
    @Field(type = FieldType.Text)
    private String mkeData;
    /**
     * 0:保留
     * 1:删除
     */
    @Field(type = FieldType.Integer)
    private Integer isDel;

    /**
     * 创建时间
     */
    @Field(type = FieldType.Long)
    private Long gmtCreate;
    /**
     * 更新时间
     */
    @Field(type = FieldType.Long)
    private Long gmtModify;

}
