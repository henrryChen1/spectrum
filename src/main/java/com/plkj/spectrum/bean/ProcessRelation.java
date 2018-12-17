package com.plkj.spectrum.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProcessRelation implements Serializable {
    private String tableName;
    private String sourceTables;
    private String afterTables;
    private String mapJson;


    private static final long serialVersionUID = 2L;

    public ProcessRelation() {
        init();
    }

    public void init() {
        this.tableName = "";
        this.sourceTables = "";
        this.afterTables = "";
        this.mapJson = "";
    }

    public ProcessRelation(String tableName, String sourceTables, String afterTables, String mapJson) {
        this.tableName = tableName;
        this.sourceTables = sourceTables;
        this.afterTables = afterTables;
        this.mapJson = mapJson;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getSourceTables() {
        return sourceTables;
    }

    public void setSourceTables(String sourceTables) {
        this.sourceTables = sourceTables;
    }

    public String getAfterTables() {
        return afterTables;
    }

    public void setAfterTables(String afterTables) {
        this.afterTables = afterTables;
    }

    public String getMapJson() {
        return mapJson;
    }

    public void setMapJson(String mapJson) {
        this.mapJson = mapJson;
    }

    @Override
    public String toString() {
        return "ProcessRelation{" +
                "tableName='" + tableName + '\'' +
                ", sourceTables='" + sourceTables + '\'' +
                ", afterTables='" + afterTables + '\'' +
                ", mapJson='" + mapJson + '\'' +
                '}';
    }
}
