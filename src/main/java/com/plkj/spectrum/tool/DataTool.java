package com.plkj.spectrum.tool;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.plkj.spectrum.bean.ProcessRelation;
import com.plkj.spectrum.bean.SourceDataNode;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.springframework.web.multipart.MultipartFile;

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
public class DataTool {
    public static final String SAMPLE_XLSX_FILE_PATH = "/Users/chenwei/Code" +
            "/spectrum/src/main/test1.xlsx";
    private static final String SUFFIX_2003 = ".xls";
    private static final String SUFFIX_2007 = ".xlsx";


    public static void main(String[] args) throws IOException, InvalidFormatException {
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
                    .replace("\n", "").replace("\r","")
                    .replace("\t",""));
            processRelation.setSourceTables(soucreTables.toJSONString().replace("\"", "'")
                    .replace("\n", "").replace("\r","")
                    .replace("\t",""));
            processRelation.setAfterTables(afterTables.toJSONString().replace("\"", "'").
                    replace("\n", "").replace("\r","")
                    .replace("\t",""));
            processRelation.setMapJson(StringUtils.replace(JsonTool.buildJson(sourceTable, afterTable).toJSONString(),
                    "\"", "'").replace("\n", "")
                    .replace("\r","").replace("\t",""));;
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


    public static List<SourceDataNode> getInitData(MultipartFile file) throws IOException, InvalidFormatException {
        // Creating a Workbook from an Excel file (.xls or .xlsx)
        if (file == null) {
            return null;
        }
        String originalFilename = file.getOriginalFilename();
        Workbook workbook = null;

        if (originalFilename.endsWith(SUFFIX_2003)) {
            workbook = new HSSFWorkbook(file.getInputStream());
        } else if (originalFilename.endsWith(SUFFIX_2007)) {
            workbook = new XSSFWorkbook(file.getInputStream());
        } else {
            return null;
        }

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
                if (cr.getFirstColumn() == 1) {
                    int lastRow = cr.getLastRow();
                    int cell = cr.getFirstColumn();
                    int columnIndex = cr.getFirstRow();//列的Index
                    int sourceTableIndex = columnIndex; //sourceTable的Index

                    targetTableName = sheet.getRow(columnIndex).getCell(cell)!=null?
                            StringUtils.trim(sheet.getRow(columnIndex).getCell(cell)
                            .getStringCellValue()).toUpperCase():"";
                    targetTableComment = sheet.getRow(columnIndex).getCell(cell + 1)!=null?
                            StringUtils.trim(sheet.getRow(columnIndex).getCell(cell + 1)
                            .getStringCellValue()).toUpperCase():"";
                    storeProcedure = sheet.getRow(columnIndex).getCell(cell + 8)!=null?
                            StringUtils.trim(sheet.getRow(columnIndex).getCell(cell + 8)
                            .getStringCellValue()).toUpperCase():"";
                    while (columnIndex <= lastRow) {
                        targetColumnName =sheet.getRow(columnIndex).getCell(cell + 2)!=null?
                                StringUtils.trim(sheet.getRow(columnIndex).getCell(cell + 2)
                                .getStringCellValue()).toUpperCase():"";
                        targetColumnComment = sheet.getRow(columnIndex).getCell(cell + 3)!=null?
                                StringUtils.trim(sheet.getRow(columnIndex).getCell(cell + 3)
                                .getStringCellValue()).toUpperCase():"";
                        Result result = isMergedRegion(sheet, columnIndex, cell + 2);
                        if (result.merged) {
                            columnIndex = result.endRow + 1;
                            while (sourceTableIndex <= result.endRow) {
                                String sourceTableName = sheet.getRow(sourceTableIndex)
                                        .getCell(cell + 4)!=null?
                                        StringUtils.trim(sheet.getRow(sourceTableIndex)
                                        .getCell(cell + 4).getStringCellValue()).toUpperCase():"";
                                String sourceTableComment = sheet.getRow(sourceTableIndex)
                                        .getCell(cell + 5)!=null?
                                        StringUtils.trim(sheet.getRow(sourceTableIndex)
                                        .getCell(cell + 5).getStringCellValue()).toUpperCase():"";
                                Result tableResult = isMergedRegion(sheet, sourceTableIndex, cell + 4);
                                if (tableResult.merged) {
                                    Result sourceTableResult = isMergedRegion(sheet, sourceTableIndex, cell + 4);
                                    if (sourceTableResult.merged) {
                                        while (sourceTableIndex <= sourceTableResult.endRow) {
                                            String sourceColumnName = sheet.getRow(sourceTableIndex)
                                                    .getCell(cell + 6)!=null?
                                                    StringUtils.trim(sheet.getRow(sourceTableIndex)
                                                    .getCell(cell + 6).getStringCellValue()).toUpperCase():"";
                                            String sourceColumnComment = sheet.getRow(sourceTableIndex)
                                                    .getCell(cell + 7)!=null?
                                                    StringUtils.trim(sheet.getRow(sourceTableIndex)
                                                    .getCell(cell + 7).getStringCellValue()).toUpperCase():"";
                                            SourceDataNode sourceDataNode = new SourceDataNode(targetTableName
                                                    , targetColumnComment, targetColumnName, targetColumnComment
                                                    , sourceTableName, sourceTableComment, sourceColumnName
                                                    , sourceColumnComment, storeProcedure);
                                            if (StringUtils.isNotBlank(sourceDataNode.getTargetTableName())
                                                    && StringUtils.isNotBlank(sourceDataNode.getSourceColumnName())
                                                    ) {
                                                sourceDataNodes.add(sourceDataNode);
                                            }
                                            sourceTableIndex++;
                                        }
                                    } else {
                                        String sourceColumnName = sheet.getRow(sourceTableIndex)
                                                .getCell(cell + 6)!=null?
                                                StringUtils.trim(sheet.getRow(sourceTableIndex)
                                                .getCell(cell + 6).getStringCellValue()).toUpperCase():"";
                                        String sourceColumnComment = sheet.getRow(sourceTableIndex)
                                                .getCell(cell + 7)!=null?
                                                StringUtils.trim(sheet.getRow(sourceTableIndex)
                                                .getCell(cell + 7).getStringCellValue()).toUpperCase():"";
                                        SourceDataNode sourceDataNode = new SourceDataNode(targetTableName
                                                , targetTableComment, targetColumnName, targetColumnComment, sourceTableName
                                                , sourceTableComment, sourceColumnName, sourceColumnComment, storeProcedure);
                                        if (StringUtils.isNotBlank(sourceDataNode.getTargetTableName())
                                                && StringUtils.isNotBlank(sourceDataNode.getSourceColumnName())
                                                ) {
                                            sourceDataNodes.add(sourceDataNode);
                                        }
                                        sourceTableIndex++;
                                    }
                                } else {
                                    String sourceColumnName = sheet.getRow(sourceTableIndex)
                                            .getCell(cell + 6)!=null?
                                            StringUtils.trim(sheet.getRow(sourceTableIndex)
                                            .getCell(cell + 6).getStringCellValue()).toUpperCase():"";
                                    String sourceColumnComment = sheet.getRow(sourceTableIndex)
                                            .getCell(cell + 7)!=null?
                                            StringUtils.trim(sheet.getRow(sourceTableIndex)
                                            .getCell(cell + 7).getStringCellValue()).toUpperCase():"";
                                    SourceDataNode sourceDataNode = new SourceDataNode(targetTableName
                                            , targetTableComment, targetColumnName, targetColumnComment, sourceTableName
                                            , sourceTableComment, sourceColumnName, sourceColumnComment, storeProcedure);
                                    if (StringUtils.isNotBlank(sourceDataNode.getTargetTableName())
                                            && StringUtils.isNotBlank(sourceDataNode.getSourceColumnName())
                                            ) {
                                        sourceDataNodes.add(sourceDataNode);
                                    }
                                    sourceTableIndex++;
                                }
                            }

                        } else {
                            String sourceTableName = sheet.getRow(sourceTableIndex)
                                    .getCell(cell + 4)!=null?
                                    StringUtils.trim(sheet.getRow(sourceTableIndex)
                                    .getCell(cell + 4).getStringCellValue()).toUpperCase():"";
                            String sourceTableComment = sheet.getRow(sourceTableIndex)
                                    .getCell(cell + 5)!=null?
                                    StringUtils.trim(sheet.getRow(sourceTableIndex)
                                    .getCell(cell + 5).getStringCellValue()).toUpperCase():"";
                            String sourceColumnName = sheet.getRow(sourceTableIndex)
                                    .getCell(cell + 6)!=null?
                                    StringUtils.trim(sheet.getRow(sourceTableIndex)
                                    .getCell(cell + 6).getStringCellValue()).toUpperCase():"";
                            String sourceColumnComment = sheet.getRow(sourceTableIndex)
                                    .getCell(cell + 7)!=null ?
                                    StringUtils.trim(sheet.getRow(sourceTableIndex)
                                    .getCell(cell + 7).getStringCellValue()).toUpperCase():"";
                            SourceDataNode sourceDataNode = new SourceDataNode(targetTableName, targetTableComment
                                    , targetColumnName, targetColumnComment, sourceTableName
                                    , sourceTableComment, sourceColumnName, sourceColumnComment, storeProcedure);
                            if (StringUtils.isNotBlank(sourceDataNode.getTargetTableName())
                                    && StringUtils.isNotBlank(sourceDataNode.getSourceColumnName())
                                    ) {
                                sourceDataNodes.add(sourceDataNode);
                            }
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
                    return new Result(true, firstRow, lastRow, firstColumn, lastColumn);
                }
            }
        }
        return new Result(false, 0, 0, 0, 0);
    }


    public static List<ProcessRelation> executeData(List<SourceDataNode> sourceDataNodeList)
            throws IOException, InvalidFormatException {
        Map<String, Node> nodeMap = new HashMap<>();
        List<Link> linkList = new LinkedList<>();
        for (SourceDataNode sourceDataNode : sourceDataNodeList) {
            if (StringUtils.isNotBlank(sourceDataNode.getTargetColumnName()) &&
                    StringUtils.isNotBlank(sourceDataNode.getTargetTableName())) {
                if (nodeMap.containsKey(sourceDataNode.getTargetTableName())) {
                    Node node = nodeMap.get(sourceDataNode.getTargetTableName());
                    node.addColumn(new Column(sourceDataNode.getTargetColumnName(), sourceDataNode.getTargetColumnComment()));
                    nodeMap.replace(sourceDataNode.getTargetTableName(), node);
                } else {
                    Node node = new Node(sourceDataNode.getTargetTableName(), null, sourceDataNode.getTargetTableComment()
                            , sourceDataNode.getStoreProcedure());
                    node.addColumn(new Column(sourceDataNode.getTargetColumnName(), sourceDataNode.getTargetColumnComment()));
                    nodeMap.put(sourceDataNode.getTargetTableName(), node);
                }
            }
            if (StringUtils.isNotBlank(sourceDataNode.getSourceColumnName()) &&
                    StringUtils.isNotBlank(sourceDataNode.getSourceTableName())) {
                if (nodeMap.containsKey(sourceDataNode.getSourceTableName())) {
                    Node node = nodeMap.get(sourceDataNode.getSourceTableName());
                    node.addColumn(new Column(sourceDataNode.getSourceColumnName(), sourceDataNode.getSourceColumnComment()));
                    nodeMap.replace(sourceDataNode.getSourceTableName(), node);
                } else {
                    Node node = new Node(sourceDataNode.getSourceTableName(), null, sourceDataNode.getSourceTableComment()
                            , "");
                    node.addColumn(new Column(sourceDataNode.getSourceColumnName(), sourceDataNode.getSourceColumnComment()));
                    nodeMap.put(sourceDataNode.getSourceTableName(), node);
                }
            }
            if (StringUtils.isNotBlank(sourceDataNode.getTargetColumnName()) &&
                    StringUtils.isNotBlank(sourceDataNode.getTargetTableName()) &&
                    StringUtils.isNotBlank(sourceDataNode.getSourceColumnName()) &&
                    StringUtils.isNotBlank(sourceDataNode.getSourceTableName())) {
                linkList.add(new Link(0, sourceDataNode.getSourceTableName(), sourceDataNode.getTargetTableName()
                        , sourceDataNode.getSourceColumnName(), sourceDataNode.getTargetColumnName()));
            }
        }


        List<Node> nodeList = new ArrayList();
        nodeList.addAll(nodeMap.values());
        List<ProcessRelation> processRelationList = getProcessRelationList(nodeList, linkList);
        return processRelationList;
    }

    public static ProcessRelation findRelationColumn(String tableName, String columnName, ProcessRelation processRelation,
                                                     List<SourceDataNode> sourceDataNodes) {

        TreeOfRelation sourceTree = findColumnTree(tableName, columnName, new TreeOfRelation(), sourceDataNodes, 0);
        TreeOfRelation afterTree = findColumnTree(tableName, columnName, new TreeOfRelation(), sourceDataNodes, 1);
        JSONArray afterTables = new JSONArray();
        JSONArray sourceTables = new JSONArray();
        afterTables.addAll(afterTree.getTableName());
        sourceTables.addAll(sourceTree.getTableName());
        JSONObject mapJson = JsonTool.buildJson(afterTree, sourceTree);
        processRelation.setAfterTables(afterTables.toJSONString());
        processRelation.setSourceTables(sourceTables.toJSONString());
        processRelation.setMapJson(mapJson.toJSONString());

        return processRelation;
    }

    public static TreeOfRelation findColumnTree(String tableName, String columnName, TreeOfRelation treeOfRelation,
                                                List<SourceDataNode> sourceDataNodes, int value) {
        if (treeOfRelation == null) {
            treeOfRelation = new TreeOfRelation();
        }
        otter:
        for (SourceDataNode sourceDataNode : sourceDataNodes) {
            if (sourceDataNode.getTargetTableName().equalsIgnoreCase(tableName)
                    && sourceDataNode.getTargetColumnName().equalsIgnoreCase(columnName) && value == 0) {
                if (treeOfRelation.containTable(tableName)) {
                    for (Node node : treeOfRelation.getNodes()) {
                        if (node.getName().equalsIgnoreCase(tableName)) {
                            int index = treeOfRelation.getNodes().indexOf(node);
                            treeOfRelation.getNodes().get(index).addColumn(
                                    new Column(sourceDataNode.getTargetColumnName(),
                                            sourceDataNode.getTargetColumnComment()));
                        }
                    }
                } else {
                    Node node = new Node();
                    node.setName(sourceDataNode.getTargetTableName());
                    node.addColumn(new Column(sourceDataNode.getTargetColumnName()
                            , sourceDataNode.getTargetColumnComment()));
                    treeOfRelation.addNode(node);
                }
                if (treeOfRelation.containTable(sourceDataNode.getSourceTableName())) {

                    for (Node node : treeOfRelation.getNodes()) {
                        if (node.getName().equalsIgnoreCase(sourceDataNode.getSourceTableName())) {
                            int index = treeOfRelation.getNodes().indexOf(node);
                            treeOfRelation.getNodes().get(index).addColumn(
                                    new Column(sourceDataNode.getSourceColumnName(),
                                            sourceDataNode.getSourceColumnComment()));
                        }
                    }
                } else {
                    Node node = new Node();
                    node.setName(sourceDataNode.getSourceTableName());
                    node.setComment(sourceDataNode.getSourceTableComment());
                    node.addColumn(new Column(sourceDataNode.getSourceColumnName()
                            , sourceDataNode.getSourceColumnComment()));
                    treeOfRelation.addNode(node);
                }

                Link link = new Link();
                link.setValue(0);
                link.setTargetTable(sourceDataNode.getTargetTableName());
                link.setTargetColumn(sourceDataNode.getTargetColumnName());
                link.setSouceTable(sourceDataNode.getSourceTableName());
                link.setSourceColumn(sourceDataNode.getSourceColumnName());
                boolean ifAdd = true;
                for (Link everyLink : treeOfRelation.getLinks()) {
                    if (everyLink.equals(link)) {
                        ifAdd = false;
                        break;
                    }
                }
                if (ifAdd) ;
                {
                    treeOfRelation.addLink(link);
                    findColumnTree(sourceDataNode.getSourceTableName(), sourceDataNode.getSourceColumnName(),
                            treeOfRelation, sourceDataNodes, value);
                }
            } else if (sourceDataNode.getSourceTableName().equalsIgnoreCase(tableName)
                    && sourceDataNode.getSourceColumnName().equalsIgnoreCase(columnName) && value == 1) {
                int source = -1;
                int target = -1;
                if (treeOfRelation.containTable(tableName)) {
                    for (Node node : treeOfRelation.getNodes()) {
                        if (node.getName().equalsIgnoreCase(tableName)) {
                            source = treeOfRelation.getNodes().indexOf(node);
                            treeOfRelation.getNodes().get(source).addColumn(
                                    new Column(sourceDataNode.getSourceColumnName(),
                                            sourceDataNode.getSourceColumnComment()));
                        }
                    }
                } else {
                    Node node = new Node();
                    node.setName(sourceDataNode.getSourceTableName());
                    node.setComment(sourceDataNode.getSourceTableComment());
                    node.addColumn(new Column(sourceDataNode.getSourceColumnName()
                            , sourceDataNode.getSourceColumnComment()));
                    treeOfRelation.addNode(node);
                    source = treeOfRelation.getNodes().indexOf(node);
                }

                if (treeOfRelation.containTable(sourceDataNode.getTargetTableName())) {
                    for (Node node : treeOfRelation.getNodes()) {
                        if (node.getName().equalsIgnoreCase(sourceDataNode.getTargetTableName())) {
                            target = treeOfRelation.getNodes().indexOf(node);
                            treeOfRelation.getNodes().get(target).addColumn(
                                    new Column(sourceDataNode.getTargetColumnName(),
                                            sourceDataNode.getTargetColumnComment()));
                        }
                    }
                } else {
                    Node node = new Node();
                    node.setName(sourceDataNode.getTargetTableName());
                    node.setComment(sourceDataNode.getTargetTableComment());
                    node.addColumn(new Column(sourceDataNode.getTargetColumnName()
                            , sourceDataNode.getTargetColumnComment()));
                    treeOfRelation.addNode(node);
                    target = treeOfRelation.getNodes().indexOf(node);
                }
                Link link = new Link();
                link.setValue(0);
                link.setTargetTable(sourceDataNode.getTargetTableName());
                link.setTargetColumn(sourceDataNode.getTargetColumnName());
                link.setSouceTable(sourceDataNode.getSourceTableName());
                link.setSourceColumn(sourceDataNode.getSourceColumnName());
                boolean ifAdd = true;
                for (Link everyLink : treeOfRelation.getLinks()) {
                    if (everyLink.equals(link)) {
                        ifAdd = false;
                        break;
                    }
                }
                if (ifAdd) ;
                {
                    treeOfRelation.addLink(link);
                    findColumnTree(sourceDataNode.getTargetTableName(), sourceDataNode.getTargetColumnName(),
                            treeOfRelation, sourceDataNodes, value);
                }
            }
        }
        return treeOfRelation;
    }
}
