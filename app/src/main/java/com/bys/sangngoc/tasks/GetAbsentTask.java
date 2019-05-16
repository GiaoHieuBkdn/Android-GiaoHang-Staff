package com.bys.sangngoc.tasks;

import android.content.Context;

import com.bys.sangngoc.api.ApiListener;
import com.bys.sangngoc.api.models.AbsentListOutput;

/**
 * Created by dcmen
 */
public class GetAbsentTask extends BaseTask<AbsentListOutput> {
    private int mUserId;
    private int mPage, mLimit;

    public GetAbsentTask(Context context, int userId, int page, int limit, ApiListener<AbsentListOutput> listener) {
        super(context, listener);
        this.mUserId = userId;
        this.mPage = page;
        this.mLimit = limit;
    }

    @Override
    protected AbsentListOutput callApiMethod() throws Exception {
        return mApi.getListAbsent(this.mUserId, this.mPage, this.mLimit);
    }
}
