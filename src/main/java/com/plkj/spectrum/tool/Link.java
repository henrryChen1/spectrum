package com.plkj.spectrum.tool;

import java.util.List;

public class Link {
    private String sourceTable;
    private String targetTable;
    private int value; //方向
    private String sourceColumn;
    private String targetColumn;

    public Link() {
    }

    public Link(String source, String target, int value, String sourceColumn, String targetColumn) {
        this.sourceTable = source;
        this.targetTable = target;
        this.value = value;
        this.sourceColumn = sourceColumn;
        this.targetColumn = targetColumn;
    }

    public String getSourceTable() {
        return sourceTable;
    }

    public void setSourceTable(String sourceTable) {
        this.sourceTable = sourceTable;
    }

    public String getTargetTable() {
        return targetTable;
    }

    public void setTargetTable(String targetTable) {
        this.targetTable = targetTable;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getSourceColumn() {
        return sourceColumn;
    }

    public void setSourceColumn(String sourceColumn) {
        this.sourceColumn = sourceColumn;
    }

    public String getTargetColumn() {
        return targetColumn;
    }

    public void setTargetColumn(String targetColumn) {
        this.targetColumn = targetColumn;
    }

    @Override
    public String toString() {
        return "Link{" +
                "sourceTable='" + sourceTable + '\'' +
                ", targetTable='" + targetTable + '\'' +
                ", value=" + value +
                ", sourceColumn='" + sourceColumn + '\'' +
                ", targetColumn='" + targetColumn + '\'' +
                '}';
    }

    //将表名转化为坐标.
    public void toJsonLink(List<Data> dataList){
        for(Data data: dataList){
            if(data.getName().equalsIgnoreCase(sourceTable)){
                this.setSourceTable(String.valueOf(dataList.indexOf(data)));
            }
            if(data.getName().equalsIgnoreCase(targetColumn)){
                this.setTargetTable(String.valueOf(dataList.indexOf(data)));
            }
        }
    }
}
