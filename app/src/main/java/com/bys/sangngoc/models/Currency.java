package com.bys.sangngoc.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by DC-MEN on 3/29/2018.
 */

public class Currency implements Serializable{
    @SerializedName("id")
    public int id;
    @SerializedName("currencyNo")
    public String currencyNo;
    @SerializedName("currencyName")
    public String currencyName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCurrencyNo() {
        return currencyNo;
    }

    public void setCurrencyNo(String currencyNo) {
        this.currencyNo = currencyNo;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }
}
