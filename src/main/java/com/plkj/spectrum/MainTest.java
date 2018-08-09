package com.plkj.spectrum;

import com.alibaba.fastjson.JSONArray;
import com.plkj.spectrum.bean.ProcessRelation;
import com.plkj.spectrum.tool.JsonTool;
import org.apache.commons.lang.StringUtils;
import com.plkj.spectrum.tool.QueryExecl;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
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
    public void insertDataTest() throws IOException, InvalidFormatException {
        StringBuffer sql = new StringBuffer("insert into PROCESS_RELATION values ");
        List<ProcessRelation> processRelationList = QueryExecl.excelRead();

// 拼接 并且插入数据库.d
        for (int i = 0; i < processRelationList.size(); i++) {
            ProcessRelation processRelation = processRelationList.get(i);
            sql.append("(\"" + JsonTool.dealString(processRelation.getTableName()) + "\",")
                    .append("\"" + JsonTool.dealString(processRelation.getStoreProcedure()) + "\",")
                    .append("\"" + JsonTool.dealString(processRelation.getComment()) + "\",")
                    .append("\"" + JsonTool.dealString(processRelation.getColumns().toString()) + "\",")
                    .append("\"" + JsonTool.dealString(processRelation.getSourceTables()) + "\",")
                    .append("\"" + JsonTool.dealString(processRelation.getAfterTables()) + "\",")
                    .append("\"" + JsonTool.dealString(processRelation.getMapJson()) + "\")");
            if (i < processRelationList.size() - 1) {
                sql.append(",");
            }

        }
        String executeSql = sql.toString().replaceAll("\\\\n", "");
        hiveJdbcTemplate.execute(executeSql);

    }


}
