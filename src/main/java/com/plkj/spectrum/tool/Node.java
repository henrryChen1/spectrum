package com.plkj.spectrum.tool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Node {
    private String name;
    private List<String> colums;
//    private String comment;
//    private String hierarchy;
    private String comment;
    private String storeProcedure;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Node(String name, List<String> colums, String comment) {

        this.name = name;
        this.colums = colums;
        this.comment = comment;
    }

    public Node() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getColums() {
        return colums;
    }

    public void setColums(List<String> colums) {
        this.colums = colums;
    }


    public  void addColumns(String column){
        if(this.colums==null){
            setColums(new ArrayList<>());
        }
        this.colums.add(column);
    }

    @Override
    public String toString() {
        return "Node{" +
                "name='" + name + '\'' +
                ", colums=" + colums +
                ", comment='" + comment + '\'' +
                ", storeProcedure='" + storeProcedure + '\'' +
                '}';
    }

    public String getStoreProcedure() {
        return storeProcedure;
    }

    public void setStoreProcedure(String storeProcedure) {
        this.storeProcedure = storeProcedure;
    }

    public Node(String name, List<String> colums, String comment, String storeProcedure) {

        this.name = name;
        this.colums = colums;
        this.comment = comment;
        this.storeProcedure = storeProcedure;
    }
}
