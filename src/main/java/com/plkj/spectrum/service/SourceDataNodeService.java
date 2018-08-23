package com.plkj.spectrum.service;

import com.alibaba.fastjson.JSONObject;
import com.plkj.spectrum.bean.ProcessRelation;
import com.plkj.spectrum.bean.SourceDataNode;
import com.plkj.spectrum.dao.ProcessRelationDao;
import com.plkj.spectrum.dao.SourceDataNodeDao;
import com.plkj.spectrum.tool.DataTool;
import com.plkj.spectrum.tool.JsonTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class SourceDataNodeService {
    @Autowired
    private SourceDataNodeDao sourceDataNodeDao;
    @Autowired
    private ProcessRelationDao processRelationDao;


    public JSONObject executeData() {
        List<SourceDataNode> sourceDataNodes = sourceDataNodeDao.findAllData();
        JSONObject object = new JSONObject();
        try {
            List<ProcessRelation> processRelationList = DataTool.executeData(sourceDataNodes);
            processRelationDao.truncateTable();
            if (processRelationList.size() != 0) {
                processRelationDao.insertData(processRelationList);
            }
            object.put("message", "success");
        } catch (Exception e) {
            object.put("message", "error");
        }
        return object;
    }

    public JSONObject insertExcel(MultipartFile file) {
        JSONObject object = new JSONObject();
        List<SourceDataNode> sourceDataNodes = null;
        try {
            sourceDataNodes = DataTool.getInitData(file);

            if (sourceDataNodes.size() != 0) {
                for (int i = 0; i < sourceDataNodes.size(); i++) {
                    SourceDataNode sourceDataNode = sourceDataNodes.get(i);
                    sourceDataNode.setTargetTableName(JsonTool.dealString(sourceDataNode.getTargetTableName()));
                    sourceDataNode.setTargetTableComment(JsonTool.dealString(sourceDataNode.getTargetTableComment()));
                    sourceDataNode.setTargetColumnName(JsonTool.dealString(sourceDataNode.getTargetColumnName()));
                    sourceDataNode.setTargetColumnComment(JsonTool.dealString(sourceDataNode.getTargetColumnComment()));
                    sourceDataNode.setSourceTableName(JsonTool.dealString(sourceDataNode.getSourceTableName()));
                    sourceDataNode.setSourceTableComment(JsonTool.dealString(sourceDataNode.getSourceTableComment()));
                    sourceDataNode.setSourceColumnName(JsonTool.dealString(sourceDataNode.getSourceColumnName()));
                    sourceDataNode.setSourceColumnComment(JsonTool.dealString(sourceDataNode.getSourceColumnComment()));
                    sourceDataNode.setStoreProcedure(JsonTool.dealString(sourceDataNode.getStoreProcedure()));
                }
                sourceDataNodeDao.insertData(sourceDataNodes);
                List<ProcessRelation> processRelationList = DataTool.executeData(sourceDataNodes);
                processRelationDao.truncateTable();
                if (processRelationList.size() != 0) {
                    processRelationDao.insertData(processRelationList);
                }
                object.put("message", "success");
            }
        } catch (Exception e) {
            object.put("message", "error");
            e.printStackTrace();

        }
        return object;
    }
}
