package com.lexor.qbsa.beanhandler;

import com.lexor.qbsa.domain.CompanyConfig;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.BeanProcessor;
import org.apache.commons.dbutils.handlers.BeanListHandler;

public class CompanyConfigHandler extends BeanListHandler<CompanyConfig> {

    public CompanyConfigHandler() {
        super(CompanyConfig.class, new BasicRowProcessor(new BeanProcessor(getColumnsToFieldsMap())));
    }

    public static Map<String, String> getColumnsToFieldsMap() {
        Map<String, String> columnsToFieldsMap = new HashMap<>();
        columnsToFieldsMap.put("id", "id");
        columnsToFieldsMap.put("realmId", "realmId");
        columnsToFieldsMap.put("accessToken", "accessToken");
        columnsToFieldsMap.put("accessTokenSecret", "accessTokenSecret");
        columnsToFieldsMap.put("webhooksSubscribedEntites", "webhooksSubscribedEntites");
        columnsToFieldsMap.put("lastCdcTimestamp", "lastCdcTimestamp");
        columnsToFieldsMap.put("oauth2BearerToken", "oauth2BearerToken");
        return columnsToFieldsMap;
    }
}
