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
import com.bys.sangngoc.tasks.ForgotGetCodeByEmailTask;
import com.bys.sangngoc.utils.Constants;
import com.bys.sangngoc.utils.StringUtils;

/**
 * Created by Admin on 3/2/2018.
 */

public class ForgotPasswordActivity extends BaseActivity implements View.OnClickListener, ApiListener{
    private Button mBtnCancel, mBtnConfirm;
    public static int RQ_ENTER_CODE = 9898;
    private EditText mEdtEmail;

    @Override
    protected int initLayout() {
        return R.layout.activity_forgot_password;
    }

    @Override
    protected void initComponents() {
        setTitle(getString(R.string.txt_get_passowrd));
        showNavLeft(R.drawable.ic_back, this);

        mBtnCancel = (Button) findViewById(R.id.btn_cancel);
        mBtnConfirm = (Button) findViewById(R.id.btn_confirm);
        mEdtEmail = (EditText) findViewById(R.id.edt_email);
    }

    @Override
    protected void addListener() {
        mBtnCancel.setOnClickListener(this);
        mBtnConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imv_nav_left:
                finish();
                break;
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.btn_confirm:
                String email = mEdtEmail.getText().toString().trim();
                if(email.length() == 0){
                    toast(getString(R.string.txt_plz_input_email));
                    return;
                }
                if(!StringUtils.isValidEmail(email)){
                    toast(getString(R.string.txt_email_invalid));
                    return;
                }
                showLoading(true);
                new ForgotGetCodeByEmailTask(this, email, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
        if (task instanceof ForgotGetCodeByEmailTask){
            showLoading(false);
            BaseOutput output = (BaseOutput) data;
            if(output != null) {
                if(output.success) {
                    Intent i = new Intent(this, EnterCodeForgotPasswordActivity.class);
                    i.putExtra(Constants.EXTRAX_EMAIL, mEdtEmail.getText().toString().trim());
                    startActivityForResult(i, RQ_ENTER_CODE);
                } else {
                    showAlert(getString(R.string.txt_email_not_exist));
                }
            } else {
                showAlert(getString(R.string.err_unexpected_exception));
            }
        }
    }

    @Override
    public void onConnectionError(BaseTask task, Exception exception) {
        if (task instanceof ForgotGetCodeByEmailTask) {
            showLoading(false);
            showAlert(exception);
        }
    }
}
