package com.dgut;

import com.dgut.utils.K8sClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import java.io.IOException;

@Slf4j
@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
        log.info("项目启动成功...");
        try {
            K8sClient.initApiClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("k8s连接成功...");
    }
}
