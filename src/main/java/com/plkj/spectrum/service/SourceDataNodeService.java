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
                int end = processRelationList.size();
                while (end>100){
                    processRelationDao.insertData(processRelationList.subList(end-100,end));
                    end-=100;
                }
                processRelationDao.insertData(processRelationList.subList(0,end));
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
                    sourceDataNode.setSourceTableName(JsonTool.dealString(sourceDataNode.getSourceTableName()));
                }
                sourceDataNodeDao.insertData(sourceDataNodes);
                List<ProcessRelation> processRelationList = DataTool.executeData(sourceDataNodes);
                processRelationDao.truncateTable();
                if (processRelationList.size() != 0) {

                int end = processRelationList.size();
                    while (end>100){
                        processRelationDao.insertData(processRelationList.subList(end-100,end));
                        end-=100;
                    }
                    processRelationDao.insertData(processRelationList.subList(0,end));

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
