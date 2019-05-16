package com.bys.sangngoc.tasks;

import android.content.Context;

import com.bys.sangngoc.api.ApiListener;
import com.bys.sangngoc.api.models.BaseOutput;
import com.bys.sangngoc.api.models.BaseOutput;
import com.bys.sangngoc.api.objects.LoginInput;

/**
 * Created by dcmen on 13-Apr-17.
 */
public class ForgotGetCodeByEmailTask extends BaseTask<BaseOutput> {
    private String mEmail;

    public ForgotGetCodeByEmailTask(Context context, String email, ApiListener<BaseOutput> listener) {
        super(context, listener);
        this.mEmail = email;
    }

    @Override
    protected BaseOutput callApiMethod() throws Exception {
        return mApi.getCodeByEmailInForgotPassword(this.mEmail);
    }
}
