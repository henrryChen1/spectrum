package com.plkj.spectrum.service;

import com.plkj.spectrum.bean.ProcessRelation;
import com.plkj.spectrum.bean.SourceDataNode;
import com.plkj.spectrum.dao.ProcessRelationDao;
import com.plkj.spectrum.dao.SourceDataNodeDao;
import com.plkj.spectrum.tool.DataTool;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
@Service
public class SourceDataNodeService {
    @Autowired
    private SourceDataNodeDao sourceDataNodeDao;
    @Autowired
    private ProcessRelationDao processRelationDao;

    public void executeData() {
        List<SourceDataNode> sourceDataNodes = sourceDataNodeDao.findAllData();
        try {
            List<ProcessRelation> processRelationList = DataTool.executeData(sourceDataNodes);
            processRelationDao.truncateTable();
            processRelationDao.insertData(processRelationList);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }

    }
}
