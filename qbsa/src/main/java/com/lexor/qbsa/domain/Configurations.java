package com.lexor.qbsa.domain;

import java.io.Serializable;

/**
 * Entity to store oauth tokens and other configs for the QBO company
 */
public class Configurations implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String key;

    private String value;

    public Configurations(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public Configurations() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
