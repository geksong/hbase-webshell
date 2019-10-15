package org.sixpence.hbase.webshell.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bianshi on 2019/7/1.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RowVo implements Serializable {
    private String rowKey;
    private List<ColumnFamilyVo> families;
}
