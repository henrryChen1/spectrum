package com.plkj.spectrum.service;

import com.plkj.spectrum.bean.ProcessRelation;
import com.plkj.spectrum.dao.ProcessRelationDao;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProcessRelationService {
    @Autowired
    private ProcessRelationDao dao;

    public List<String> queryByFuzzyName(String tableName) {
        //进行模糊查询预处理 前后加上百分号
        tableName = "%" + StringUtils.trim(StringUtils.upperCase(tableName)) + "%";
        List<String> list = dao.queryByFuzzyName(tableName);
        return list;
    }

    public ProcessRelation queryByName(String tableName) {
        //进行精准查询
        ProcessRelation relation = dao.queryByTableName(StringUtils.trim(StringUtils.upperCase(tableName)));
        if (relation != null) {
            relation.setMapJson(relation.getMapJson().replace("'", "\""));
        }
        return  relation;
    }
}
