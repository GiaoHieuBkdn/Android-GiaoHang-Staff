package com.bys.sangngoc.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bys.sangngoc.R;

public class CustomDialogAbsentConfirm extends Dialog {
    public OnBtnClickListener mOnBtnClickListener;
    private Button mBtnLeft, mBtnRight;
    private Context mContext;
    private EditText mEdtReason;
    private TextView mTvTitle;

    public CustomDialogAbsentConfirm(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_absent_confirm_dialog);
        Window window = this.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.getWindow().setAttributes(wlp);
        init();
    }

    public void init() {
        mBtnLeft = (Button) findViewById(R.id.btn_left);
        mBtnRight = (Button) findViewById(R.id.btn_right);
        mEdtReason = (EditText) findViewById(R.id.edt_reason);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mBtnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnBtnClickListener != null) {
                    mOnBtnClickListener.onButtonClick(mEdtReason.getText().toString().trim());
                    CustomDialogAbsentConfirm.this.dismiss();
                }
            }
        });
        mBtnLeft.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
               CustomDialogAbsentConfirm.this.dismiss();
            }
        });
    }

    public void hiddenEdittext(){
        mEdtReason.setVisibility(View.GONE);
    }

    public void setTitle(String title) {
        mTvTitle.setText(title);
    }

    public void setButtonsText(String btnLeft, String btnRight){
        mBtnLeft.setText(btnLeft);
        mBtnRight.setText(btnRight);
    }

    public void setOnBtnClickListener(OnBtnClickListener listener) {
        this.mOnBtnClickListener = listener;
    }

    public interface OnBtnClickListener {
        void onButtonClick(String string);
    }

}
