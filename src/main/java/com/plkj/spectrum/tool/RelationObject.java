package com.plkj.spectrum.tool;

import java.util.ArrayList;
import java.util.List;

public class RelationObject {
    private String tableName;
    private List<String> sourceTables;
    private List<String> afterTables;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<String> getSourceTables() {
        return sourceTables;
    }

    public void setSourceTables(List<String> sourceTables) {
        this.sourceTables = sourceTables;
    }

    public List<String> getAfterTables() {
        return afterTables;
    }

    public void setAfterTables(List<String> afterTables) {
        this.afterTables = afterTables;
    }

    public RelationObject(String tableName, List<String> sourceTables) {
        this.tableName = tableName;
        this.sourceTables = sourceTables;
        this.afterTables = new ArrayList<String>();
    }

    public RelationObject(String tableName, List<String> sourceTables, List<String> afterTables) {
        this.tableName = tableName;
        this.sourceTables = sourceTables;
        this.afterTables = afterTables;
    }

    public RelationObject() {
    }

    @Override
    public String toString() {
        return "RelationObject{" +
                "tableName='" + tableName + '\'' +
                ", sourceTables=" + sourceTables +
                ", afterTables=" + afterTables +
                '}';
    }
}
