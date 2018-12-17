package com.plkj.spectrum.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.plkj.spectrum.bean.ProcessRelation;
import com.plkj.spectrum.bean.SourceDataNode;
import com.plkj.spectrum.service.ProcessRelationService;
import com.plkj.spectrum.service.SourceDataNodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
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
@Api("血缘关系")
@CrossOrigin
public class ProcessRelationController {


    @Autowired
    private ProcessRelationService service;
    @Autowired
    private SourceDataNodeService sourceDataNodeService;

    //根据表名进行模糊查询 返回List
    @RequestMapping(value = "/fuzzyQuery",method = RequestMethod.GET)
    @ApiOperation("根据表名进行模糊查询 返回表名列表")
    @ApiImplicitParam(name = "tableName",value = "表名关键字",dataType = "String")
    public JSONArray fuzzyQuery(String tableName) {
        return service.queryByFuzzyName(tableName);
    }
    //根据指定的表返回详细信息
    @RequestMapping(value = "/relationQuery",method = RequestMethod.GET)
    @ApiOperation("根据指定的表返回详细信息")
    @ApiImplicitParam(name = "tableName",value = "精确表名",dataType = "String")
    public JSONObject relationQuery(String tableName){
        return  service.queryByName(tableName);
    }
    //重新计算数据
    @RequestMapping(value = "/executeData",method = RequestMethod.GET)
    @ApiOperation("重新计算数据")
    public JSONObject  executeDate() {
        JSONObject object =sourceDataNodeService.executeData();
        return  object;
    }
    //返回单个字段的影响表与这个字段影响的表
    @RequestMapping(value = "columnQuery",method = RequestMethod.GET)
    @ApiOperation("返回单个字段的影响表与这个字段影响的表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tableName",value = "精确表名",dataType = "String"),
            @ApiImplicitParam(name = "columnName",value = "精确字段名",dataType = "String")
    })
    public  JSONObject columnQuery(String tableName, String columnName){
        return  service.queryByTableAndColumnName(tableName,columnName);
    }
    //上传Excel
    @RequestMapping(value = "uploadexcel",method = RequestMethod.POST)
    @ApiOperation("上传Excel生成数据")
    @ApiImplicitParam(name = "file",value = "excel文件名",dataType = "MultipartFile")
    public JSONObject uploadexcel(@RequestPart("file") MultipartFile file){
        JSONObject object =sourceDataNodeService.insertExcel(file);
        return  object;
    }
}
