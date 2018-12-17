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
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
            processRelation.setTableName(node.getName());

            JSONArray columns = new JSONArray();
            columns.addAll(node.buildcolumnsName());
            JSONArray soucreTables = new JSONArray();
            soucreTables.addAll(sourceTable.getTableName());
            JSONArray afterTables = new JSONArray();
            afterTables.addAll(afterTable.getTableName());

            processRelation.setSourceTables(soucreTables.toJSONString().replace("\"", "'")
                    .replace("\n", "").replace("\r", "")
                    .replace("\t", ""));
            processRelation.setAfterTables(afterTables.toJSONString().replace("\"", "'").
                    replace("\n", "").replace("\r", "")
                    .replace("\t", ""));
            processRelation.setMapJson(StringUtils.replace(JsonTool.buildJson(sourceTable, afterTable).toJSONString(),
                    "\"", "'").replace("\n", "")
                    .replace("\r", "").replace("\t", ""));
            ;
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

        List<SourceDataNode> sourceDataNodes = new ArrayList<>();

        while (sheetIterable.hasNext()) {
            Sheet sheet = sheetIterable.next();
            Iterator<Row> rowIterator = sheet.rowIterator();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                String targetTableName = row.getCell(1).getStringCellValue();
                String sourceTableName = row.getCell(2).getStringCellValue();
                sourceDataNodes.add(new SourceDataNode(targetTableName, sourceTableName));
            }
        }


        return sourceDataNodes;


    }

    public static List<ProcessRelation> executeData(List<SourceDataNode> sourceDataNodeList)
            throws IOException, InvalidFormatException {
        List<Node> nodeList = new ArrayList<>();
        List<Link> linkList = new LinkedList<>();
        for (SourceDataNode sourceDataNode : sourceDataNodeList) {
            nodeList.add(new Node(sourceDataNode.getSourceTableName()));
            nodeList.add(new Node(sourceDataNode.getTargetTableName()));
            linkList.add(new Link(1, sourceDataNode.getTargetTableName(), sourceDataNode.getSourceTableName()));
        }
        List<Node> resultNodeList = new ArrayList<>();
        List<Link> resultLinkList = new ArrayList<>();
        for(Node node:nodeList){
            if(!resultLinkList.contains(node)){
                re
            }
        }
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
