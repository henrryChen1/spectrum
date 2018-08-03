package com.plkj.spectrum.tool;

import org.apache.commons.lang.StringUtils;

public class Link {
    private int source;
    private int target;
    private String value;
    private String sourceColumn;
    private String targetColumn;

    public Link(int source, int target, String value, String sourceColumn, String targetColumn) {
        this.source = source;
        this.target = target;
        this.value = value;
        this.sourceColumn = sourceColumn;
        this.targetColumn = targetColumn;
    }

    public Link() {
    }

    @Override
    public String toString() {
        return "Link{" +
                "source=" + source +
                ", target=" + target +
                ", value='" + value + '\'' +
                ", sourceColumn='" + sourceColumn + '\'' +
                ", targetColumn='" + targetColumn + '\'' +
                '}';
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
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
}
