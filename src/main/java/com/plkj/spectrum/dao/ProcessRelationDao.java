package com.plkj.spectrum.dao;

import com.plkj.spectrum.bean.ProcessRelation;

import java.util.List;

public interface ProcessRelationDao {

    List<String> queryByFuzzyName(String tableName);

    ProcessRelation queryByTableName(String tableName);
}
