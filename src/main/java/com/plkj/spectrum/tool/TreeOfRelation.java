package com.plkj.spectrum.tool;

import java.util.ArrayList;
import java.util.List;

public class TreeOfRelation {
    private List<Link> links;
    private List<Node> nodes;

    public TreeOfRelation(List<Link> links, List<Node> nodes) {
        this.links = links;
        this.nodes = nodes;
    }

    public void init() {
        this.links = new ArrayList<>();
        this.nodes = new ArrayList<>();
    }

    public TreeOfRelation() {
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public void addNode(Node node) {
        if (this.nodes != null) {
            this.nodes.add(node);
        }
    }

    public void addLink(Link link) {
        if(this.links==null){
            this.links=new ArrayList<>();
        }
        this.links.add(link);
    }

    @Override
    public String toString() {
        return "TreeOfRelation{" +
                "links=" + links +
                ", nodes=" + nodes +
                '}';
    }

    public List<String> getTableName() {
        List<String> list = new ArrayList<>();
        for (Node node : this.nodes) {
            if(nodes.indexOf(node)!=0) {
                list.add(node.getName());
            }
        }
        return list;
    }

    public boolean containTable(String tableName) {
        if(this.nodes==null){
            this.nodes=new ArrayList<>();
        }
        for (Node node : this.nodes) {
            if (node.getName().equalsIgnoreCase(tableName)) {
                return true;
            }
        }
        return false;
    }


}
