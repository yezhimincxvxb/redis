package com.yzm.redis03;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class Redis03Application {

    public static void main(String[] args) {
        SpringApplication.run(Redis03Application.class, args);
    }

}
