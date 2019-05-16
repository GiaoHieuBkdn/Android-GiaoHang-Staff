package com.bys.sangngoc.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Admin on 3/27/2018.
 */

public class DeliveredStatusInfo implements Serializable{
    @SerializedName("actualDeliveryTime")
    public long actualDeliveryTime;
    @SerializedName("images")
    public ArrayList<Image> images;

    public long getActualDeliveryTime() {
        return actualDeliveryTime;
    }

    public void setActualDeliveryTime(long actualDeliveryTime) {
        this.actualDeliveryTime = actualDeliveryTime;
    }

    public ArrayList<Image> getImages() {
        return images;
    }

    public void setImages(ArrayList<Image> images) {
        this.images = images;
    }
}
