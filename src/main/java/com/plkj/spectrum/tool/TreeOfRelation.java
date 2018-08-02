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

    public void init(String tableName) {
        Data data = new Data();
        data.setName(tableName);
        data.setCategory(-1);
        addData(data);
    }

    public String getRelyTableNames() {
        List<String> names = new ArrayList<>();
        for (Data data : Datas) {
            names.add(data.getName());
        }
        return names.toString();
    }

    public boolean checkTableIsRecursive(String tableName) {
        int index = -9999;
        //找到是否有相同表名的data(已经被存入)
        for (Data data : Datas) {
            if (data.getName() == tableName || data.getName().equalsIgnoreCase(tableName)) {
                index = Datas.indexOf(data);
            }
        }
        //寻找是否有关于他自己的source
        for (Link link : Links) {
            if (link.getSource() - index == 0) {
                return false;
            }
        }
        return true;
    }
}
