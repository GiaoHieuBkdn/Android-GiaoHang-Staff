package com.bys.sangngoc.tasks;

import android.content.Context;

import com.bys.sangngoc.api.ApiListener;
import com.bys.sangngoc.api.models.GetCommentsListOutput;
import com.bys.sangngoc.api.models.GetCommentsListOutput;

/**
 * Created by dcmen
 */
public class GetCommentsOfTaskTask extends BaseTask<GetCommentsListOutput> {
    private int mStart, mLimit;
    private int mTaskId;

    public GetCommentsOfTaskTask(Context context, int taskId, int start, int limit, ApiListener<GetCommentsListOutput> listener) {
        super(context, listener);
        this.mTaskId = taskId;
        this.mStart = start;
        this.mLimit = limit;
    }

    @Override
    protected GetCommentsListOutput callApiMethod() throws Exception {
        return mApi.getCommentsTask(mTaskId, mStart, mLimit);
    }
}
