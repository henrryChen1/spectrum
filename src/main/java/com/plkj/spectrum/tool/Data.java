package com.plkj.spectrum.tool;

import java.util.Arrays;

public class Data {
    private int category;
    private String name;
    private String[] columns;
    private String hierarchy;

    public Data(int category, String name, String[] columns, String hierarchy) {
        this.category = category;
        this.name = name;
        this.columns = columns;
        this.hierarchy = hierarchy;
    }

    public Data() {
    }

    @Override
    public String toString() {
        return "Data{" +
                "category=" + category +
                ", name='" + name + '\'' +
                ", columns=" + Arrays.toString(columns) +
                ", hierarchy='" + hierarchy + '\'' +
                '}';
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
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
}
