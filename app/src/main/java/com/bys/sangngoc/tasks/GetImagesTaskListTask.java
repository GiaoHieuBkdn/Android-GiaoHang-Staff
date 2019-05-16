package com.bys.sangngoc.tasks;

import android.content.Context;

import com.bys.sangngoc.api.ApiListener;
import com.bys.sangngoc.api.models.GetImagesListOutput;
import com.bys.sangngoc.api.models.GetImagesListOutput;

/**
 * Created by dcmen
 */
public class GetImagesTaskListTask extends BaseTask<GetImagesListOutput> {
    private int mTaskId, mStart, mLimit;

    public GetImagesTaskListTask(Context context, int taskId, int start, int limit, ApiListener<GetImagesListOutput> listener) {
        super(context, listener);
        this.mTaskId = taskId;
        this.mStart = start;
        this.mLimit = limit;
    }

    @Override
    protected GetImagesListOutput callApiMethod() throws Exception {
        return mApi.getImagesTask(mTaskId, mStart, mLimit);
    }
}
