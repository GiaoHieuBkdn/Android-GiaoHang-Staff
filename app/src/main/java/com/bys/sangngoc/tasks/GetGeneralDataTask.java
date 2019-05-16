package com.bys.sangngoc.tasks;

import android.content.Context;

import com.bys.sangngoc.api.ApiListener;

/**
 * Created by dcmen
 */
public class GetGeneralDataTask extends BaseTask<String> {
    public GetGeneralDataTask(Context context, ApiListener<String> listener) {
        super(context, listener);
    }

    @Override
    protected String callApiMethod() throws Exception {
        return mApi.getGeneralData();
    }
}
