package org.sixpence.hbase.webshell.req;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bianshi on 2019/7/1.
 */
@Data
public class RowValueReq implements Serializable {
    private String rowKey;
    private List<ColumnValueReq> columnValues;
}
