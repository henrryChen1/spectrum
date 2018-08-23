package com.plkj.spectrum.tool;

import java.util.List;

public class Link {
    private int source;
    private int target;
    private int value;
    private String souceTable;
    private String targetTable;
    private String sourceColumn;
    private String targetColumn;


    @Override
    public String toString() {
        return "Link{" +
                "source=" + source +
                ", target=" + target +
                ", value=" + value +
                ", souceTable='" + souceTable + '\'' +
                ", targetTable='" + targetTable + '\'' +
                ", sourceColumn='" + sourceColumn + '\'' +
                ", targetColumn='" + targetColumn + '\'' +
                '}';
    }
    public Link(int value, String souceTable, String targetTable, String sourceColumn, String targetColumn) {

        this.value = value;
        this.souceTable = souceTable;
        this.targetTable = targetTable;
        this.sourceColumn = sourceColumn;
        this.targetColumn = targetColumn;
    }

    public Link(int value, String souceTable, String targetTable) {
        this.value = value;
        this.souceTable = souceTable;
        this.targetTable = targetTable;
    }

    public void setSourceColumn(String sourceColumn) {
        this.sourceColumn = sourceColumn;
    }

    public void setTargetColumn(String targetColumn) {
        this.targetColumn = targetColumn;
    }

    public String getSourceColumn() {

        return sourceColumn;
    }

    public String getTargetColumn() {
        return targetColumn;
    }



    public Link() {
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getSouceTable() {
        return souceTable;
    }

    public void setSouceTable(String souceTable) {
        this.souceTable = souceTable;
    }

    public String getTargetTable() {
        return targetTable;
    }

    public void setTargetTable(String targetTable) {
        this.targetTable = targetTable;
    }

    public void setSource(List<Node> nodeList){
        for(Node node:nodeList){
            if(node.getName().equalsIgnoreCase(this.souceTable)){
                this.setSource(nodeList.indexOf(node));
                return;
            }
        }
    }


    public void setTarget(List<Node> nodeList){
        for(Node node:nodeList){
            if(node.getName().equalsIgnoreCase(this.targetTable)){
                this.setTarget(nodeList.indexOf(node));
                return;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Link link = (Link) o;

        if (!souceTable.equals(link.souceTable)) return false;
        if (!targetTable.equals(link.targetTable)) return false;
        if (!sourceColumn.equals(link.sourceColumn)) return false;
        return targetColumn.equals(link.targetColumn);
    }

    @Override
    public int hashCode() {
        int result = souceTable.hashCode();
        result = 31 * result + targetTable.hashCode();
        result = 31 * result + sourceColumn.hashCode();
        result = 31 * result + targetColumn.hashCode();
        return result;
    }
}
