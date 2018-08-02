package com.plkj.spectrum;

import com.plkj.spectrum.bean.ProcessRelation;
import com.plkj.spectrum.tool.JsonTool;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class MainTest {

    @Autowired
    @Qualifier(value = "hiveJdbcTemplate")
    JdbcTemplate hiveJdbcTemplate;


    @Test
    public void DataSourceTest() {
        StringBuffer sql = new StringBuffer("SELECT * FROM PERSON WHERE ID = 1");

        List<Map<String, Object>> list = hiveJdbcTemplate.queryForList(sql.toString());
        System.out.println(list);
        System.out.print(sql.toString());
    }

    @Test
    public void insertDataTest() {
        StringBuffer sql = new StringBuffer("insert into PROCESS_RELATION values (");
        ProcessRelation processRelation = new ProcessRelation();
        processRelation.setTableName("CW");
        processRelation.setSourceTables("AA,BB");
        processRelation.setAfterTables("CC");
        processRelation.setComment("demo");
        processRelation.setStoreProcedure("demoSP");
        //TODO加上一个BuildJson方法的参数
        sql.append("\""+processRelation.getTableName()).append("\",")
           .append("\""+processRelation.getStoreProcedure()).append("\",")
           .append("\""+processRelation.getComment()).append("\",")
           .append("\""+processRelation.getSourceTables()).append("\",")
           .append("\""+processRelation.getAfterTables()).append("\",")
           .append("\""+processRelation.getMapJson()).append("\")") ;

        hiveJdbcTemplate.execute(sql.toString());
    }

}
