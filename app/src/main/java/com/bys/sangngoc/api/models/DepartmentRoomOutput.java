package com.bys.sangngoc.api.models;

import com.bys.sangngoc.models.DepartmentRoom;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Admin on 4/13/2017.
 */

public class DepartmentRoomOutput extends BaseOutput {
    @SerializedName("result")
    public ArrayList<DepartmentRoom> result;
}
