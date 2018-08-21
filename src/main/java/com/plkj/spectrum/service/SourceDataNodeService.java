package com.plkj.spectrum.service;

import com.alibaba.fastjson.JSONObject;
import com.plkj.spectrum.bean.ProcessRelation;
import com.plkj.spectrum.bean.SourceDataNode;
import com.plkj.spectrum.dao.ProcessRelationDao;
import com.plkj.spectrum.dao.SourceDataNodeDao;
import com.plkj.spectrum.tool.DataTool;
import com.plkj.spectrum.tool.JsonTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class SourceDataNodeService {
    @Autowired
    private SourceDataNodeDao sourceDataNodeDao;
    @Autowired
    private ProcessRelationDao processRelationDao;

    @Autowired
    @Qualifier(value = "hiveJdbcTemplate")
    JdbcTemplate hiveJdbcTemplate;

    public JSONObject executeData() {
        List<SourceDataNode> sourceDataNodes = sourceDataNodeDao.findAllData();
        JSONObject object = new JSONObject();
        try {
            List<ProcessRelation> processRelationList = DataTool.executeData(sourceDataNodes);
            processRelationDao.truncateTable();
            if (processRelationList.size() != 0) {
                processRelationDao.insertData(processRelationList);
            }
            object.put("message", "error");
        } catch (Exception e) {
            object.put("message", "error");
        }
        object.put("message", "success");
        return object;
    }

    public JSONObject insertExcel(MultipartFile file) {
        JSONObject object = new JSONObject();
        List<SourceDataNode> sourceDataNodes = null;
        try {
            sourceDataNodes = DataTool.getInitData(file);
            StringBuffer sql = new StringBuffer("insert into SOURCE_DATA_NODE values ");

            if (sourceDataNodes.size() != 0) {
                for (int i = 0; i < sourceDataNodes.size(); i++) {
                    SourceDataNode sourceDataNode = sourceDataNodes.get(i);
                    sql.append("(\"" + JsonTool.dealString(sourceDataNode.getTargetTableName()) + "\",")
                            .append("\"" + JsonTool.dealString(sourceDataNode.getTargetTableComment()) + "\",")
                            .append("\"" + JsonTool.dealString(sourceDataNode.getTargetColumnName()) + "\",")
                            .append("\"" + JsonTool.dealString(sourceDataNode.getTargetColumnComment()) + "\",")
                            .append("\"" + JsonTool.dealString(sourceDataNode.getSourceTableName()) + "\",")
                            .append("\"" + JsonTool.dealString(sourceDataNode.getSourceTableComment()) + "\",")
                            .append("\"" + JsonTool.dealString(sourceDataNode.getSourceColumnName()) + "\",")
                            .append("\"" + JsonTool.dealString(sourceDataNode.getSourceColumnComment()) + "\",")
                            .append("\"" + JsonTool.dealString(sourceDataNode.getStoreProcedure()) + "\")");
                    if (i < sourceDataNodes.size() - 1) {
                        sql.append(",");
                    }
                }
                String executeSql = sql.toString().replaceAll("\\\\n", "");
                hiveJdbcTemplate.execute(executeSql);
                executeData();
            }
        } catch (Exception e) {
            object.put("message", "error");
            e.printStackTrace();

        }
//        object.put("message", "success");
        return object;

    }
}
