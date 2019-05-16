package com.bys.sangngoc.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MeasureUnit implements Serializable{
    @SerializedName("key")
    private String key;
    @SerializedName("value")
    private String valuee;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValuee() {
        return valuee;
    }

    public void setValuee(String valuee) {
        this.valuee = valuee;
    }
}
