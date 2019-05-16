package com.bys.sangngoc.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bys.sangngoc.R;
import com.bys.sangngoc.api.ApiListener;
import com.bys.sangngoc.api.models.BaseOutput;
import com.bys.sangngoc.tasks.BaseTask;
import com.bys.sangngoc.tasks.CreateNewPasswordTask;
import com.bys.sangngoc.utils.Constants;

/**
 * Created by Admin on 3/2/2018.
 */

public class GetNewPasswordActivity extends BaseActivity implements View.OnClickListener, ApiListener{
    private Button mBtnCancel, mBtnAgree;
    public static int RQ_ENTER_CODE = 9898;
    private EditText mEdtPassword, mEdtPasswordConfirm;
    private String mEmail, mToken;

    @Override
    protected int initLayout() {
        return R.layout.activity_get_new_password;
    }

    @Override
    protected void initComponents() {
        setTitle(getString(R.string.txt_get_passowrd));
        showNavLeft(R.drawable.ic_back, this);

        mEmail = getIntent().getStringExtra(Constants.EXTRAX_EMAIL);
        mToken = getIntent().getStringExtra(Constants.EXTRAX_TOKEN_CODE);
        mBtnCancel = (Button) findViewById(R.id.btn_cancel);
        mBtnAgree = (Button) findViewById(R.id.btn_agree);
        mEdtPassword = (EditText) findViewById(R.id.edt_password);
        mEdtPasswordConfirm = (EditText) findViewById(R.id.edt_password_confirm);
    }

    @Override
    protected void addListener() {
        mBtnCancel.setOnClickListener(this);
        mBtnAgree.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imv_nav_left:
                finish();
                break;
            case R.id.btn_cancel:
                setResult(RESULT_OK);
                finish();
                break;
            case R.id.btn_agree:
                String password =  mEdtPassword.getText().toString().trim();
                String passwordConfirm = mEdtPasswordConfirm.getText().toString().trim();
                if(password.length() == 0){
                    toast(getString(R.string.txt_warning_plz_input_new_password));
                    return;
                }
                if(!password.equals(passwordConfirm)){
                    toast(getString(R.string.txt_confirm_password_incorrect));
                    return;
                }

                showLoading(true);
                new CreateNewPasswordTask(this, mEmail, mToken, password, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RQ_ENTER_CODE && resultCode == RESULT_OK){
            finish();
        }
    }

    @Override
    public void onConnectionOpen(BaseTask task) {

    }

    @Override
    public void onConnectionSuccess(BaseTask task, Object data) {
        if (task instanceof CreateNewPasswordTask){
            showLoading(false);
            BaseOutput output = (BaseOutput) data;
            if(output != null) {
                if(output.success) {
                    toast(R.string.txt_change_password_successfully);
                    setResult(RESULT_OK);
                    finish();
                } else {
                    showAlert(getString(R.string.txt_warning_cannot_change_password));
                }
            } else {
                showAlert(getString(R.string.err_unexpected_exception));
            }
        }
    }

    @Override
    public void onConnectionError(BaseTask task, Exception exception) {
        if (task instanceof CreateNewPasswordTask) {
            showLoading(false);
            showAlert(exception);
        }
    }
}
