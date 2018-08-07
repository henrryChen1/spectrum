package com.plkj.spectrum.tool;

import java.util.Arrays;

public class Data {
    /**
     * name:表名
     * columns:列的数组
     * hierarchy:这个表所在层.
     */
    private String name;
    private String[] columns;
    private String hierarchy;

    public Data(String name, String[] columns, String hierarchy) {
        this.name = name;
        this.columns = columns;
        this.hierarchy = hierarchy;
    }

    public Data() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getColumns() {
        return columns;
    }

    public void setColumns(String[] columns) {
        this.columns = columns;
    }

    public String getHierarchy() {
        return hierarchy;
    }

    public void setHierarchy(String hierarchy) {
        this.hierarchy = hierarchy;
    }

    @Override
    public String toString() {
        return "Data{" +
                "name='" + name + '\'' +
                ", columns=" + Arrays.toString(columns) +
                ", hierarchy='" + hierarchy + '\'' +
                '}';
    }
}

