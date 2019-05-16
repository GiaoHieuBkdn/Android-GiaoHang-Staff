package com.bys.sangngoc.models;

import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.io.Serializable;

public class ConfirmDeliveryItem implements Serializable {
    @SerializedName("productId")
    private int productId;
    @SerializedName("deliveredQty")
    private double deliveredQty;
    @SerializedName("note")
    private String note;

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public double getDeliveredQty() {
        return deliveredQty;
    }

    public void setDeliveredQty(double deliveredQty) {
        this.deliveredQty = deliveredQty;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}


