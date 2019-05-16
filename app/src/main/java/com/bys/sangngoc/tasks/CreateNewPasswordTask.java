package com.bys.sangngoc.tasks;

import android.content.Context;

import com.bys.sangngoc.api.ApiListener;
import com.bys.sangngoc.api.models.BaseOutput;
import com.bys.sangngoc.api.models.BaseOutput;

/**
 * Created by dcmen on 13-Apr-17.
 */
public class CreateNewPasswordTask extends BaseTask<BaseOutput> {
    private String mEmail;
    private String mToken;
    private String mPassword;

    public CreateNewPasswordTask(Context context, String email, String token, String password, ApiListener<BaseOutput> listener) {
        super(context, listener);
        this.mEmail = email;
        this.mToken = token;
        this.mPassword = password;
    }

    @Override
    protected BaseOutput callApiMethod() throws Exception {
        return mApi.createNewPassword(this.mEmail, this.mToken, this.mPassword);
    }
}
