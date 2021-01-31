package com.lexor.qbsa.beanhandler;

import com.lexor.qbsa.domain.Configurations;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.BeanProcessor;
import org.apache.commons.dbutils.handlers.BeanListHandler;

public class ConfigurationsHandler extends BeanListHandler<Configurations> {

    public ConfigurationsHandler() {
        super(Configurations.class, new BasicRowProcessor(new BeanProcessor(getColumnsToFieldsMap())));
    }

    public static Map<String, String> getColumnsToFieldsMap() {
        Map<String, String> columnsToFieldsMap = new HashMap<>();
        columnsToFieldsMap.put("id", "id");
        columnsToFieldsMap.put("value", "value");
        columnsToFieldsMap.put("key", "key");
        return columnsToFieldsMap;
    }
}
