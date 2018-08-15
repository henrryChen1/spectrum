package com.plkj.spectrum.dao;

import com.plkj.spectrum.bean.ProcessRelation;

import java.util.List;
import java.util.Map;

public interface ProcessRelationDao {

    List<Map<String,String>> queryByFuzzyName(String tableName);

    ProcessRelation queryByTableName(String tableName);

    void truncateTable();

    void insertData(List<ProcessRelation> processRelationList);
}
