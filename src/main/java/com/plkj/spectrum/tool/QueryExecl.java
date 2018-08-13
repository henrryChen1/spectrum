package com.plkj.spectrum.tool;

import com.alibaba.fastjson.JSONArray;
import com.plkj.spectrum.bean.ProcessRelation;
import com.plkj.spectrum.bean.SourceDataNode;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/*
* Change the Excel to Node to insert into hive.
*
* */
public class QueryExecl {
    public static final String SAMPLE_XLSX_FILE_PATH = "/Users/chenwei/Code" +
            "/spectrum/src/main/test1.xlsx";

    public static void main(String[] args) throws IOException, InvalidFormatException {
        List<ProcessRelation> processRelationList = excelRead();
    }

    public static List<ProcessRelation> excelRead() throws IOException, InvalidFormatException {

        // Creating a Workbook from an Excel file (.xls or .xlsx)
        Workbook workbook = WorkbookFactory.create(new File(SAMPLE_XLSX_FILE_PATH));

        Iterator<Sheet> sheetIterable = workbook.sheetIterator();
        List<RelationObject> list = new ArrayList<>();

        List<Node> nodeList = new ArrayList<>();
        List<Link> linkList = new LinkedList<>();
        while (sheetIterable.hasNext()) {
            Sheet sheet = sheetIterable.next();
            Iterator<Row> rowIterator = sheet.rowIterator();
            List<CellRangeAddress> crs = sheet.getMergedRegions();
            for (CellRangeAddress cr : crs) {
                if (cr.getFirstColumn() == 0 || cr.getFirstColumn() == 5) {
                    int rowIndex = cr.getFirstRow();
                    int lastRow = cr.getLastRow();
                    int cell = cr.getFirstColumn();
                    Node node = new Node();
                    node.setName(StringUtils.trim(sheet.getRow(rowIndex).getCell(cell).getStringCellValue())
                            .toUpperCase());
                    node.setComment(StringUtils.trim(sheet.getRow(rowIndex).getCell(cell + 1).getStringCellValue())
                            .toUpperCase());
                    if (cr.getFirstColumn() == 0) {
                        node.setStoreProcedure(StringUtils.trim(sheet.getRow(rowIndex).getCell(4).getStringCellValue())
                                .toUpperCase());
                    }
                    cell = cell + 2;
                    node.setColums(new ArrayList<>());
                    String targetName = StringUtils.trim(sheet.getRow(cr.getFirstRow()).
                            getCell(cr.getFirstColumn()).getStringCellValue()).toUpperCase();
                    while (rowIndex <= lastRow) {
                        if (sheet.getRow(rowIndex).getCell(cell) != null &&
                                StringUtils.isNotBlank(sheet.getRow(rowIndex).getCell(cell).toString())) {
                            String name = StringUtils.trim(sheet.getRow(rowIndex).getCell(cell).getStringCellValue())
                                    .toUpperCase();
                            String comment = StringUtils.trim(sheet.getRow(rowIndex).getCell(cell + 1).getStringCellValue())
                                    .toUpperCase();
                            node.addColumn(new Column(name, comment));
                        }
                        String sourceName = StringUtils.trim(sheet.getRow(rowIndex).getCell(5).getStringCellValue()).
                                toUpperCase();

                        if (cr.getFirstColumn() == 0 && StringUtils.isNotBlank(sourceName)) {
                            Link link = new Link();
                            link.setTargetTable(targetName);
                            link.setSouceTable(sourceName);
                            link.setValue(0);
                            linkList.add(link);
                        }
                        rowIndex++;
                    }

                    if (nodeList.size() > 0) {
                        for (int i = 0; i < nodeList.size(); i++) {
                            Node existNode = nodeList.get(i);
                            if (existNode.getName().equalsIgnoreCase(node.getName())) {
                                for (Column column : node.getColums()) {
                                    if (!existNode.getColums().contains(column)) {
                                        existNode.addColumn(column);
                                    }
                                }
                            } else if (i == nodeList.size() - 1) {
                                nodeList.add(node);
                            }
                        }
                    } else {
                        nodeList.add(node);
                    }
                }
            }


        }
        paseResult(nodeList, linkList);
        //找出了所有的Node与link已经
        List<ProcessRelation> processRelationList = getProcessRelationList(nodeList, linkList);
        return processRelationList;
    }

    private static List<ProcessRelation> getProcessRelationList(List<Node> nodeList, List<Link> linkList)
            throws IOException, InvalidFormatException {
        List<ProcessRelation> processRelationList = new ArrayList<>();
        for (Node node : nodeList) {
            TreeOfRelation sourceTable = findRelationTable(node.getName(), nodeList,
                    linkList, new TreeOfRelation(), 0);
            TreeOfRelation afterTable = findRelationTable(node.getName(), nodeList,
                    linkList, new TreeOfRelation(), 1);
            ProcessRelation processRelation = new ProcessRelation();
            processRelation.setStoreProcedure(node.getStoreProcedure());
            processRelation.setTableName(node.getName());
            processRelation.setComment(node.getComment());

            JSONArray columns = new JSONArray();
            columns.addAll(node.buildcolumnsName());
            JSONArray soucreTables = new JSONArray();
            soucreTables.addAll(sourceTable.getTableName());
            JSONArray afterTables = new JSONArray();
            afterTables.addAll(afterTable.getTableName());

            processRelation.setColumns(columns.toJSONString().replace("\"", "'")
                    .replace("\n", ""));
            processRelation.setSourceTables(soucreTables.toJSONString().replace("\"", "'")
                    .replace("\n", ""));
            processRelation.setAfterTables(afterTables.toJSONString().replace("\"", "'").
                    replace("\n", ""));
            processRelation.setMapJson(StringUtils.replace(JsonTool.buildJson(sourceTable, afterTable).toJSONString(),
                    "\"", "'").replace("\n", ""));
            processRelationList.add(processRelation);
        }
        return processRelationList;
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
     * @param tableName 需要查询的表名
     * @param linkList  excel查询出来的对应关系(单层)
     * @param nodeList  所有Node的集合
     * @param value     来源表或者影响表，查询来源表就是0  查询影响表就是1
     * @return
     * @throws IOException
     * @throws InvalidFormatException
     */
    private static TreeOfRelation findRelationTable(String tableName, List<Node> nodeList, List<Link> linkList,
                                                    TreeOfRelation treeOfRelation, int value) throws IOException,
            InvalidFormatException {
        //已经便利过这个表

        if (treeOfRelation.containTable(tableName)) {
            return treeOfRelation;
        }
        for (Node node : nodeList) {
            if (node.getName().equalsIgnoreCase(tableName)) {
                treeOfRelation.addNode(node);
            }
        }
        for (Link link : linkList) {
            if (value == 0 && link.getTargetTable().equalsIgnoreCase(tableName)) {
                treeOfRelation.addLink(link);
                treeOfRelation = findRelationTable(link.getSouceTable(), nodeList, linkList, treeOfRelation, value);
            }
            if (value == 1 && link.getSouceTable().equalsIgnoreCase(tableName)) {
                treeOfRelation.addLink(link);
                treeOfRelation = findRelationTable(link.getTargetTable(), nodeList, linkList, treeOfRelation, value);
            }
        }

        return treeOfRelation;
    }


    public static void paseResult(List<Node> nodeList, List<Link> linkList) {
        Map<Node, Collection<Node>> replaceMap = new HashMap<>();
        String targetName = "";
        for (Node node : replaceMap.keySet()) {
            nodeList.remove(node);
            Collection<Node> nodeCollection = replaceMap.get(node);
            for (int i = 0; i < linkList.size(); i++) {
                if (linkList.get(i).getSouceTable().equalsIgnoreCase(node.getName())) {
                    targetName = linkList.get(i).getTargetTable();
                    linkList.remove(i);
                }
            }
            for (Node newNode : nodeCollection) {
                //NodeList
                if (nodeList.size() > 0) {
                    for (int i = 0; i < nodeList.size(); i++) {
                        Node existNode = nodeList.get(i);
                        if (existNode.getName().equalsIgnoreCase(newNode.getName())) {
                            for (Column column : newNode.getColums()) {
                                if (!existNode.getColums().contains(column)) {
                                    existNode.addColumn(column);
                                }
                            }
                        } else if (i == nodeList.size() - 1) {
                            nodeList.add(newNode);
                        }
                    }
                } else {
                    nodeList.add(newNode);
                }
                if (null != targetName && StringUtils.isNotBlank(targetName)) {
                    linkList.add(new Link(0, newNode.getName(), targetName));
                }
            }
        }
    }

    public List<SourceDataNode> getInitData() throws IOException, InvalidFormatException {
        // Creating a Workbook from an Excel file (.xls or .xlsx)
        Workbook workbook = WorkbookFactory.create(new File(SAMPLE_XLSX_FILE_PATH));

        Iterator<Sheet> sheetIterable = workbook.sheetIterator();

        String targetColumnName;
        String targetColumnComment;
        String targetTableName;
        String targetTableComment;
        String storeProcedure;

        List<SourceDataNode> sourceDataNodes = new ArrayList<>();
        while (sheetIterable.hasNext()) {
            Sheet sheet = sheetIterable.next();
            Iterator<Row> rowIterator = sheet.rowIterator();
            List<CellRangeAddress> crs = sheet.getMergedRegions();
            for (CellRangeAddress cr : crs) {
                if (cr.getFirstColumn() == 0) {
                    int lastRow = cr.getLastRow();
                    int cell = cr.getFirstColumn();
                    int columnIndex = cr.getFirstRow();//列的Index
                    int sourceTableIndex = columnIndex; //sourceTable的Index

                    targetTableName = StringUtils.trim(sheet.getRow(columnIndex).getCell(cell + 1)
                            .getStringCellValue()).toUpperCase();
                    targetTableComment = StringUtils.trim(sheet.getRow(columnIndex).getCell(cell + 2)
                            .getStringCellValue()).toUpperCase();
                    storeProcedure =  StringUtils.trim(sheet.getRow(columnIndex).getCell(cell + 9)
                            .getStringCellValue()).toUpperCase();
                    while (columnIndex <=lastRow) {
                        targetColumnName = StringUtils.trim(sheet.getRow(columnIndex).getCell(cell + 3)
                                .getStringCellValue()).toUpperCase();
                        targetColumnComment = StringUtils.trim(sheet.getRow(columnIndex).getCell(cell + 4)
                                .getStringCellValue()).toUpperCase();
                        Result result = isMergedRegion(sheet, columnIndex, cell + 3);
                        if (result.merged) {
                            columnIndex = result.endRow + 1;
                            while (sourceTableIndex <= result.endRow) {
                                String sourceTableName = StringUtils.trim(sheet.getRow(sourceTableIndex)
                                        .getCell(cell + 5).getStringCellValue()).toUpperCase();
                                String sourceTableComment = StringUtils.trim(sheet.getRow(sourceTableIndex)
                                        .getCell(cell + 6).getStringCellValue()).toUpperCase();
                                Result tableResult = isMergedRegion(sheet, sourceTableIndex, cell + 5);
                                if (tableResult.merged) {
                                    Result sourceTableResult = isMergedRegion(sheet, sourceTableIndex, cell + 5);
                                    if (sourceTableResult.merged) {
                                        while(sourceTableIndex<=sourceTableResult.endRow){
                                            String sourceColumnName = StringUtils.trim(sheet.getRow(sourceTableIndex)
                                                    .getCell(cell + 7).getStringCellValue()).toUpperCase();
                                            String sourceColumnComment = StringUtils.trim(sheet.getRow(sourceTableIndex)
                                                    .getCell(cell + 8).getStringCellValue()).toUpperCase();
                                            SourceDataNode sourceDataNode = new SourceDataNode(targetTableName
                                                    ,targetColumnComment,targetColumnName,targetColumnComment
                                                    ,sourceTableName,sourceTableComment,sourceColumnName
                                                    ,sourceColumnComment,storeProcedure);
                                            sourceDataNodes.add(sourceDataNode);
                                            sourceTableIndex++;
                                        }
                                    }else{
                                        String sourceColumnName = StringUtils.trim(sheet.getRow(sourceTableIndex)
                                                .getCell(cell + 7).getStringCellValue()).toUpperCase();
                                        String sourceColumnComment = StringUtils.trim(sheet.getRow(sourceTableIndex)
                                                .getCell(cell + 8).getStringCellValue()).toUpperCase();
                                        SourceDataNode sourceDataNode = new SourceDataNode(targetTableName
                                                ,targetTableComment,targetColumnName,targetColumnComment,sourceTableName
                                                ,sourceTableComment,sourceColumnName,sourceColumnComment,storeProcedure);
                                        sourceDataNodes.add(sourceDataNode);
                                        sourceTableIndex++;
                                    }
                                }else {
                                    String sourceColumnName = StringUtils.trim(sheet.getRow(sourceTableIndex)
                                            .getCell(cell + 7).getStringCellValue()).toUpperCase();
                                    String sourceColumnComment = StringUtils.trim(sheet.getRow(sourceTableIndex)
                                            .getCell(cell + 8).getStringCellValue()).toUpperCase();
                                    SourceDataNode sourceDataNode = new SourceDataNode(targetTableName
                                            ,targetTableComment,targetColumnName,targetColumnComment,sourceTableName
                                            ,sourceTableComment,sourceColumnName,sourceColumnComment,storeProcedure);
                                    sourceDataNodes.add(sourceDataNode);
                                    sourceTableIndex++;
                                }
                            }

                        } else {
                            String sourceTableName = StringUtils.trim(sheet.getRow(sourceTableIndex)
                                    .getCell(cell + 5).getStringCellValue()).toUpperCase();
                            String sourceTableComment = StringUtils.trim(sheet.getRow(sourceTableIndex)
                                    .getCell(cell + 6).getStringCellValue()).toUpperCase();
                            String sourceColumnName = StringUtils.trim(sheet.getRow(sourceTableIndex)
                                    .getCell(cell + 7).getStringCellValue()).toUpperCase();
                            String sourceColumnComment = StringUtils.trim(sheet.getRow(sourceTableIndex)
                                    .getCell(cell + 8).getStringCellValue()).toUpperCase();
                            SourceDataNode sourceDataNode = new SourceDataNode(targetTableName,targetTableComment
                                    ,targetColumnName,targetColumnComment,sourceTableName
                                    ,sourceTableComment,sourceColumnName,sourceColumnComment,storeProcedure);
                            sourceDataNodes.add(sourceDataNode);
                            columnIndex++;
                            sourceTableIndex++;
                        }
                    }
                }
            }
        }
        return sourceDataNodes;
    }

    public static Result isMergedRegion(Sheet sheet, int row, int column) {
        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            int firstColumn = range.getFirstColumn();
            int lastColumn = range.getLastColumn();
            int firstRow = range.getFirstRow();
            int lastRow = range.getLastRow();
            if (row >= firstRow && row <= lastRow) {
                if (column >= firstColumn && column <= lastColumn) {
                    return new Result(true, firstRow , lastRow , firstColumn ,lastColumn );
                }
            }
        }
        return new Result(false, 0, 0, 0, 0);
    }
    @Test
    public void test(){
        try {
            List<SourceDataNode> sourceDataNodes =getInitData();
            JSONArray array = new JSONArray();
            array.addAll(sourceDataNodes);
            System.out.println(array.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
    }
}
