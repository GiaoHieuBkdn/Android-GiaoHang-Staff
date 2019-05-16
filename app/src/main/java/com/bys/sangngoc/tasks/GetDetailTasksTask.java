package com.bys.sangngoc.tasks;

import android.content.Context;

import com.bys.sangngoc.api.ApiListener;
import com.bys.sangngoc.api.models.GetTaskOutput;
import com.bys.sangngoc.api.models.GetTaskOutput;

/**
 * Created by dcmen
 */
public class GetDetailTasksTask extends BaseTask<GetTaskOutput> {
    private int mTaskId;

    public GetDetailTasksTask(Context context, int taskId, ApiListener<GetTaskOutput> listener) {
        super(context, listener);
        this.mTaskId = taskId;
    }

    @Override
    protected GetTaskOutput callApiMethod() throws Exception {
        return mApi.getDetailTasks(mTaskId);
    }
}
