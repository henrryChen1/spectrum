package com.plkj.spectrum;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpectrumApplicationTests {

    @Autowired
    @Qualifier("hiveJdbcTemplate")
    JdbcTemplate hiveJdbcTemplate;

    @Test
    public void contextLoads() {
        // create table
        StringBuffer sql = new StringBuffer("create table IF NOT EXISTS ");
        sql.append("HIVE_TEST1 ");
        sql.append("(KEY INT, VALUE STRING) ");
        sql.append("PARTITIONED BY (S_TIME DATE)"); // 分区存储
        sql.append("ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n' "); // 定义分隔符
        sql.append("STORED AS TEXTFILE"); // 作为文本存储

        // drop table
//		StringBuffer sql = new StringBuffer("DROP TABLE IF EXISTS ");
//		sql.append("HIVE_TEST1");


        hiveJdbcTemplate.execute(sql.toString());
    }

}
