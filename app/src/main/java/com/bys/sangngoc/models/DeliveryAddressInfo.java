package com.bys.sangngoc.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by DC-MEN on 3/29/2018.
 */

public class DeliveryAddressInfo implements Serializable{
    @SerializedName("address")
    public String address;
    @SerializedName("addressLatitude")
    public double addressLatitude;
    @SerializedName("addressLongitude")
    public double addressLongitude;
    @SerializedName("contactPhone")
    public String contactPhone;
    @SerializedName("contactName")
    public String contactName;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getAddressLatitude() {
        return addressLatitude;
    }

    public void setAddressLatitude(double addressLatitude) {
        this.addressLatitude = addressLatitude;
    }

    public double getAddressLongitude() {
        return addressLongitude;
    }

    public void setAddressLongitude(double addressLongitude) {
        this.addressLongitude = addressLongitude;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
}
