package com.bys.sangngoc.api.models;

import com.google.gson.annotations.SerializedName;

import com.bys.sangngoc.models.User;

/**
 * Created by Admin on 4/13/2017.
 */

public class LoginOutput extends BaseOutput {
    @SerializedName("result")
    public Result result;

    public class Result {
        @SerializedName("userId")
        public int userId;
        @SerializedName("employeeId")
        public int employeeId;
        @SerializedName("tokenType")
        public String tokenType;
        @SerializedName("accessToken")
        public String accessToken;
        @SerializedName("expiresInSeconds")
        public double expiresInSeconds;
        @SerializedName("role")
        public String role;
    }
}
