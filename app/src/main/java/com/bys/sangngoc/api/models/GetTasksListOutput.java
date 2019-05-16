package com.bys.sangngoc.api.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import com.bys.sangngoc.models.Project;
import com.bys.sangngoc.models.Work;

/**
 * Created by Admin on 4/13/2017.
 */

public class GetTasksListOutput extends BaseOutput {
    @SerializedName("result")
    public Result result;

    public class Result {
        @SerializedName("pageIndex")
        public int pageIndex;
        @SerializedName("pageSize")
        public int pageSize;
        @SerializedName("totalCount")
        public int totalCount;
        @SerializedName("totalPages")
        public int totalPages;
        @SerializedName("items")
        public ArrayList<Work> items;
        @SerializedName("hasPreviousPage")
        public boolean hasPreviousPage;
        @SerializedName("hasNextPage")
        public boolean hasNextPage;
        @SerializedName("extraData")
        public String extraData;
    }
}
