package com.yzm.redis01;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class Redis01Application {

    public static void main(String[] args) {
        SpringApplication.run(Redis01Application.class, args);
    }

}
