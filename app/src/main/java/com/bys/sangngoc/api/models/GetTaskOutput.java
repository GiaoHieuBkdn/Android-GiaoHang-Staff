package com.bys.sangngoc.api.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import com.bys.sangngoc.models.Work;

/**
 * Created by Admin on 4/13/2017.
 */

public class GetTaskOutput extends BaseOutput {
    @SerializedName("result")
    public Work result;
}
