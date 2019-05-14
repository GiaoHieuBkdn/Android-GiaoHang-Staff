package com.bys.sangngoc.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.bys.sangngoc.R;
import com.bys.sangngoc.utils.Constants;

public class CustomDialogWarningMyLocation extends Dialog {
    private Context mContext;
    private Button mBtnCancel;
    private Button mBtnConfirm;
    private IItemSelectedListener mIItemSelectedListener;

    public CustomDialogWarningMyLocation(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_warning_my_location);
        setCancelable(false);
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

    public void setItemSelectedListener(IItemSelectedListener listener) {
        this.mIItemSelectedListener = listener;
    }

    public void init() {
        mBtnCancel = (Button) findViewById(R.id.btn_cancel);
        mBtnConfirm = (Button) findViewById(R.id.btn_confirm);

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomDialogWarningMyLocation.this.dismiss();
            }
        });
        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    public interface IItemSelectedListener {
        public void onSelected(String status);
    }

}
