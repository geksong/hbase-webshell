package org.sixpence.hbase.webshell;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by bianshi on 2019/7/1.
 */
@Slf4j
@Component
public class ConnectionHolder implements ApplicationListener<ApplicationContextEvent> {
    private static Connection con = null;

    public static Connection connection() {
        return con;
    }

    private void init(String hbaseZkAddress) {
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", hbaseZkAddress);
        try {
            HBaseAdmin.available(conf);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            ConnectionHolder.con = ConnectionFactory.createConnection(conf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void shutdown() {
        if(null == ConnectionHolder.con) return;
        try {
            ConnectionHolder.con.close();
        }catch (Exception e) {
            log.error("hbase 连接关闭异常", e);
        }
    }

    @Override
    public void onApplicationEvent(ApplicationContextEvent event) {
        if(null == event.getApplicationContext().getParent()) {
            if(event instanceof ContextRefreshedEvent) {
                init(event.getApplicationContext().getEnvironment().getProperty("hbase.zookeeper.address"));
            }else if(event instanceof ContextStoppedEvent || event instanceof ContextClosedEvent) {
                shutdown();
            }
        }
    }
}
