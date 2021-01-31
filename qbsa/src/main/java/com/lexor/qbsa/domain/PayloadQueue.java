package com.lexor.qbsa.domain;

import java.io.Serializable;

/**
 * Entity to store oauth tokens and other configs for the QBO company
 */
public class PayloadQueue implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String source;

    private String payload;

    private int status;

    public PayloadQueue(String source, String payload, int status) {
        this.source = source;
        this.payload = payload;
        this.status = status;
    }

    public PayloadQueue() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
