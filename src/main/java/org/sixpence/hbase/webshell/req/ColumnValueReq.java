package org.sixpence.hbase.webshell.req;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by bianshi on 2019/7/1.
 */
@Data
public class ColumnValueReq extends ColumnFamilyReq implements Serializable {
    private String columnName;
    private String value;
    private Long version;
}
