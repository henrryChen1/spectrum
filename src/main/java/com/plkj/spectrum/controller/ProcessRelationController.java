package com.plkj.spectrum.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.plkj.spectrum.bean.ProcessRelation;
import com.plkj.spectrum.bean.SourceDataNode;
import com.plkj.spectrum.service.ProcessRelationService;
import com.plkj.spectrum.service.SourceDataNodeService;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/ProcessRelation")
@CrossOrigin
public class ProcessRelationController {


    @Autowired
    private ProcessRelationService service;
    @Autowired
    private SourceDataNodeService sourceDataNodeService;

    //根据表名进行模糊查询 返回List
    @RequestMapping("/fuzzyQuery")
    public JSONArray fuzzyQuery(String tableName, HttpServletResponse response) {
        return service.queryByFuzzyName(tableName);
    }
    //根据指定的表返回详细信息
    @RequestMapping("/relationQuery")
    public JSONObject relationQuery(String tableName, HttpServletResponse response){
        return  service.queryByName(tableName);
    }
    //重新计算数据
    @RequestMapping("/executeData")
    public JSONObject  executeDate(HttpServletResponse response) {
        JSONObject object =sourceDataNodeService.executeData();
        return  object;
    }
    //返回单个字段的影响表与这个字段影响的表
    @RequestMapping("columnQuery")
    public  JSONObject columnQuery(String tableName, String columnName,HttpServletResponse response){
        return  service.queryByTableAndColumnName(tableName,columnName);
    }

    @RequestMapping(value = "upload",method = RequestMethod.POST)
    public JSONObject upload(@RequestPart("file") MultipartFile file, HttpServletResponse response){
        JSONObject object =sourceDataNodeService.insertExcel(file);
        return  object;
    }
}
