package org.sixpence.hbase.webshell.req;

import lombok.Data;

import java.io.Serializable;

/**
 * 列族请求
 * Created by bianshi on 2019/7/1.
 */
@Data
public class ColumnFamilyReq implements Serializable {
    private String familyName;
}
