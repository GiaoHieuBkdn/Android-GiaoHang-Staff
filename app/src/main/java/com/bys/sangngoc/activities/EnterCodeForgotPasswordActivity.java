package com.bys.sangngoc.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bys.sangngoc.R;
import com.bys.sangngoc.api.ApiListener;
import com.bys.sangngoc.api.models.BaseOutput;
import com.bys.sangngoc.api.models.ValidateCodeOutput;
import com.bys.sangngoc.tasks.BaseTask;
import com.bys.sangngoc.tasks.ForgotEnterCodeTask;
import com.bys.sangngoc.tasks.ForgotGetCodeByEmailTask;
import com.bys.sangngoc.utils.Constants;

/**
 * Created by Admin on 3/2/2018.
 */

public class EnterCodeForgotPasswordActivity extends BaseActivity implements View.OnClickListener, ApiListener {
    private EditText mEdtEnterCode;
    private TextView[] tvCodes = new TextView[6];
    private int[] idCodes = new int[]{R.id.tv_code_1, R.id.tv_code_2, R.id.tv_code_3, R.id.tv_code_4, R.id.tv_code_5, R.id.tv_code_6};
    private TextView mTvResendCode;
    private String mEmail;
    private Button mBtnCancel, mBtnContinue;
    private TextView mTvResendCodeWarning;
    private TextView mTvEmail;

    @Override
    protected int initLayout() {
        return R.layout.activity_enter_code_forgot_password;
    }

    @Override
    protected void initComponents() {
        setTitle(getString(R.string.txt_get_passowrd));
        showNavLeft(R.drawable.ic_back, this);

        mEmail = getIntent().getStringExtra(Constants.EXTRAX_EMAIL);
        mTvEmail = (TextView) findViewById(R.id.tv_email);
        mTvEmail.setText(mEmail);
        mTvResendCode = (TextView) findViewById(R.id.tv_resend_code);
        mBtnCancel = (Button) findViewById(R.id.btn_cancel);
        mBtnContinue = (Button) findViewById(R.id.btn_continue);
        mTvResendCodeWarning = (TextView) findViewById(R.id.tv_resend_code_response);

        mTvResendCode.setPaintFlags(mTvResendCode.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        mEdtEnterCode = (EditText) findViewById(R.id.edt_code);
        for (int i = 0; i < tvCodes.length; i++) {
            tvCodes[i] = (TextView) findViewById(idCodes[i]);
            tvCodes[i].setText("");
            tvCodes[i].setOnClickListener(mOnClickListener);
        }
        mEdtEnterCode.requestFocus();
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                InputMethodManager imm = (InputMethodManager) EnterCodeForgotPasswordActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mEdtEnterCode, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 300);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            InputMethodManager imm = (InputMethodManager) EnterCodeForgotPasswordActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(mEdtEnterCode, InputMethodManager.SHOW_IMPLICIT);
            mTvResendCodeWarning.setVisibility(View.GONE);
        }
    };

    @Override
    protected void addListener() {
        mTvResendCode.setOnClickListener(this);
        mBtnContinue.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);
        mEdtEnterCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                fillCode();
                mTvResendCodeWarning.setVisibility(View.GONE);
                if (mEdtEnterCode.getText().toString().length() == 6) {
                    mBtnContinue.setEnabled(true);
                } else {
                    mBtnContinue.setEnabled(false);
                }
            }
        });
    }

    public void fillCode() {
        String codes = mEdtEnterCode.getText().toString();
        for (int i = 0; i < tvCodes.length; i++) {
            if (i < codes.length()) {
                tvCodes[i].setText(String.valueOf(codes.charAt(i)));
            } else {
                tvCodes[i].setText("");
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imv_nav_left:
                finish();
                break;
            case R.id.tv_resend_code:
                showLoading(true);
                new ForgotGetCodeByEmailTask(this, mEmail, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                break;
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.btn_continue:
                if(mEdtEnterCode.getText().toString().length() == 0){
                    toast(R.string.txt_plz_enter_code);
                    return;
                }
                showLoading(true);
                new ForgotEnterCodeTask(EnterCodeForgotPasswordActivity.this, mEmail, mEdtEnterCode.getText().toString(), EnterCodeForgotPasswordActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                break;
        }
    }

    @Override
    public void onConnectionOpen(BaseTask task) {

    }

    @Override
    public void onConnectionSuccess(BaseTask task, Object data) {
        if (task instanceof ForgotEnterCodeTask) {
            showLoading(false);
            ValidateCodeOutput output = (ValidateCodeOutput) data;
            if (output != null) {
                if (output.success) {
                    Intent i = new Intent(this, GetNewPasswordActivity.class);
                    i.putExtra(Constants.EXTRAX_EMAIL, mEmail);
                    i.putExtra(Constants.EXTRAX_TOKEN_CODE, output.token);
                    startActivityForResult(i, ForgotPasswordActivity.RQ_ENTER_CODE);
                } else {
                    showAlert(getString(R.string.txt_enter_code_incorrect));
                }
            } else {
                showAlert(getString(R.string.err_unexpected_exception));
            }
        } else if (task instanceof ForgotGetCodeByEmailTask){
            showLoading(false);
            BaseOutput output = (BaseOutput) data;
            if(output != null) {
                if(output.success) {
                    mEdtEnterCode.setText("");
                    mTvResendCodeWarning.setVisibility(View.VISIBLE);
                } else {
                    showAlert(getString(R.string.txt_email_not_exist_forgot));
                }
            } else {
                showAlert(getString(R.string.err_unexpected_exception));
            }
        }
    }

    @Override
    public void onConnectionError(BaseTask task, Exception exception) {
        if (task instanceof ForgotEnterCodeTask || task instanceof ForgotGetCodeByEmailTask) {
            showLoading(false);
            showAlert(exception);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ForgotPasswordActivity.RQ_ENTER_CODE && resultCode == RESULT_OK){
            setResult(RESULT_OK);
            finish();
        }
    }
}
