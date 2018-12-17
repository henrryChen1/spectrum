package com.plkj.spectrum.tool;

import java.util.List;
import java.util.Objects;

public class Link {
    private int source;//来源表在数组列表中的index
    private int target;//影响表在数组中的index
    private int value;//方向0代表从来源指向影响  1代表从影响指向来源
    private String souceTable;//来源表名
    private String targetTable;//目标表明

    @Override
    public String toString() {
        return "Link{" +
                "source=" + source +
                ", target=" + target +
                ", value=" + value +
                ", souceTable='" + souceTable + '\'' +
                ", targetTable='" + targetTable + '\'' +
                '}';
    }
    public Link(int value, String souceTable, String targetTable) {

        this.value = value;
        this.souceTable = souceTable;
        this.targetTable = targetTable;
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
        return value == link.value &&
                Objects.equals(souceTable, link.souceTable) &&
                Objects.equals(targetTable, link.targetTable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, souceTable, targetTable);
    }
}
