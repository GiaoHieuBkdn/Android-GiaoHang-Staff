package com.bys.sangngoc.api.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Admin on 4/13/2017.
 */

public class ValidateCodeOutput extends BaseOutput {
    @SerializedName("result")
    public String token;
}
