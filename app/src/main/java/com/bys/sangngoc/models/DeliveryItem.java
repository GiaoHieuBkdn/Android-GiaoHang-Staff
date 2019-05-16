package com.bys.sangngoc.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Admin on 3/27/2018.
 */

public class DeliveryItem implements Serializable{
    @SerializedName("productId")
    public int productId;
    @SerializedName("productNo")
    public String productNo;
    @SerializedName("productName")
    public String productName;
    @SerializedName("productDesc")
    public String productDesc;
    @SerializedName("quantity")
    public double quantity;
    @SerializedName("deliveredQty")
    public double deliveredQty;
    @SerializedName("measureUnit")
    public MeasureUnit measureUnit;
    @SerializedName("barrelQty")
    public int barrelQty;
    @SerializedName("note")
    public String note;
    @SerializedName("qtyByQuanlities")
    public ArrayList<ProductQtyOfTypes> productQtyOfTypes;

    public double quantityTmp;
    private double deliveredActualQty;
    private String noteItem;
    private boolean isEdited;
    private boolean isChanged = false;
    private double deliveredGoodQty;
    private double deliveredBadQty;
    private double deliveredErrorQty;
    private double deliveredGoodQtyTmp;
    private double deliveredBadQtyTmp;
    private double deliveredErrorQtyTmp;
    private int productAttributeGoodId;
    private int productAttributeBadId;
    private int productAttributeErrorId;
    private double productAttributeGoodPrice;
    private double productAttributeBadPrice;
    private double productAttributeErrorPrice;
    private String type;
    private int keyReason;
    private int positionReason;
    private ArrayList<KeyValue> mReason;

    public int getKeyReason() {
        return keyReason;
    }

    public void setKeyReason(int keyReason) {
        this.keyReason = keyReason;
    }

    public int getPositionReason() {
        return positionReason;
    }

    public void setPositionReason(int positionReason) {
        this.positionReason = positionReason;
    }

    public ArrayList<KeyValue> getmReason() {
        return mReason;
    }

    public void setmReason(ArrayList<KeyValue> mReason) {
        this.mReason = mReason;
    }

    public double getQuantityTmp() {
        return quantityTmp;
    }

    public void setQuantityTmp(double quantityTmp) {
        this.quantityTmp = quantityTmp;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean status = true;

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductNo() {
        return productNo;
    }

    public void setProductNo(String productNo) {
        this.productNo = productNo;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public DeliveryItem(String productNo, String productDesc, int quantity, int deliveredQty) {
        this.productNo = productNo;
        this.productDesc = productDesc;
        this.quantity = quantity;
        this.deliveredQty = deliveredQty;
    }
    public DeliveryItem() {
    }

    public MeasureUnit getMeasureUnit() {
        return measureUnit;
    }

    public void setMeasureUnit(MeasureUnit measureUnit) {
        this.measureUnit = measureUnit;
    }


    public void setDeliveredActualQty(double deliveredActualQty) {
        this.deliveredActualQty = deliveredActualQty;
    }



    public int getBarrelQty() {
        return barrelQty;
    }

    public void setBarrelQty(int barrelQty) {
        this.barrelQty = barrelQty;
    }

    public void setNoteItem(String noteItem) {
        this.noteItem = noteItem;
    }

    public double getDeliveredActualQty() {
        return deliveredActualQty;
    }

    public String getNoteItem() {
        return noteItem;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getDeliveredQty() {
        return deliveredQty;
    }

    public void setDeliveredQty(double deliveredQty) {
        this.deliveredQty = deliveredQty;
    }

    public boolean isEdited() {
        return isEdited;
    }

    public void setEdited(boolean edited) {
        isEdited = edited;
    }

    public double getDeliveredGoodQty() {
        return deliveredGoodQty;
    }

    public void setDeliveredGoodQty(double deliveredGoodQty) {
        this.deliveredGoodQty = deliveredGoodQty;
    }

    public double getDeliveredBadQty() {
        return deliveredBadQty;
    }

    public void setDeliveredBadQty(double deliveredBadQty) {
        this.deliveredBadQty = deliveredBadQty;
    }

    public double getDeliveredErrorQty() {
        return deliveredErrorQty;
    }

    public void setDeliveredErrorQty(double deliveredErrorQty) {
        this.deliveredErrorQty = deliveredErrorQty;
    }

    public ArrayList<ProductQtyOfTypes> getProductQtyOfTypes() {
        return productQtyOfTypes;
    }

    public void setProductQtyOfTypes(ArrayList<ProductQtyOfTypes> productQtyOfTypes) {
        this.productQtyOfTypes = productQtyOfTypes;
    }

    public double getDeliveredGoodQtyTmp() {
        return deliveredGoodQtyTmp;
    }

    public void setDeliveredGoodQtyTmp(double deliveredGoodQtyTmp) {
        this.deliveredGoodQtyTmp = deliveredGoodQtyTmp;
    }

    public double getDeliveredBadQtyTmp() {
        return deliveredBadQtyTmp;
    }

    public void setDeliveredBadQtyTmp(double deliveredBadQtyTmp) {
        this.deliveredBadQtyTmp = deliveredBadQtyTmp;
    }

    public double getDeliveredErrorQtyTmp() {
        return deliveredErrorQtyTmp;
    }

    public void setDeliveredErrorQtyTmp(double deliveredErrorQtyTmp) {
        this.deliveredErrorQtyTmp = deliveredErrorQtyTmp;
    }

    public int getProductAttributeGoodId() {
        return productAttributeGoodId;
    }

    public void setProductAttributeGoodId(int productAttributeGoodId) {
        this.productAttributeGoodId = productAttributeGoodId;
    }

    public int getProductAttributeBadId() {
        return productAttributeBadId;
    }

    public void setProductAttributeBadId(int productAttributeBadId) {
        this.productAttributeBadId = productAttributeBadId;
    }

    public int getProductAttributeErrorId() {
        return productAttributeErrorId;
    }

    public void setProductAttributeErrorId(int productAttributeErrorId) {
        this.productAttributeErrorId = productAttributeErrorId;
    }

    public double getProductAttributeGoodPrice() {
        return productAttributeGoodPrice;
    }

    public void setProductAttributeGoodPrice(double productAttributeGoodPrice) {
        this.productAttributeGoodPrice = productAttributeGoodPrice;
    }

    public double getProductAttributeBadPrice() {
        return productAttributeBadPrice;
    }

    public void setProductAttributeBadPrice(double productAttributeBadPrice) {
        this.productAttributeBadPrice = productAttributeBadPrice;
    }

    public double getProductAttributeErrorPrice() {
        return productAttributeErrorPrice;
    }

    public void setProductAttributeErrorPrice(double productAttributeErrorPrice) {
        this.productAttributeErrorPrice = productAttributeErrorPrice;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isChanged() {
        return isChanged;
    }

    public void setChanged(boolean changed) {
        isChanged = changed;
    }

}
