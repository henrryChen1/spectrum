package com.plkj.spectrum.tool;

public class Link {
    private int source;
    private int target;
    private String value;

    public Link(int source, int target, String value) {
        this.source = source;
        this.target = target;
        this.value = value;
    }

    public Link(    ) {
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

    @Override
    public String toString() {
        return "Link{" +
                "source=" + source +
                ", target=" + target +
                ", value='" + value + '\'' +
                '}';
    }
}
