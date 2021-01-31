package com.lexor.qbsa.beanhandler;

import com.lexor.qbsa.domain.PayloadQueue;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.BeanProcessor;
import org.apache.commons.dbutils.handlers.BeanListHandler;

public class PayloadQueueHandler extends BeanListHandler<PayloadQueue> {

    public PayloadQueueHandler() {
        super(PayloadQueue.class, new BasicRowProcessor(new BeanProcessor(getColumnsToFieldsMap())));
    }

    public static Map<String, String> getColumnsToFieldsMap() {
        Map<String, String> columnsToFieldsMap = new HashMap<>();
        columnsToFieldsMap.put("id", "id");
        columnsToFieldsMap.put("source", "source");
        columnsToFieldsMap.put("payload", "payload");
        columnsToFieldsMap.put("status", "status");
        return columnsToFieldsMap;
    }
}
