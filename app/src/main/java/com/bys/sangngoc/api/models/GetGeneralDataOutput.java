package com.bys.sangngoc.api.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import com.bys.sangngoc.models.Currency;
import com.bys.sangngoc.models.KeyValue;

/**
 * Created by Admin on 4/13/2017.
 */

public class GetGeneralDataOutput extends BaseOutput {
    @SerializedName("result")
    public Result result;

    public class Result implements Serializable {
        @SerializedName("taskStatus")
        public ArrayList<KeyValue> taskStatus;
        @SerializedName("historyTaskStatus")
        public ArrayList<KeyValue> historyTaskStatus;
        @SerializedName("toDoTaskStatus")
        public ArrayList<KeyValue> toDoTaskStatus;
        @SerializedName("deliveryIssueType")
        public ArrayList<KeyValue> deliveryIssueType;
        @SerializedName("deliveryPointStatus")
        public ArrayList<KeyValue> deliveryPointStatus;
        @SerializedName("toDoDeliveryPointStatus")
        public ArrayList<KeyValue> toDoDeliveryPointStatus;
        @SerializedName("historyDeliveryPointStatus")
        public ArrayList<KeyValue> historyDeliveryPointStatus;
        @SerializedName("saleOrderStatus")
        public ArrayList<KeyValue> saleOrderStatus;
        @SerializedName("differenceQtyConfigs")
        public ArrayList<KeyValue> differenceQtyConfigs;
        @SerializedName("customerTypes")
        public ArrayList<KeyValue> customerTypes;
        @SerializedName("paymentTypes")
        public ArrayList<KeyValue> paymentTypes;
        @SerializedName("currencies")
        public ArrayList<Currency> currencies;
        @SerializedName("genders")
        public ArrayList<KeyValue> genders;
    }
}
