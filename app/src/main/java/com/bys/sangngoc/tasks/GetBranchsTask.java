package com.bys.sangngoc.tasks;

import android.content.Context;

import com.bys.sangngoc.api.ApiListener;
import com.bys.sangngoc.api.models.BranchOutput;

/**
 * Created by dcmen
 */
public class GetBranchsTask extends BaseTask<BranchOutput> {

    public GetBranchsTask(Context context, ApiListener<BranchOutput> listener) {
        super(context, listener);
    }

    @Override
    protected BranchOutput callApiMethod() throws Exception {
        return mApi.getBranchs();
    }
}
