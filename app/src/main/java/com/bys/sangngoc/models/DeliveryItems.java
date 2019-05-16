package com.bys.sangngoc.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class DeliveryItems implements Serializable {
    @SerializedName("deliveryItems")
    public DeliveryItem deliveryItems;
    @SerializedName("note")
    public String note;


    public DeliveryItem getDeliveryItems() {
        return deliveryItems;
    }

    public void setDeliveryItems(DeliveryItem deliveryItems) {
        this.deliveryItems = deliveryItems;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
