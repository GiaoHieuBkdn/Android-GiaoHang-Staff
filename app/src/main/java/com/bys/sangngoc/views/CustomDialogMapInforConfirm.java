package com.bys.sangngoc.views;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.bys.sangngoc.R;
import com.bys.sangngoc.activities.drivers.DetailDriverDeliveryActivity;
import com.bys.sangngoc.models.DeliveryPoint;
import com.bys.sangngoc.utils.Constants;
import com.bys.sangngoc.utils.StringUtils;

public class CustomDialogMapInforConfirm extends Dialog implements View.OnClickListener {
    public OnBtnClickListener mOnBtnClickListener;
    private Context mContext;
    private View mViewCall, mViewShowDetail;
    private DeliveryPoint mDeliveryPoint;
    private TextView mTvStatus;
    private TextView mTvPointNo;
    private TextView mTvCustomerName;
    private TextView mTvDate;
    private TextView mTvAddress;
    private TextView mTvNote;

    public CustomDialogMapInforConfirm(Context context, DeliveryPoint deliveryPoint) {
        super(context);
        mContext = context;
        mDeliveryPoint = deliveryPoint;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_map_infor);
        Window window = this.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.dimAmount = 0.0f;
        window.setAttributes(wlp);

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.getWindow().setAttributes(wlp);
        init();
    }

    public void init() {
        mTvStatus = (TextView) findViewById(R.id.tv_status);
        mTvPointNo = (TextView) findViewById(R.id.tv_point_no);
        mTvDate = (TextView) findViewById(R.id.tv_date);
        mTvCustomerName = (TextView) findViewById(R.id.tv_customer_name);
        mTvAddress = (TextView) findViewById(R.id.tv_address);
        mTvNote = (TextView) findViewById(R.id.tv_note);

        mViewCall = findViewById(R.id.view_call);
        mViewShowDetail = findViewById(R.id.view_show_detail);
        mViewCall.setOnClickListener(this);
        mViewShowDetail.setOnClickListener(this);

        mTvPointNo.setText(StringUtils.getString(mContext, mDeliveryPoint.getDeliveryPointNo()));
        mTvCustomerName.setText(StringUtils.getString(mContext, mDeliveryPoint.getDeliveryAddressInfo() != null ? mDeliveryPoint.getDeliveryAddressInfo().getContactName() : ""));
        mTvDate.setText(StringUtils.getFullDate2StringFromTimestamp(mDeliveryPoint.getExpectedDeliveryTime() * 1000));
        mTvAddress.setText(StringUtils.getString(mContext, mDeliveryPoint.getDeliveryAddressInfo() != null ? mDeliveryPoint.getDeliveryAddressInfo().getAddress() : ""));
        mTvNote.setText(StringUtils.getString(mContext, mDeliveryPoint.getNotes()));

        if (mDeliveryPoint.getStatus().equalsIgnoreCase(Constants.STATUS_PROCESSING)) {
            mTvStatus.setBackgroundResource(R.drawable.bg_status_blue);
            mTvStatus.setText(mContext.getString(R.string.txt_status_progressing));
            mTvPointNo.setTextColor(ContextCompat.getColor(mContext, R.color.color_status_blue));
        } else if (mDeliveryPoint.getStatus().equalsIgnoreCase(Constants.STATUS_DONT_PROCESSING)) {
            mTvStatus.setBackgroundResource(R.drawable.bg_status_orange);
            mTvStatus.setText(mContext.getString(R.string.txt_status_dont_progressing));
            mTvPointNo.setTextColor(ContextCompat.getColor(mContext, R.color.color_status_orange));
        }
    }


    public void setOnBtnClickListener(OnBtnClickListener listener) {
        this.mOnBtnClickListener = listener;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.view_call:
                TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
                if (tm.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
                    Toast.makeText(mContext, mContext.getString(R.string.txt_device_not_support_call), Toast.LENGTH_SHORT).show();
                } else {
                    if (mDeliveryPoint.getDeliveryAddressInfo() != null && mDeliveryPoint.getDeliveryAddressInfo().getContactPhone() != null && mDeliveryPoint.getDeliveryAddressInfo().getContactPhone().length() > 0) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + mDeliveryPoint.getDeliveryAddressInfo().getContactPhone()));
                        mContext.startActivity(intent);
                    } else {
                        Toast.makeText(mContext, mContext.getString(R.string.txt_not_have_phone), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.view_show_detail:
                Intent i = new Intent(mContext, DetailDriverDeliveryActivity.class);
                i.putExtra(Constants.EXTRAX_DELIVERY_POINT, mDeliveryPoint);
                mContext.startActivity(i);
                break;
        }
    }

    public interface OnBtnClickListener {
        void onButtonClick(String string);
    }

}
