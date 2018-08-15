package com.plkj.spectrum.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProcessRelation implements Serializable {
    private static final long serialVersionUID = 1L;
    private String tableName;
    private String storeProcedure;
    private String comment;
    private String columns;
    private String sourceTables;
    private String afterTables;
    private String mapJson;


    public ProcessRelation() {
        init();
    }

    public void init() {
        this.tableName = "";
        this.storeProcedure = "";
        this.comment = "";
        this.sourceTables = "";
        this.afterTables = "";
        this.mapJson = "";
        this.columns = "";
    }

    @Override
    public String toString() {
        return "ProcessRelation{" +
                "tableName='" + tableName + '\'' +
                ", storeProcedure='" + storeProcedure + '\'' +
                ", comment='" + comment + '\'' +
                ", sourceTables='" + sourceTables + '\'' +
                ", afterTables='" + afterTables + '\'' +
                ", mapJson='" + mapJson + '\'' +
                ", columns=" + columns +
                '}';
    }

    public String getColumns() {
        return columns;
    }

    public void setColumns(String columns) {
        this.columns = columns;
    }

    public ProcessRelation(String tableName, String storeProcedure, String comment, String sourceTables,
                           String afterTables, String mapJson, String columns) {
        init();
        this.tableName = tableName;
        this.storeProcedure = storeProcedure;
        this.comment = comment;
        this.sourceTables = sourceTables;
        this.afterTables = afterTables;
        this.mapJson = mapJson;
        this.columns = columns;


    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getStoreProcedure() {
        return storeProcedure;
    }

    public void setStoreProcedure(String storeProcedure) {
        this.storeProcedure = storeProcedure;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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
}
