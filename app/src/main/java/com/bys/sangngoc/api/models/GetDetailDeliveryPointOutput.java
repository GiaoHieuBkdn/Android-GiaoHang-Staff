package com.bys.sangngoc.api.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import com.bys.sangngoc.models.DeliveryPoint;

/**
 * Created by Admin on 4/13/2017.
 */

public class GetDetailDeliveryPointOutput extends BaseOutput {
    @SerializedName("result")
    public DeliveryPoint result;
}
