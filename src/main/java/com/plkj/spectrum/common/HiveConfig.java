package com.plkj.spectrum.common;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class HiveConfig {

    private static final Logger logger = LoggerFactory.getLogger(HiveConfig.class);


    @Autowired
    private Environment environment;


    @Bean(name = "dataSource")
    @Qualifier("hiveJdbcDataSource")
    public DruidDataSource dataSource(){
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(environment.getProperty("hive.url"));
        dataSource.setDriverClassName(environment.getProperty("hive.driver-class-name"));

        dataSource.setUsername(environment.getProperty("hive.username"));
        dataSource.setPassword(environment.getProperty("hive.password"));
        dataSource.setKeepAlive(true);
        dataSource.setConnectionErrorRetryAttempts(1);
        return dataSource;

    }

    @Bean(name = "hiveJdbcTemplate")
    public JdbcTemplate hiveJdbcTemplate(@Qualifier("hiveJdbcDataSource") DruidDataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
