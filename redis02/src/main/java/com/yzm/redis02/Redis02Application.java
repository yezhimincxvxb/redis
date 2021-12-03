package com.yzm.redis02;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class Redis02Application {

    public static void main(String[] args) {
        SpringApplication.run(Redis02Application.class, args);
    }

}
