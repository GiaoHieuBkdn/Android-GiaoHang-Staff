package com.bys.sangngoc.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;

import com.bys.sangngoc.R;
import com.bys.sangngoc.adapters.ListProductAdapter;
import com.bys.sangngoc.utils.Constants;

public class CustomDialogChooseStatusWork extends Dialog {
    private Context mContext;
    private Button mBtnCancel;
    private Button mBtnConfirm;
    private RadioGroup mRadioGroup;
    private RadioButton mRdNew, mRdProcessing, mRdChecking;
    private IItemSelectedListener mIItemSelectedListener;

    public CustomDialogChooseStatusWork(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_choose_status_work);
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
        mRadioGroup = (RadioGroup) findViewById(R.id.rd_group_status);
        mRdNew = (RadioButton) findViewById(R.id.rd_new);
        mRdProcessing = (RadioButton) findViewById(R.id.rd_processing);
        mRdChecking = (RadioButton) findViewById(R.id.rd_checking);

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomDialogChooseStatusWork.this.dismiss();
            }
        });
        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRadioGroup.getCheckedRadioButtonId() == mRdNew.getId()) {
                    CustomDialogChooseStatusWork.this.dismiss();
                    if(mIItemSelectedListener != null){
                        mIItemSelectedListener.onSelected(Constants.STATUS_WORK_WILL_PROCESS);
                    }
                } else if (mRadioGroup.getCheckedRadioButtonId() == mRdProcessing.getId()) {
                    CustomDialogChooseStatusWork.this.dismiss();
                    if(mIItemSelectedListener != null){
                        mIItemSelectedListener.onSelected(Constants.STATUS_WORK_PROCESSING);
                    }
                } else if (mRadioGroup.getCheckedRadioButtonId() == mRdChecking.getId()) {
                    CustomDialogChooseStatusWork.this.dismiss();
                    if(mIItemSelectedListener != null){
                        mIItemSelectedListener.onSelected(Constants.STATUS_WORK_CHECKING);
                    }
                } else {
                    Toast.makeText(mContext, mContext.getString(R.string.txt_plz_choose_status_work), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public interface IItemSelectedListener {
        public void onSelected(String status);
    }

}
