package com.plkj.spectrum.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.plkj.spectrum.bean.ProcessRelation;
import com.plkj.spectrum.dao.ProcessRelationDao;
import com.plkj.spectrum.tool.JsonTool;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ProcessRelationService {
    @Autowired
    private ProcessRelationDao dao;

    public JSONArray queryByFuzzyName(String tableName) {
        //进行模糊查询预处理 前后加上百分号
        tableName = "%" + StringUtils.trim(StringUtils.upperCase(tableName)) + "%";
        List<Map<String, String>> result = dao.queryByFuzzyName(tableName);
        JSONArray array = new JSONArray();
        if (result == null || result.size() == 0) {
            return null;
        }
        for (Map<String,String> map : result) {
                JSONObject object = new JSONObject();
                object.put("name", map.get("table_name"));
                object.put("parent", 0);
                int parentId = array.size() + 1;
                object.put("id", array.size() + 1);
                array.add(object);
                String[] columns = map.get("columns").split("'");
                for (int i = 1; i < columns.length; i = i + 2) {
                    object = new JSONObject();
                    object.put("name", columns[i]);
                    object.put("parent", parentId);
                    object.put("id", array.size()+1);
                    array.add(object);
                }

        }
        return array;

    }

    public JSONObject queryByName(String tableName) {
        //进行精准查询
        ProcessRelation relation = dao.queryByTableName(StringUtils.trim(StringUtils.upperCase(tableName)));
        JSONObject object = JsonTool.getJson(relation);
        return  object;
    }
}
