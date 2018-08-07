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

/**
 * Change the Excel to Data to insert into hive.
 *
 * @Author cw
 * 思路:1.读取excel 每一行  生成一个link与 dataList
 * 2.去查询每一个dataList的表名 然后依次查询结果.
 */
public class QueryExecl {
    public static final String SAMPLE_XLSX_FILE_PATH = "/Users/chenwei/Code" +
            "/spectrum/src/main/relationShip.xlsx";

//    public static void main(String[] args) throws IOException, InvalidFormatException {
//        List<ProcessRelation> processRelationList = excelRead();
//        for (ProcessRelation processRelation : processRelationList) {
//            System.out.println(processRelation);
//        }
//        System.out.println("总数组长度: " + processRelationList.size());
//    }

//    public static List<ProcessRelation> excelRead() throws IOException, InvalidFormatException {
//
//        // Creating a Workbook from an Excel file (.xls or .xlsx)
//        Workbook workbook = WorkbookFactory.create(new File(SAMPLE_XLSX_FILE_PATH));
//
//        Iterator<Sheet> sheetIterable = workbook.sheetIterator();
//        List<RelationObject> list = new ArrayList<>();
//        //以表名做索引.
//        Map<String, ProcessRelation> relations = new HashMap<>();
//        int index = 0;
//        while (sheetIterable.hasNext()) {
//            Sheet sheet = sheetIterable.next();
//            Iterator<Row> rowIterator = sheet.rowIterator();
//            while (rowIterator.hasNext()) {
//                Row row = rowIterator.next();
//                ProcessRelation processRelation = new ProcessRelation();
//                if (row.getCell(1) != null && row.getRowNum() != 0) {
//                    String tableName = StringUtils.remove(StringUtils.remove(row.getCell(1).
//                            getStringCellValue(), "\n"), "\r");
//                    processRelation.setTableName(tableName);
//                    if (row.getCell(3) != null) {
//                        String comment = StringUtils.remove(StringUtils.remove(row.getCell(3).
//                                getStringCellValue(), "\n"), "\r");
//                        processRelation.setComment(comment);
//                    }
//                    if (row.getCell(2) != null) {
//                        String storeProcedure = StringUtils.remove(StringUtils.remove(row.getCell(2).
//                                getStringCellValue(), "\n"), "\r");
//                        processRelation.setStoreProcedure(storeProcedure);
//                    }
//
//                    Cell cell = row.getCell(5);
//                    List<String> sourceTables = Arrays.asList(StringUtils.split(StringUtils.upperCase(StringUtils.
//                                    trim(cell.getStringCellValue())),
//                            "\r\n"));
//                    sourceTables.remove("无");
//                    sourceTables.remove("");
//                    sourceTables.remove("");
//                    sourceTables.remove(" ");
//                    if (StringUtils.isNotBlank(tableName)) {
//                        list.add(new RelationObject(tableName, sourceTables));
//                        relations.put(tableName, processRelation);
//                        index++;
//                    }
//                }
//            }
//        }
//        System.out.println("num: " + index);
//        Map<String, List<String>> sourceTableRelation = new HashMap<>();
//        Map<String, List<String>> afterTableRelation = new HashMap<>();
//
//        for (RelationObject relationObject : list) {
//            sourceTableRelation.put(relationObject.getTableName(), relationObject.getSourceTables());
//        }
//        afterTableRelation = getAfterTableRelationBySourceTableRelation
//                (sourceTableRelation);
//        List<ProcessRelation> processRelationList = new ArrayList<>();
//        for (String name : sourceTableRelation.keySet()) {
//            TreeOfRelation sourceRelation = new TreeOfRelation();
//            sourceRelation = findSourceTables(name, sourceTableRelation, new TreeOfRelation(),
//                    "来源表");
//            TreeOfRelation afterRelation = new TreeOfRelation();
//            afterRelation = findSourceTables(name, afterTableRelation, new TreeOfRelation(),
//                    "影响表");
//
//            ProcessRelation processRelation = relations.get(name);
//            processRelation.setAfterTables(afterRelation.getRelyTableNames());
//            processRelation.setSourceTables(sourceRelation.getRelyTableNames());
//            processRelation.setMapJson(JsonTool.buildJson(afterRelation, sourceRelation).
//                    toJSONString());
//            processRelationList.add(processRelation);
//
//        }
//        return processRelationList;
//    }
    public static List<ProcessRelation> excelRead() throws IOException, InvalidFormatException {

    return null;
    }

    /**
     * @param tableName      表名
     * @param dataList       所有的Data
     * @param linkList       所有的Link
     * @param treeOfRelation 封装结果
     * @param value          1代表从source到target寻找影响表  0代表从target到source寻找来源表
     * @return
     */
    public static TreeOfRelation mainComupting(String tableName, List<Data> dataList,
                                               List<Link> linkList, TreeOfRelation treeOfRelation, int value) {


        if (treeOfRelation.containTable(tableName)) {
            return treeOfRelation;
        }

        for (Data data : dataList) {
            if (data.getName() == tableName) {
                treeOfRelation.addData(data);
                break;
            }
        }
             //1 > 从 source到target
        if (value == 1) {
            for (Link link : linkList) {
                if (link.getSourceTable().equalsIgnoreCase(tableName)) {
                    treeOfRelation.addLink(link);
                    String targetTableName = link.getTargetTable();
                    inner:
                    for (Data data : dataList) {
                        if (data.getName().equalsIgnoreCase(targetTableName) &&
                                treeOfRelation.checkTableIsRecursive(targetTableName)) {
                            treeOfRelation = mainComupting(targetTableName, dataList, linkList, treeOfRelation, value);
                            break inner;
                        }
                    }
                }
            }
            //0 > 从 target到source
        } else if (value == 0) {
            for (Link link : linkList) {
                if (link.getTargetTable().equalsIgnoreCase(tableName)) {
                    treeOfRelation.addLink(link);
                    String sourceTableName = link.getSourceTable();
                    inner:
                    for (Data data : dataList) {
                        if (data.getName().equalsIgnoreCase(sourceTableName) &&
                                treeOfRelation.checkTableIsRecursive(sourceTableName)) {
                            treeOfRelation = mainComupting(sourceTableName, dataList, linkList, treeOfRelation, value);
                            break inner;
                        }
                    }

                }
            }
        }
        return treeOfRelation;
    }
}
