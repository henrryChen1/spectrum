package com.plkj.spectrum.tool;

import java.util.ArrayList;
import java.util.List;

public class TreeOfRelation {
    private List<Link> Links;
    private List<Data> Datas;

    @Override
    public String toString() {
        return "TreeOfRelation{" +
                "Links=" + Links +
                ", Datas=" + Datas +
                '}';
    }

    public TreeOfRelation(List<Link> Links, List<Data> Datas) {
        this.Links = Links;
        this.Datas = Datas;
    }

    public TreeOfRelation() {
        this.Links = new ArrayList<>();
        this.Datas = new ArrayList<>();

    }

    public List<Link> getLinks() {
        return Links;
    }

    public void setLinks(List<Link> Links) {
        this.Links = Links;
    }

    public List<Data> getDatas() {
        return Datas;
    }

    public void setDatas(List<Data> datas) {
        this.Datas = datas;
    }

    public void addData(Data data) {
        this.Datas.add(data);
    }

    public void addLink(Link link) {
        this.Links.add(link);
    }

    public List<String> getTableNames() {
        List<String> tableNames = new ArrayList<>();
        for (Data data : Datas) {
            tableNames.add(data.getName());
        }
        return tableNames;
    }


    public String getRelyTableNames() {
        List<String> names = new ArrayList<>();
        for (Data data : Datas) {
            names.add(data.getName());
        }
        return names.toString();
    }

    //判断一个表是否需要需要进入递归操作
    public boolean checkTableIsRecursive(String tableName) {
        //找到是否有相同表名的data(已经被存入)
        for (Data data : Datas) {
            if (data.getName() == tableName || data.getName().equalsIgnoreCase(tableName)) {
                return false;
            }
        }
        //寻找是否有关于他自己的source/防止死循环
        for (Link link : Links) {
            if (link.getSourceTable() == tableName) {
                return false;
            }
        }
        return true;
    }

    public boolean containTable(String tableName) {
        for (Data data : Datas) {
            if (data.getName() == tableName || data.getName().equalsIgnoreCase(tableName)) {
                return true;
            }
        }
        return false;
    }

}
