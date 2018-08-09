package com.plkj.spectrum.tool;

import com.alibaba.fastjson.JSONArray;
import com.plkj.spectrum.bean.ProcessRelation;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;

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
            "/spectrum/src/main/relationShip.xlsx";

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
                        if (StringUtils.isNotBlank(sheet.getRow(rowIndex).getCell(cell).getStringCellValue())) {
                            node.addColumns(StringUtils.trim(sheet.getRow(rowIndex).getCell(cell).getStringCellValue())
                                    .toUpperCase());
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
                                for (String column : node.getColums()) {
                                    if (!existNode.getColums().contains(column)) {
                                        existNode.addColumns(column);
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
            columns.addAll(node.getColums());
            JSONArray soucreTables = new JSONArray();
            soucreTables.addAll(sourceTable.getTableName());
            JSONArray afterTables = new JSONArray();
            afterTables.addAll(afterTable.getTableName());

            processRelation.setColumns(columns.toJSONString().replace("\"", "'")
                    .replace("\n",""));
            processRelation.setSourceTables(soucreTables.toJSONString().replace("\"", "'")
                    .replace("\n",""));
            processRelation.setAfterTables(afterTables.toJSONString().replace("\"", "'").
                    replace("\n",""));
            processRelation.setMapJson(StringUtils.replace(JsonTool.buildJson(sourceTable, afterTable).toJSONString(),
                    "\"", "'").replace("\n",""));
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

    /**
     * parse if name .equals DW_SAVING_CARD_INFO（t1)
     * DW_CARD_INFO  (t2)
     * 需要做一次匹配.
     */
    public static Collection<Node> parseNode(Node node) {
        Map<String, Node> nameMap = new HashMap<>();
        List<Node> nodeList = new ArrayList<>();
        String nm = node.getName().replace("（", "(").replace("\r", "").
                replace("）", ")");
        String[] names = nm.split("\n");
        for (String name : names) {
            String n = StringUtils.trim(name.split("\\(")[0]);
            String t = StringUtils.trim(name.split("\\(")[1].split("\\)")[0]);
            Node newNode = new Node();
            newNode.setName(n);
            nameMap.put(t, newNode);
        }
        if (StringUtils.isNotBlank(node.getComment())) {
            String ct = node.getComment().replace("（", "(").replace("\r", "").
                    replace("）", ")");
            String[] comments = ct.split("\n");
            for (String comment : comments) {
                String value = StringUtils.trim(comment.split("\\(")[0]);
                String id = StringUtils.trim(comment.split("\\(")[1].split("\\)")[0]);
                for (String key : nameMap.keySet()) {
                    if (id.equalsIgnoreCase(key)) {
                        nameMap.get(id).setComment(value);
                    }
                }
            }
        }
        if (node.getColums() != null) {
            for (String colums : node.getColums()) {
                String value = StringUtils.trim(colums.split("\\(")[0]);
                String id = StringUtils.trim(colums.split("\\(")[1].split("\\)")[0]);
                for (String key : nameMap.keySet()) {
                    if (id.contains(key)) {
                        nameMap.get(key).addColumns(value);
                    }
                }
            }
        }

        return nameMap.values();
    }


    public static void paseResult(List<Node> nodeList, List<Link> linkList) {
        Map<Node, Collection<Node>> replaceMap = new HashMap<>();
        String targetName = "";
        for (Node node : nodeList) {
            if (node.getName().contains("\n")) {
                Collection<Node> nodeList1 = parseNode(node);
                replaceMap.put(node, nodeList1);
            }
        }
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
                            for (String column : newNode.getColums()) {
                                if (!existNode.getColums().contains(column)) {
                                    existNode.addColumns(column);
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

}
