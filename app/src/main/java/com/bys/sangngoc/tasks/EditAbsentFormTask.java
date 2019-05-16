package com.bys.sangngoc.tasks;

import android.content.Context;

import com.bys.sangngoc.api.ApiListener;
import com.bys.sangngoc.api.models.BaseOutput;
import com.bys.sangngoc.api.objects.SubmitAbsentFormInput;

/**
 * Created by dcmen on 13-Apr-17.
 */
public class EditAbsentFormTask extends BaseTask<BaseOutput> {
    private SubmitAbsentFormInput mInput;

    public EditAbsentFormTask(Context context, SubmitAbsentFormInput input, ApiListener<BaseOutput> listener) {
        super(context, listener);
        this.mInput = input;
    }

    @Override
    protected BaseOutput callApiMethod() throws Exception {
        return mApi.editAbsentForm(this.mInput);
    }
}
