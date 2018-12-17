package com.plkj.spectrum.bean;

import java.io.Serializable;
import java.util.Objects;

public class SourceDataNode implements Serializable{
    private String targetTableName;

    private String sourceTableName;


    private static final long serialVersionUID = 1L;

    public SourceDataNode(String targetTableName, String sourceTableName) {
        this.targetTableName = targetTableName;
        this.sourceTableName = sourceTableName;
    }

    public String getTargetTableName() {
        return targetTableName;
    }

    public void setTargetTableName(String targetTableName) {
        this.targetTableName = targetTableName;
    }

    public String getSourceTableName() {
        return sourceTableName;
    }

    public void setSourceTableName(String sourceTableName) {
        this.sourceTableName = sourceTableName;
    }

    @Override
    public String toString() {
        return "SourceDataNode{" +
                "targetTableName='" + targetTableName + '\'' +
                ", sourceTableName='" + sourceTableName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SourceDataNode that = (SourceDataNode) o;
        return Objects.equals(targetTableName, that.targetTableName) &&
                Objects.equals(sourceTableName, that.sourceTableName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetTableName, sourceTableName);
    }
}
