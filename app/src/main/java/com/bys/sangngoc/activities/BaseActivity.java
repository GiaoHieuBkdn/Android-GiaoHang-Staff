package com.bys.sangngoc.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.IOException;

import com.bys.sangngoc.R;
import com.bys.sangngoc.api.exception.ApiException;

/**
 * Created by Admin on 3/22/2017.
 */

public abstract class BaseActivity extends AppCompatActivity {
    public ImageView mImvNavRight;
    private ImageView mImvNavLeft, mImvNavRight2;
    private TextView mTvTitle;
    protected Fragment mFragment;
    protected ProgressDialog mProgressDialog;
    private MaterialDialog mAlertDialog;

    protected abstract int initLayout();

    protected abstract void initComponents();

    protected abstract void addListener();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int layoutId = initLayout();
        if (layoutId != 0) {
            setContentView(layoutId);
        }
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getString(R.string.txt_waiting));
        initNavigation();
        initComponents();
        addListener();
    }

    public void initNavigation() {
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        if (mTvTitle != null) {
            mImvNavLeft = (ImageView) findViewById(R.id.imv_nav_left);
            mImvNavRight = (ImageView) findViewById(R.id.imv_nav_right);
            mImvNavRight2 = (ImageView) findViewById(R.id.imv_nav_right2);
            mTvTitle.setSelected(true);
        }
    }

    public void showNavigation(ImageView imageView, int resId, View.OnClickListener listener) {
        if (imageView != null) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(resId);
            if (listener != null) {
                imageView.setOnClickListener(listener);
            }
        }
    }

    public void showNavLeft(int resId, View.OnClickListener listener) {
        showNavigation(mImvNavLeft, resId, listener);
    }

    public void showNavRight(int resId, View.OnClickListener listener) {
        showNavigation(mImvNavRight, resId, listener);
    }

    public void hiddenNavRight() {
        mImvNavRight.setVisibility(View.GONE);
    }

    public void showNavRight2(int resId, View.OnClickListener listener) {
        showNavigation(mImvNavRight2, resId, listener);
    }

    public void setTitle(String title) {
        if (mTvTitle != null) {
            mTvTitle.setText(title);
        }
    }

    public void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void toast(int messageId) {
        Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show();
    }

    public void showLoading(boolean isShow) {
        try {
            if (isShow) {
                mProgressDialog.show();
            } else {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }
        } catch (IllegalArgumentException ex) {
        }
    }

    public void setNewPage(Fragment fragment) {
        try {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
                    getSupportFragmentManager().popBackStackImmediate();
                }
            }
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_main, fragment, "currentFragment");
            transaction.commitAllowingStateLoss();
            if (mFragment != null)
                transaction.remove(mFragment);
            mFragment = fragment;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.trans_right_to_left_in, R.anim.trans_right_to_left_out,
                        R.anim.trans_left_to_right_in, R.anim.trans_left_to_right_out)
                .replace(R.id.frame_main, fragment)
                .addToBackStack(null)
                .commit();
    }

    public void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().popBackStackImmediate(R.id.frame_main, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_main, fragment)
                .commit();
    }

    @Override
    public void finish() {
        hideKeyBoard();
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void startActivity(Intent intent) {
        // TODO Auto-generated method stub
        super.startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    protected void hideKeyBoard() {
        try {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    try {
                        InputMethodManager inputManager = (InputMethodManager) BaseActivity.this
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(
                                BaseActivity.this.getCurrentFocus().getApplicationWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                    } catch (IllegalStateException e) {
                    } catch (Exception e) {
                    }
                }
            });

        } catch (IllegalStateException e) {
            // TODO: handle exception
        } catch (Exception e) {
        }
    }

    public void showAlert(Exception e) {
        if (e instanceof ApiException)
            showAlert(((ApiException) e).getMessage(this));
        else if (e instanceof IOException)
            showAlert(R.string.err_network_available);
        else
            showAlert(R.string.err_unexpected_exception);
    }

    public void showAlert(int messageId) {
        showAlert(getString(messageId));
    }

    public void showAlert(String message) {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }

        mAlertDialog = new MaterialDialog.Builder(this)
                .content(message)
                .positiveText(getString(R.string.txt_ok))
                .cancelable(false)
                .build();
        mAlertDialog.show();
    }
}
