package com.plkj.spectrum.tool;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.avro.data.Json;

import java.util.ArrayList;
import java.util.List;

public class JsonTool {


    public static JSONObject buildJson(TreeOfRelation afterTree, TreeOfRelation sourceTree) {
            List<Data> datas = new ArrayList<>();
            List<Link> links = new ArrayList<>();
            datas.addAll(afterTree.getDatas());
            datas.addAll(sourceTree.getDatas());

            links.addAll(sourceTree.getLinks());
            links.addAll(afterTree.getLinks());
            JSONObject jsonObject = new JSONObject();
            JSONArray linkArray = new JSONArray();
            JSONArray dataArray = new JSONArray();

            dataArray.addAll(datas);
            linkArray.addAll(links);
            jsonObject.put("Datas",dataArray);
            jsonObject.put("Links",linkArray);
            return jsonObject;

    }
}
