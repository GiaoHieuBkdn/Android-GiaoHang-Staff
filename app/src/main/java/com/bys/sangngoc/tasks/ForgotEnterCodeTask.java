package com.bys.sangngoc.tasks;

import android.content.Context;

import com.bys.sangngoc.api.ApiListener;
import com.bys.sangngoc.api.models.ValidateCodeOutput;
import com.bys.sangngoc.api.models.ValidateCodeOutput;

/**
 * Created by dcmen on 13-Apr-17.
 */
public class ForgotEnterCodeTask extends BaseTask<ValidateCodeOutput> {
    private String mEmail;
    private String mCode;

    public ForgotEnterCodeTask(Context context, String email, String code, ApiListener<ValidateCodeOutput> listener) {
        super(context, listener);
        this.mEmail = email;
        this.mCode = code;
    }

    @Override
    protected ValidateCodeOutput callApiMethod() throws Exception {
        return mApi.enterCodeForgotPassword(this.mEmail, this.mCode);
    }
}
