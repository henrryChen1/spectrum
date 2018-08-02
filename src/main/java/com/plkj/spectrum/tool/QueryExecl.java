package com.plkj.spectrum.tool;

import com.plkj.spectrum.bean.ProcessRelation;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
* Change the Excel to Data to insert into hive.
*
* */
public class QueryExecl {
    public static final String SAMPLE_XLSX_FILE_PATH = "/Users/chenwei/Code" +
            "/spectrum/src/main/relationShip.xlsx";

    public static void main(String[] args) throws IOException, InvalidFormatException {
        List<ProcessRelation> processRelationList=excelRead();
//        for(ProcessRelation processRelation:processRelationList){
//            System.out.println(processRelation);
//        }
        System.out.println("总数组长度: "+processRelationList.size());
    }
    public static List<ProcessRelation> excelRead() throws IOException, InvalidFormatException {

        // Creating a Workbook from an Excel file (.xls or .xlsx)
        Workbook workbook = WorkbookFactory.create(new File(SAMPLE_XLSX_FILE_PATH));

        Iterator<Sheet> sheetIterable = workbook.sheetIterator();
        List<RelationObject> list = new ArrayList<>();
        //以表名做索引.
        Map<String,ProcessRelation> relations = new HashMap<>();
        int index = 0;
        while (sheetIterable.hasNext()) {
            Sheet sheet = sheetIterable.next();
            Iterator<Row> rowIterator = sheet.rowIterator();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                ProcessRelation processRelation = new ProcessRelation();
                if (row.getCell(1) != null && row.getRowNum() != 0) {
//                if (row.getCell(1) != null && row.getRowNum() != 0&&row.getRowNum()<3) {

                    String tableName = StringUtils.remove(StringUtils.remove(row.getCell(1).
                            getStringCellValue(), "\n"), "\r");
                    processRelation.setTableName(tableName);
                    if(row.getCell(3) !=null) {
                        String comment = StringUtils.remove(StringUtils.remove(row.getCell(3).
                                getStringCellValue(), "\n"), "\r");
                        processRelation.setComment(comment);
                    }
                    if(row.getCell(2)!=null) {
                        String storeProcedure = StringUtils.remove(StringUtils.remove(row.getCell(2).
                                getStringCellValue(), "\n"), "\r");
                        processRelation.setStoreProcedure(storeProcedure);
                    }

                    Cell cell = row.getCell(5);
                    List<String> sourceTables = Arrays.asList(StringUtils.split(StringUtils.upperCase(StringUtils.
                                    trim(cell.getStringCellValue())),
                            "\r\n"));
                    sourceTables.remove("无");
                    sourceTables.remove("");
                    sourceTables.remove("");
                    sourceTables.remove(" ");
                    if (StringUtils.isNotBlank(tableName)) {
                        list.add(new RelationObject(tableName, sourceTables));
                        relations.put(tableName,processRelation);
                        index++;
                    }
                }
            }
        }
        System.out.println("num: "+index);
//        for (RelationObject RelationObject : list) {
//            String tableName = RelationObject.getTableName();
//            for (RelationObject relationObject1 : list) {
//                if (relationObject1.getSourceTables().contains(
//                        tableName)) {
//                    RelationObject.getAfterTables().add(relationObject1.getTableName());
//                }
//            }
//        }
        Map<String, List<String>> sourceTableRelation = new HashMap<>();
        Map<String, List<String>> afterTableRelation = new HashMap<>();

        for (RelationObject relationObject : list) {
            sourceTableRelation.put(relationObject.getTableName(), relationObject.getSourceTables());
        }
            afterTableRelation = getAfterTableRelationBySourceTableRelation
                    (sourceTableRelation);
        List<ProcessRelation>  processRelationList = new ArrayList<>();
            for(String name :sourceTableRelation.keySet() ) {
                TreeOfRelation sourceRelation = new TreeOfRelation();
                sourceRelation = findSourceTables(name, sourceTableRelation, new TreeOfRelation(),
                        "来源表");
                TreeOfRelation afterRelation = new TreeOfRelation();
                afterRelation = findSourceTables(name, afterTableRelation, new TreeOfRelation(),
                        "影响表");

                ProcessRelation processRelation = relations.get(name);
                processRelation.setAfterTables(afterRelation.getRelyTableNames());
                processRelation.setSourceTables(sourceRelation.getRelyTableNames());
                processRelation.setMapJson(JsonTool.buildJson(afterRelation,sourceRelation).
                        toJSONString());
                processRelationList.add(processRelation);

            }
        return  processRelationList;
    }

    /**
     * 将来源表的映射转化为影响表的映射.
     * @param sourceTableRelation 来源表的映射
     * @return  影响表的映射
     * @Author cw
     * 1.将表名作一个集合(去重)
     * 2.去来源表中寻找
     */
    private static Map<String,List<String>> getAfterTableRelationBySourceTableRelation(
                                    Map<String, List<String>> sourceTableRelation) {
        Set<String> tableNames = new HashSet<>();
        for(Map.Entry<String,List<String>> entry:sourceTableRelation.entrySet()){
            tableNames.add(entry.getKey());
            tableNames.addAll(entry.getValue());
        }
        Map<String,List<String>> afterTableRelation = new HashMap<>();
        for(String tableName:tableNames){
            List<String> afterTables = new ArrayList<>();
            if(afterTableRelation.containsKey(tableName)){
                continue;
                //跳过此次循环.
            }else{
                for(Map.Entry<String,List<String>> entry:sourceTableRelation.entrySet()){
                    if(entry.getValue().contains(tableName)){
                        afterTables.add(entry.getKey());
                    }
                }
               afterTableRelation.put(tableName,afterTables);
            }
        }
     return afterTableRelation;
    }

    /**
     * 递归寻找来源表，影响表
     * 具体逻辑:
     * 1.输入表名,如果是来源表或者影响表  查找他对应的来源表的列表
     * 2.通过来源表的列表 在对象中查询是否有来源表，如果没有就添加上来源表的data与Link
     * 3.如果有就添加上link
     * 4.将来源表在表名列中查询，如果没有结束
     * 5.如果就以来源表名为tableName 重新执行方法 递归调用。
     *
     * @param tableName      需要查询的表名
     * @param tableRelation  excel查询出来的对应关系(单层)
     * @param treeOfRelation 接受结果的图结构.
     * @param value          来源表或者影响表，影响表就输入"影响表",来源表就输入"来源表"
     * @return
     * @throws IOException
     * @throws InvalidFormatException
     */
    private static TreeOfRelation findSourceTables(String tableName, Map<String, List<String>> tableRelation
            , TreeOfRelation treeOfRelation, String value) throws IOException, InvalidFormatException {
        //未初始化
        if (treeOfRelation == null || treeOfRelation.getDatas().isEmpty()) {
            treeOfRelation.init(tableName);
        }
        //1.找到已经有的结果数中的所有表名
        List<String> tableNames = treeOfRelation.getTableNames();
        //2.如果这张表没有下级表，或者这张表在原本的树中已经遍历过.
        if(!tableRelation.containsKey(tableName)||tableRelation.get(tableName).isEmpty()
                ||treeOfRelation.checkTableIsRecursive(tableName)){
            return treeOfRelation;
        }
        if (tableRelation.containsKey(tableName) && !tableRelation.get(tableName).isEmpty()) {
            //3.得到下级表的集合
            List<String> directSourceTables = tableRelation.get(tableName);
            //4.遍历下级表

            for (String sourceTableName : directSourceTables) {
                //5.如果下级表已经在结果数中,只需要添加link
                if (tableName.contains(sourceTableName)) {
                    Link link = new Link();
                    link.setSource(tableNames.indexOf(tableName));
                    link.setTarget(tableNames.indexOf(sourceTableName));
                    link.setValue(value);
                    treeOfRelation.addLink(link);
                } else {
                    //如果不在 就需要添加link与data
                    Data data = null;
                    if (value == "来源表") {
                        data = new Data(1, sourceTableName);
                    } else if (value == "影响表") {
                        data = new Data(0, sourceTableName);
                    }
                    treeOfRelation.addData(data);
                    Link link = new Link();
                    link.setSource(tableNames.indexOf(tableName));
                    link.setTarget(treeOfRelation.getDatas().indexOf(data));
                    link.setValue(value);
                    treeOfRelation.addLink(link);
                }
                //如果来源表还有来源表 递归调用方法
                if (tableRelation.containsKey(sourceTableName) && !tableRelation.get(sourceTableName).isEmpty()) {
                    System.out.println("递归进入:"+sourceTableName);
                    findSourceTables(sourceTableName, tableRelation, treeOfRelation, value);
                }
            }
        }
        return treeOfRelation;
    }


}
