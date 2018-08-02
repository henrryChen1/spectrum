package com.plkj.spectrum.tool;

public class Data {
    private boolean draggable = true;
    private int category;
    private String name;

    @Override
    public String toString() {
        return "Data{" +
                "draggable=" + draggable +
                ", category=" + category +
                ", name='" + name + '\'' +
                '}';
    }

    public Data(int category, String name) {
        this.category = category;
        this.name = name;
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

    public Data() {
    }

}
