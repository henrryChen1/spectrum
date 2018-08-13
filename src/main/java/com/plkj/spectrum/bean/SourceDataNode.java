package com.plkj.spectrum.bean;

public class SourceDataNode {
    private String targetTableName;
    private String targetTableComment;
    private String targetColumnName;
    private String targetColumnComment;
    private String sourceTableName;
    private String sourceTableComment;
    private String sourceColumnName;
    private String sourceColumnComment;
    private String storeProcedure;

    public SourceDataNode(String targetTableName, String targetTableComment, String targetColumnName,
                          String targetColumnComment, String sourceTableName, String sourceTableComment,
                          String sourceColumnName, String sourceColumnComment, String storeProcedure) {
        this.targetTableName = targetTableName;
        this.targetTableComment = targetTableComment;
        this.targetColumnName = targetColumnName;
        this.targetColumnComment = targetColumnComment;
        this.sourceTableName = sourceTableName;
        this.sourceTableComment = sourceTableComment;
        this.sourceColumnName = sourceColumnName;
        this.sourceColumnComment = sourceColumnComment;
        this.storeProcedure = storeProcedure;
    }

    public SourceDataNode() {
    }

    public String getTargetTableName() {
        return targetTableName;
    }

    public void setTargetTableName(String targetTableName) {
        this.targetTableName = targetTableName;
    }

    public String getTargetTableComment() {
        return targetTableComment;
    }

    public void setTargetTableComment(String targetTableComment) {
        this.targetTableComment = targetTableComment;
    }

    public String getTargetColumnName() {
        return targetColumnName;
    }

    public void setTargetColumnName(String targetColumnName) {
        this.targetColumnName = targetColumnName;
    }

    public String getTargetColumnComment() {
        return targetColumnComment;
    }

    public void setTargetColumnComment(String targetColumnComment) {
        this.targetColumnComment = targetColumnComment;
    }

    public String getSourceTableName() {
        return sourceTableName;
    }

    public void setSourceTableName(String sourceTableName) {
        this.sourceTableName = sourceTableName;
    }

    public String getSourceTableComment() {
        return sourceTableComment;
    }

    public void setSourceTableComment(String sourceTableComment) {
        this.sourceTableComment = sourceTableComment;
    }

    public String getSourceColumnName() {
        return sourceColumnName;
    }

    public void setSourceColumnName(String sourceColumnName) {
        this.sourceColumnName = sourceColumnName;
    }

    public String getSourceColumnComment() {
        return sourceColumnComment;
    }

    public void setSourceColumnComment(String sourceColumnComment) {
        this.sourceColumnComment = sourceColumnComment;
    }

    public String getStoreProcedure() {
        return storeProcedure;
    }

    public void setStoreProcedure(String storeProcedure) {
        this.storeProcedure = storeProcedure;
    }

    @Override
    public String toString() {
        return "SourceDataNode{" +
                "targetTableName='" + targetTableName + '\'' +
                ", targetTableComment='" + targetTableComment + '\'' +
                ", targetColumnName='" + targetColumnName + '\'' +
                ", targetColumnComment='" + targetColumnComment + '\'' +
                ", sourceTableName='" + sourceTableName + '\'' +
                ", sourceTableComment='" + sourceTableComment + '\'' +
                ", sourceColumnName='" + sourceColumnName + '\'' +
                ", sourceColumnComment='" + sourceColumnComment + '\'' +
                ", storeProcedure='" + storeProcedure + '\'' +
                '}';
    }
}
