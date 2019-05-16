package com.bys.sangngoc.tasks;

import android.content.Context;

import com.bys.sangngoc.api.ApiListener;
import com.bys.sangngoc.api.models.GetAuditListOutput;
import com.bys.sangngoc.api.models.GetAuditListOutput;

/**
 * Created by dcmen
 */
public class GetAuditTaskListTask extends BaseTask<GetAuditListOutput> {
    private int mTaskId, mStart, mLimit;

    public GetAuditTaskListTask(Context context, int taskId, int start, int limit, ApiListener<GetAuditListOutput> listener) {
        super(context, listener);
        this.mTaskId = taskId;
        this.mStart = start;
        this.mLimit = limit;
    }

    @Override
    protected GetAuditListOutput callApiMethod() throws Exception {
        return mApi.getAudits(mTaskId, mStart, mLimit);
    }
}
