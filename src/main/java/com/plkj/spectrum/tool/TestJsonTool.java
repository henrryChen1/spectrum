package com.plkj.spectrum.tool;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestJsonTool {
    @Test
    public void testJsonTool(){

        Link link1 = new Link();
        Link link2 = new Link();
        Link link3 = new Link();
        link1.setSourceColumn("aa1");
        link2.setSourceColumn("bb1");
        link3.setSourceColumn("cc1");
        link1.setTargetColumn("aa2");
        link2.setTargetColumn("bb2");
        link3.setTargetColumn("cc2");
        link1.setSourceTable("aa");
        link2.setSourceTable("bb");
        link3.setSourceTable("cc");
        TreeOfRelation tree = new TreeOfRelation();
        tree.addLink(link1);
        tree.addLink(link2);
        tree.addLink(link3);
        System.out.println(JsonTool.buildJson(tree,new TreeOfRelation()
        ));

    }
}
