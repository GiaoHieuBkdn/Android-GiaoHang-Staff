package com.bys.sangngoc.tasks;

import android.content.Context;

import com.bys.sangngoc.api.ApiListener;
import com.bys.sangngoc.api.models.AbsentDetailOutput;

/**
 * Created by dcmen
 */
public class GetDetailAbsentTask extends BaseTask<AbsentDetailOutput> {
    private int mUserId;
    private int mAbsentId;

    public GetDetailAbsentTask(Context context, int userId, int absentId, ApiListener<AbsentDetailOutput> listener) {
        super(context, listener);
        this.mUserId = userId;
        this.mAbsentId = absentId;
    }

    @Override
    protected AbsentDetailOutput callApiMethod() throws Exception {
        return mApi.getDetailAbsent(this.mUserId, this.mAbsentId);
    }
}
