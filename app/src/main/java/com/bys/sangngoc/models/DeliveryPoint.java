package com.bys.sangngoc.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Admin on 3/16/2018.
 */

public class DeliveryPoint implements Serializable {
    @SerializedName("id")
    public int id;
    @SerializedName("deliveredStatusInfo")
    public DeliveredStatusInfo deliveredStatusInfo;
    @SerializedName("issuedStatusInfo")
    public IssuedStatusInfo issuedStatusInfo;
    @SerializedName("deliveryPointNo")
    public String deliveryPointNo;
    @SerializedName("saleOrderNo")
    public String saleOrderNo;
    @SerializedName("expectedDeliveryTime")
    public long expectedDeliveryTime;
    @SerializedName("actualDeliveryTime")
    public long actualDeliveryTime;
    @SerializedName("notes")
    public String notes;
    @SerializedName("status")
    public String status;
    @SerializedName("balanceDueAmount")
    public double balanceDueAmount;
    @SerializedName("feedback")
    public String feedback;
    @SerializedName("deliveryToCustomer")
    public Customer deliveryToCustomer;
    @SerializedName("canceledStatusInfo")
    public CanceledStatusInfo canceledStatusInfo;
    @SerializedName("deliveryItems")
    public ArrayList<DeliveryItem> deliveryItems;
    @SerializedName("deliveryAddressInfo")
    public DeliveryAddressInfo deliveryAddressInfo;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public DeliveredStatusInfo getDeliveredStatusInfo() {
        return deliveredStatusInfo;
    }

    public void setDeliveredStatusInfo(DeliveredStatusInfo deliveredStatusInfo) {
        this.deliveredStatusInfo = deliveredStatusInfo;
    }

    public IssuedStatusInfo getIssuedStatusInfo() {
        return issuedStatusInfo;
    }

    public void setIssuedStatusInfo(IssuedStatusInfo issuedStatusInfo) {
        this.issuedStatusInfo = issuedStatusInfo;
    }

    public CanceledStatusInfo getCanceledStatusInfo() {
        return canceledStatusInfo;
    }

    public void setCanceledStatusInfo(CanceledStatusInfo canceledStatusInfo) {
        this.canceledStatusInfo = canceledStatusInfo;
    }

    public String getDeliveryPointNo() {
        return deliveryPointNo;
    }

    public void setDeliveryPointNo(String deliveryPointNo) {
        this.deliveryPointNo = deliveryPointNo;
    }

    public String getSaleOrderNo() {
        return saleOrderNo;
    }

    public void setSaleOrderNo(String saleOrderNo) {
        this.saleOrderNo = saleOrderNo;
    }

    public long getExpectedDeliveryTime() {
        return expectedDeliveryTime;
    }

    public void setExpectedDeliveryTime(long expectedDeliveryTime) {
        this.expectedDeliveryTime = expectedDeliveryTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Customer getDeliveryToCustomer() {
        return deliveryToCustomer;
    }

    public void setDeliveryToCustomer(Customer deliveryToCustomer) {
        this.deliveryToCustomer = deliveryToCustomer;
    }
/*
    public ArrayList<DeliveryItem> getDeliveryItems() {
        return deliveryItems;
    }

    public void setDeliveryItems(ArrayList<DeliveryItem> deliveryItems) {
        this.deliveryItems = deliveryItems;
    }*/

    public ArrayList<DeliveryItem> getDeliveryItems() {
        return deliveryItems;
    }

    public void setDeliveryItems(ArrayList<DeliveryItem> deliveryItems) {
        this.deliveryItems = deliveryItems;
    }

    public DeliveryAddressInfo getDeliveryAddressInfo() {
        return deliveryAddressInfo;
    }

    public void setDeliveryAddressInfo(DeliveryAddressInfo deliveryAddressInfo) {
        this.deliveryAddressInfo = deliveryAddressInfo;
    }

    public long getActualDeliveryTime() {
        return actualDeliveryTime;
    }

    public void setActualDeliveryTime(long actualDeliveryTime) {
        this.actualDeliveryTime = actualDeliveryTime;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public double getBalanceDueAmount() {
        return balanceDueAmount;
    }

    public void setBalanceDueAmount(double balanceDueAmount) {
        this.balanceDueAmount = balanceDueAmount;
    }
}
