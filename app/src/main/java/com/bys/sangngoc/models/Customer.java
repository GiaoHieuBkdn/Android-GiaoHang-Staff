package com.bys.sangngoc.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Admin on 3/27/2018.
 */

public class Customer implements Serializable{
    @SerializedName("id")
    public int id;
    @SerializedName("customerNo")
    public String customerNo;
    @SerializedName("customerName")
    public String customerName;
    @SerializedName("customerContactAddressLine3")
    public String customerContactAddressLine3;
//    @SerializedName("customerDeliveryAddressLine3")
//    public String customerDeliveryAddressLine3;
    @SerializedName("customerContactPhone")
    public String customerContactPhone;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCustomerNo() {
        return customerNo;
    }

    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerContactAddressLine3() {
        return customerContactAddressLine3;
    }

    public void setCustomerContactAddressLine3(String customerContactAddressLine3) {
        this.customerContactAddressLine3 = customerContactAddressLine3;
    }

//    public String getCustomerDeliveryAddressLine3() {
//        return customerDeliveryAddressLine3;
//    }
//
//    public void setCustomerDeliveryAddressLine3(String customerDeliveryAddressLine3) {
//        this.customerDeliveryAddressLine3 = customerDeliveryAddressLine3;
//    }

    public String getCustomerContactPhone() {
        return customerContactPhone;
    }

    public void setCustomerContactPhone(String customerContactPhone) {
        this.customerContactPhone = customerContactPhone;
    }
}
