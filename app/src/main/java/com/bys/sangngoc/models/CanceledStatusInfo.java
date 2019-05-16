package com.bys.sangngoc.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Admin on 3/27/2018.
 */

public class CanceledStatusInfo implements Serializable{
    @SerializedName("canceledReason")
    public String canceledReason;

    public String getCanceledReason() {
        return canceledReason;
    }

    public void setCanceledReason(String canceledReason) {
        this.canceledReason = canceledReason;
    }
}
