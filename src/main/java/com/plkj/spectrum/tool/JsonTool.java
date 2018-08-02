package com.plkj.spectrum.tool;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.avro.data.Json;

import java.util.List;

public class JsonTool {
    public static JSONObject init() {
        JSONObject jsonObject = new JSONObject();
        JSONObject title = new JSONObject();
        JSONObject tooltip = new JSONObject();
        JSONObject label = new JSONObject();
        JSONObject normal = new JSONObject();
        JSONObject textstyle = new JSONObject();


        title.put("text", "");
        jsonObject.put("title", title);
        jsonObject.put("tooltip", tooltip);
        jsonObject.put("animationDurationUpdate", 1500);
        jsonObject.put("animationEasingUpdate", "quinticInOut");
        normal.put("show", true);
        textstyle.put("fontSize", 12);
        normal.put("textStyle", textstyle);
        label.put("normal", normal);
        jsonObject.put("label", label);

        return jsonObject;
    }

    public static JSONObject buildJson(TreeOfRelation afterTree, TreeOfRelation sourceTree) {
        JSONObject jsonObject = JsonTool.init();
        JSONArray data = new JSONArray();
        JSONObject distinctdata = new JSONObject();
        JSONArray links = new JSONArray();
        JSONArray series = new JSONArray();
        JSONObject serie = new JSONObject();
        JSONArray categories = new JSONArray();
        JSONObject categorie = new JSONObject();
        JSONObject itemStyle = new JSONObject();
        JSONObject force = new JSONObject();
        JSONObject edgeLabel = new JSONObject();

        JSONObject linkStyle = new JSONObject();
        //获得对象数据
        List<Data> afterNodes = afterTree.getDatas();
        List<Data> sourceNodes = sourceTree.getDatas();
        List<Link> afterLinks = afterTree.getLinks();
        List<Link> sourceLinks = sourceTree.getLinks();
        //
        distinctdata.put("name", sourceNodes.get(0).getName());
        distinctdata.put("draggable", true);
        data.add(distinctdata);
        //把对象表从对应的数组中删除
        afterNodes.remove(0);
        sourceNodes.remove(0);

        for (Data node : sourceNodes) {

            distinctdata = new JSONObject();
            distinctdata.put("name", node.getName());
            distinctdata.put("draggable", true);
            distinctdata.put("category", node.getCategory());
            data.add(distinctdata);
        }
        for (Data node : afterNodes) {
            distinctdata = new JSONObject();
            distinctdata.put("name", node.getName());
            distinctdata.put("draggable", true);
            distinctdata.put("category", node.getCategory());
            data.add(distinctdata);
        }

        serie.put("type", "graph");
        serie.put("layout", "force");
        serie.put("symbolSize", 45);
        serie.put("focusNodeAdjacency", false);
        serie.put("draggable", false);
        //categories
        categorie.put("name", "父级节点");
        JSONObject normal = new JSONObject();
        normal.put("color", "#009800");
        itemStyle.put("normal", normal);
        categorie.put("itemStyle", itemStyle);
        categories.add(categorie);
        categorie = new JSONObject();
        normal = new JSONObject();
        itemStyle = new JSONObject();
        normal.put("color", "#4592FF");
        itemStyle.put("normal", normal);
        categorie.put("itemStyle", itemStyle);
        categorie.put("name", "子集节点");
        categories.add(categorie);
        serie.put("categories", categories);
        normal = new JSONObject();
        JSONObject label = new JSONObject();
        JSONObject textstyle = new JSONObject();
        textstyle.put("fontSize", 12);
        normal.put("show", true);
        normal.put("textStyle", textstyle);
        label.put("normal", normal);
        serie.put("label", label);
        force.put("repulsion", 1000);
        serie.put("force", force);
        serie.put("edgeSymbolSize", new int[]{4, 50});
        textstyle = new JSONObject();
        normal = new JSONObject();
        textstyle.put("fontSize", 10);
        normal.put("show", true);
        normal.put("textStyle", textstyle);
        normal.put("formatter", "{c}");
        edgeLabel.put("normal", normal);
        serie.put("edgeLabel", edgeLabel);
        normal = new JSONObject();
        normal.put("opacity", 0.9);
        normal.put("width", 1);
        normal.put("curveness", 0);
        linkStyle.put("normal", normal);
        serie.put("lineStyle", linkStyle);
        serie.put("Data", data);

        //数组合并 节点坐标需要相加.
        int sourceNum = sourceLinks.size() - 1;
        for (Link link : sourceLinks) {
            JSONObject linkJson = new JSONObject();
            linkJson.put("source", link.getSource());
            linkJson.put("target", link.getValue());
            linkJson.put("value", link.getValue());
            links.add(link);
        }
        for (Link link : afterLinks) {
            JSONObject linkJson = new JSONObject();
            if (link.getSource() != 0) {
                link.setSource(link.getSource() + sourceNum - 1);
            }
            if (link.getTarget() != 0) {
                link.setTarget(link.getTarget() + sourceNum - 1);
            }
            linkJson.put("source", link.getSource());
            linkJson.put("target", link.getValue());
            linkJson.put("value", link.getValue());
            links.add(link);
        }

        serie.put("links", links);
        series.add(serie);
        jsonObject.put("series", series);
        return jsonObject;
    }
}
