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
import android.widget.TextView;

import com.bys.sangngoc.R;
import com.bys.sangngoc.adapters.ListProductAdapter;
import com.bys.sangngoc.adapters.works.ListRealProductsAdapter;
import com.bys.sangngoc.models.DeliveryItem;

import java.util.ArrayList;

public class CustomDialogListRealProductConfirm extends Dialog {

    private Context mContext;
    private RecyclerView mRcDeliveryPoints;
    private ListRealProductsAdapter mAdapter;
    private ArrayList<DeliveryItem> mData = new ArrayList<>();
    private Button mBtnCancel;
    private TextView mTvTitle;
    private boolean isHistory;
    public CustomDialogListRealProductConfirm(Context context, boolean isHistory) {
        super(context);
        this.mContext = context;
        this.isHistory = isHistory;
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
        mAdapter = new ListRealProductsAdapter(mContext, mData, isHistory);
        mRcDeliveryPoints.setAdapter(mAdapter);

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomDialogListRealProductConfirm.this.dismiss();
            }
        });
    }
    public void setTitle(String title){
        mTvTitle.setText(title);
    }

    public void loadData(ArrayList<DeliveryItem> stringArrays){
        for (DeliveryItem item : stringArrays){
            mData.add(item);
        }
        mAdapter.notifyDataSetChanged();
    }

}
