package com.bys.sangngoc.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import com.bys.sangngoc.R;
import com.bys.sangngoc.api.ApiListener;
import com.bys.sangngoc.api.models.GetProfileOutput;
import com.bys.sangngoc.api.models.LoginOutput;
import com.bys.sangngoc.api.objects.LoginInput;
import com.bys.sangngoc.models.User;
import com.bys.sangngoc.services.GPSService;
import com.bys.sangngoc.tasks.BaseTask;
import com.bys.sangngoc.tasks.GetGeneralDataTask;
import com.bys.sangngoc.tasks.GetProfileTask;
import com.bys.sangngoc.tasks.LoginTask;
import com.bys.sangngoc.utils.Constants;
import com.bys.sangngoc.utils.SharedPreferenceHelper;
import com.bys.sangngoc.views.CustomDialogAbsentConfirm;
import com.bys.sangngoc.views.CustomDialogListProductConfirm;

/**
 * Created by Admin on 3/2/2018.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener, ApiListener {
    private String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE};
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    private EditText mEdtUsername, mEdtPassword;
    private Button mBtnLogin;
    private TextView mTvForgotPassword;

    @Override
    protected int initLayout() {
        return R.layout.activity_login;
    }

    @Override
    protected void initComponents() {
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions();
        }
        mEdtUsername = (EditText) findViewById(R.id.edt_username);
        mEdtPassword = (EditText) findViewById(R.id.edt_password);
        mBtnLogin = (Button) findViewById(R.id.btn_login);
        mTvForgotPassword = (TextView) findViewById(R.id.tv_forgot_password);

        checkLogin();
    }

    public void checkLogin() {
        String userJson = SharedPreferenceHelper.getInstance(this).get(Constants.PREF_USER_PROFILE);
        if (userJson != null && userJson.length() > 0) {
            User user = new Gson().fromJson(userJson, User.class);
            if (user != null) {
                mEdtUsername.setText(SharedPreferenceHelper.getInstance(this).get(Constants.PREF_USERNAME));
                mEdtPassword.setText(SharedPreferenceHelper.getInstance(this).get(Constants.PREF_PASSWORD_NAME));
                showLoading(true);
                new LoginTask(this, new LoginInput(mEdtUsername.getText().toString().trim(), mEdtPassword.getText().toString().trim()), this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    }

    @Override
    protected void addListener() {
        mBtnLogin.setOnClickListener(this);
        mTvForgotPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_forgot_password:
                startActivity(new Intent(this, ForgotPasswordActivity.class));
                break;
            case R.id.btn_login:
                if (valid()) {
                    showLoading(true);
                    new LoginTask(this, new LoginInput(mEdtUsername.getText().toString().trim(), mEdtPassword.getText().toString().trim()), this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                break;
        }
    }

    public boolean valid() {
        if (mEdtUsername.getText().toString().trim().length() == 0) {
            toast(R.string.txt_warning_plz_input_username);
            return false;
        }
        if (mEdtPassword.getText().toString().trim().length() == 0) {
            toast(R.string.txt_warning_plz_input_password);
            return false;
        }
        return true;
    }

    private void requestPermissions() {
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
        } else {
            startService(new Intent(this, GPSService.class));
        }
    }

    public boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                if (!hasPermissions(this, PERMISSIONS)) {
                    ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onConnectionOpen(BaseTask task) {

    }

    @Override
    public void onConnectionSuccess(BaseTask task, Object data) {
        if (task instanceof LoginTask) {
            LoginOutput output = (LoginOutput) data;
            if (output.success) {
//                SharedPreferenceHelper.getInstance(this).set(Constants.PREF_ROLE, output.result.role);
                SharedPreferenceHelper.getInstance(this).setInt(Constants.PREF_USER_ID, output.result.userId);
                SharedPreferenceHelper.getInstance(this).set(Constants.PREF_SESSION_ID, output.result.tokenType + " " + output.result.accessToken);
                new GetProfileTask(this, output.result.employeeId, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                showLoading(false);
                showAlert(getString(R.string.txt_warning_login_fail));
            }
        } else if (task instanceof GetProfileTask) {
            GetProfileOutput output = (GetProfileOutput) data;
            if (output.success) {
                SharedPreferenceHelper.getInstance(this).set(Constants.PREF_USER_PROFILE, new Gson().toJson(output.result));
                SharedPreferenceHelper.getInstance(this).setInt(Constants.PREF_EMPLOYEES_ID, output.result.getId());
                SharedPreferenceHelper.getInstance(this).set(Constants.PREF_USERNAME, mEdtUsername.getText().toString());
                SharedPreferenceHelper.getInstance(this).set(Constants.PREF_PASSWORD_NAME, mEdtPassword.getText().toString());
                new GetGeneralDataTask(this, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                showAlert(getString(R.string.txt_warning_login_fail));
            }
        } else if (task instanceof GetGeneralDataTask) {
            showLoading(false);
            if (data != null && data instanceof String) {
                SharedPreferenceHelper.getInstance(this).set(Constants.PREF_GENERAL_DATA, (String) data);
            }
            finish();
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra(Constants.EXTRAX_ROLE, Constants.ROLE_DRIVER);
            startActivity(i);
        }
    }

    @Override
    public void onConnectionError(BaseTask task, Exception exception) {
        if (task instanceof LoginTask || task instanceof GetProfileTask) {
            showLoading(false);
            showAlert(exception);
        } else if (task instanceof GetGeneralDataTask) {
            finish();
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra(Constants.EXTRAX_ROLE, Constants.ROLE_DRIVER);
            startActivity(i);
        }
    }
}
