package org.sixpence.hbase.webshell.json;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.sixpence.hbase.webshell.ConnectionHolder;
import org.sixpence.hbase.webshell.req.ColumnFamilyReq;
import org.sixpence.hbase.webshell.req.ColumnValueReq;
import org.sixpence.hbase.webshell.req.RowValueReq;
import org.sixpence.hbase.webshell.vo.ColumnFamilyVo;
import org.sixpence.hbase.webshell.vo.ColumnValueVo;
import org.sixpence.hbase.webshell.vo.RowVo;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;

/**
 * Created by bianshi on 2019/6/28.
 */
@Slf4j
@RestController
@RequestMapping("/shell")
public class ShellController {
    @PostMapping("exe")
    public Mono<String> execute() throws IOException {


        return Mono.just("hello");
    }

    @PostMapping("{tableName}/create")
    public Mono<String> createTable(@PathVariable(value = "tableName") String tableName,
                                    @RequestBody List<ColumnFamilyReq> columnFamilies) {
        return createAdminMono().map(a -> {
            TableName tname = TableName.valueOf(tableName);
            try {
                if(a.tableExists(tname)) {
                    return tableName + " 表已经存在，如需重新创建，请先删除原来的表";
                }
                TableDescriptorBuilder builder = TableDescriptorBuilder.newBuilder(tname);
                for(ColumnFamilyReq cfr : columnFamilies) {
                    builder.setColumnFamily(ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(cfr.getFamilyName())).build());
                }
                TableDescriptor testTableD = builder.build();
                a.createTable(testTableD);
            }catch (Exception e) {
                log.error("创建表失败", e);
            }
            return "ok";
        });
    }

    @PostMapping("{tableName}/describe")
    public Mono<String> descTable(@PathVariable(value = "tableName") String tableName) {
        return createAdminMono().map(a -> {
            TableName tname = TableName.valueOf(tableName);
            try {
                if (!a.tableExists(tname)) {
                    return tableName + " 表不存在，请先创建表";
                }
                TableDescriptor desc = a.getDescriptor(tname);
                return JSON.toJSONString(desc);
            }catch (Exception e) {
                log.error("表描述获取失败", e);
            }
            return "ok";
        });
    }

    @PostMapping("{tableName}/put")
    public Mono<String> putValue(@PathVariable(value = "tableName") String tableName,
                                 @RequestBody RowValueReq rowReq) {
        return Mono.create(sink -> {
            String res = "ok";
            try {
                Connection con = ConnectionHolder.connection();
                TableName tname = TableName.valueOf(tableName);
                Table table = con.getTable(tname);
                Put put = new Put(Bytes.toBytes(rowReq.getRowKey()));
                List<ColumnValueReq> columns = rowReq.getColumnValues();
                columns.forEach(a -> put.addColumn(Bytes.toBytes(a.getFamilyName()), Bytes.toBytes(a.getColumnName()), null == a.getVersion() ? 0L : a.getVersion(), Bytes.toBytes(a.getValue())));
                table.put(put);
            }catch (Exception e) {
                log.error("put error", e);
                res = "error";
            }
            sink.success(res);
        });
    }

    @PostMapping("{tableName}/scan")
    public Mono<List<RowVo>> scanTable(@PathVariable(value = "tableName") String tableName,
                                  @RequestBody List<ColumnFamilyReq> columnFamilies) {
        return Mono.create(sink -> {
            List<RowVo> resL = new ArrayList<>();
            try {
                Connection con = ConnectionHolder.connection();
                TableName tname = TableName.valueOf(tableName);
                Table table = con.getTable(tname);
                Scan scan = new Scan();
                columnFamilies.forEach(a -> scan.addFamily(Bytes.toBytes(a.getFamilyName())));
                ResultScanner scanner = table.getScanner(scan);
                scanner.forEach(a -> {
                    NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> resMap = a.getMap();
                    List<ColumnFamilyVo> families = new ArrayList<>();
                    resMap.forEach((fmk, fmv) -> {
                        List<ColumnValueVo> cvvs = new ArrayList<>();
                        fmv.forEach((clk, clv) -> {
                            clv.forEach((vk, vv) -> {
                                ColumnValueVo cvv = new ColumnValueVo(Bytes.toString(clk), Bytes.toString(vv), vk);
                                cvvs.add(cvv);
                            });
                        });
                        ColumnFamilyVo cfv = new ColumnFamilyVo(Bytes.toString(fmk), cvvs);
                        families.add(cfv);
                    });
                    RowVo rv = new RowVo(Bytes.toString(a.getRow()), families);
                    resL.add(rv);
                });
            }catch (Exception e) {
                log.error("put error", e);
            }
            sink.success(resL);
        });
    }

    private Mono<Admin> createAdminMono() {
        return Mono.create(sink -> {
            Connection con = ConnectionHolder.connection();
            Admin admin = null;
            try {
                admin = con.getAdmin();
            }catch (Exception e) {
                log.error("获取admin异常", e);
            }
            sink.success(admin);
        });
    }
}
