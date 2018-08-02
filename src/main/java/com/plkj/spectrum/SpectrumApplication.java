package com.plkj.spectrum;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.plkj.spectrum.dao")
public class SpectrumApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpectrumApplication.class, args);
    }
}
