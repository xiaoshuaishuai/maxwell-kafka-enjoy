package com.ssx.maxwell.kafka.enjoy.common;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/4 14:49
 * @description:
 */
@Data
@Accessors(chain = true)
public class MaxwellData {
    private String database;
    private String table;
    private String type;
    private Long ts;
    private Long xid;
    private boolean commit;
    private String data;
    private String old;
}
