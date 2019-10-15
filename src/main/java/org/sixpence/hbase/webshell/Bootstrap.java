package org.sixpence.hbase.webshell;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Created by bianshi on 2019/6/28.
 */
@SpringBootApplication(scanBasePackages = {"org.sixpence"})
public class Bootstrap {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Bootstrap.class, args);
    }
}
