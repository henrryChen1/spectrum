package com.plkj.spectrum.tool;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.plkj.spectrum.bean.ProcessRelation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JsonTool {
    public static JSONObject buildJson(TreeOfRelation afterTree, TreeOfRelation sourceTree) {
        //fix
        List<Node> nodeList = new ArrayList<>();
        List<Link> linkList = new ArrayList<>();
        nodeList.addAll(afterTree.getNodes());
        if ((sourceTree.getNodes().size()) != 0) {
            sourceTree.getNodes().remove(0);
        }
        nodeList.addAll(sourceTree.getNodes());
        if (afterTree.getLinks() != null) {
            for (Link link : afterTree.getLinks()) {
                if (!link.getSouceTable().equalsIgnoreCase(link.getTargetTable())) {
                    for (Node node : nodeList) {
                        if (link.getSouceTable().equalsIgnoreCase(node.getName())) {
                            link.setSource(nodeList.indexOf(node));
                        }
                        if (link.getTargetTable().equalsIgnoreCase(node.getName())) {
                            link.setTarget(nodeList.indexOf(node));
                        }
                    }
                    linkList.add(link);
                }
            }
        }
        if (sourceTree.getLinks() != null) {
            for (Link link : sourceTree.getLinks()) {
                if (!link.getSouceTable().equalsIgnoreCase(link.getTargetTable())) {
                    for (Node node : nodeList) {
                        if (link.getSouceTable().equalsIgnoreCase(node.getName())) {
                            link.setSource(nodeList.indexOf(node));
                        }
                        if (link.getTargetTable().equalsIgnoreCase(node.getName())) {
                            link.setTarget(nodeList.indexOf(node));
                        }
                    }
                    linkList.add(link);
                }
            }
        }
        JSONArray nodeArray = new JSONArray();
        nodeArray.addAll(nodeList);
        JSONArray linkArray = new JSONArray();
        linkArray.addAll(linkList);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Links", linkArray);
        jsonObject.put("Datas", nodeArray);

        return jsonObject;
    }

    public static String dealString(String str) {
        if (str == null) {
            return "";
        }

        return str.replaceAll("[^a-zA-z0-9():_\\u4e00-\\u9fa5]", "");
    }

    public static String revertString(String str) {
        if (str == null) {
            return "";
        }
        return str.replaceAll("'", "\"").replaceAll("&&", ",");

    }

    public static JSONObject getJson(ProcessRelation relation) {
        if (relation != null) {
            relation.setAfterTables(JsonTool.revertString(relation.getAfterTables()));
            relation.setMapJson(JsonTool.revertString(relation.getMapJson()));
            relation.setSourceTables(JsonTool.revertString(relation.getSourceTables()));
            relation.setAfterTables(JsonTool.revertString(relation.getAfterTables()));
            relation.setColumns(JsonTool.revertString(relation.getColumns()));
            relation.setComment(JsonTool.revertString(relation.getComment()));

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("tableName", relation.getTableName());
            jsonObject.put("storeProcedure", relation.getStoreProcedure());
            jsonObject.put("comment", relation.getComment());
            jsonObject.put("columns", JSONArray.parse(relation.getColumns()));
            jsonObject.put("sourceTables", JSONArray.parse(relation.getSourceTables()));
            jsonObject.put("afterTables", JSONArray.parse(relation.getAfterTables()));
            jsonObject.put("mapJson", JSONObject.parseObject(relation.getMapJson()));

            return jsonObject;
        }
        return new JSONObject();
    }
}
