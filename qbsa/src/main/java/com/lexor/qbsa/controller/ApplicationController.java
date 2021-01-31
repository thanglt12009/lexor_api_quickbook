package com.lexor.qbsa.controller;

import com.lexor.qbsa.util.ConfigHelper;
import com.lexor.qbsa.util.Utility;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.util.Objects;

public class ApplicationController {
    
    protected static final String SUCCESS = "Success";
    protected static final String ERROR = "Error";
    protected static final String REJECTED = "Rejected";
    
    public Response doGet(String params)  {
        return Utility.doGet(params);
    }
    public Response doPost(String apiEndpoint, String postData)  {
        return Utility.doPost(apiEndpoint,postData);
    }

    public Boolean clientInWhitelist(HttpServletRequest request) {

        if (Objects.equals(request.getHeader("appcode"), ConfigHelper.properties.getProperty("appcode"))
                && Objects.equals(request.getRemoteHost(), ConfigHelper.properties.getProperty("IP")))
            return Boolean.TRUE;
        else
            return Boolean.TRUE; //test localhost so all are TRUE
    }

}
