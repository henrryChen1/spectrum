package com.plkj.spectrum.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.plkj.spectrum.bean.ProcessRelation;
import com.plkj.spectrum.service.ProcessRelationService;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/ProcessRelation")
public class ProcessRelationController {

    private static final Logger logger = LoggerFactory.getLogger(ProcessRelationController.class);

    @Autowired
    private ProcessRelationService service;

    //根据表名进行模糊查询 返回List
    @RequestMapping("/fuzzyQuery")
    public JSONArray fuzzyQuery(String tableName, HttpServletResponse response) {
        response.addHeader("Access-Control-Allow-Origin", "http://localhost:8020");
        return service.queryByFuzzyName(tableName);
    }
    //根据指定的表返回详细信息
    @RequestMapping("/relationQuery")
    public JSONObject relationQuery(String tableName, HttpServletResponse response){
        response.addHeader("Access-Control-Allow-Origin", "http://localhost:8020");

        return  service.queryByName(tableName);
    }
}
