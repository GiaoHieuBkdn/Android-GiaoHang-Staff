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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import com.bys.sangngoc.R;
import com.bys.sangngoc.adapters.ListProductAdapter;

public class CustomDialogListProductConfirm extends Dialog {
    private ImageView mLine;
    private LinearLayout mLayoutNote;
    private Context mContext;
    private RecyclerView mRcDeliveryPoints;
    private ListProductAdapter mAdapter;
    private ArrayList<String> mData = new ArrayList<>();
    private Button mBtnCancel;
    private TextView mTvTitle, mTvNote;
    public CustomDialogListProductConfirm(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_list_product_dialog);
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
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mBtnCancel = (Button) findViewById(R.id.btn_cancel);
        mRcDeliveryPoints = (RecyclerView) findViewById(R.id.rc_content);
        mRcDeliveryPoints.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mAdapter = new ListProductAdapter(mContext, mData);
        mRcDeliveryPoints.setAdapter(mAdapter);

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomDialogListProductConfirm.this.dismiss();
            }
        });
    }
    public void setTitle(String title){
        mTvTitle.setText(title);
    }

    public void loadData(ArrayList<String> stringArrays){
        for (String item : stringArrays){
            mData.add(item);
        }
        mAdapter.notifyDataSetChanged();
    }

}
