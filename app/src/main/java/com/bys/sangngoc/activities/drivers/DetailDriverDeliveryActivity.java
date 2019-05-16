package com.bys.sangngoc.activities.drivers;

import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

import com.bys.sangngoc.R;
import com.bys.sangngoc.activities.BaseActivity;
import com.bys.sangngoc.adapters.DetailDriveDeliveryAdapter;
import com.bys.sangngoc.api.ApiListener;
import com.bys.sangngoc.api.models.BaseOutput;
import com.bys.sangngoc.api.models.GetDetailDeliveryPointOutput;
import com.bys.sangngoc.api.models.GetGeneralDataOutput;
import com.bys.sangngoc.models.DeliveryItem;
import com.bys.sangngoc.models.DeliveryPoint;
import com.bys.sangngoc.models.InforItem;
import com.bys.sangngoc.models.KeyValue;
import com.bys.sangngoc.tasks.BaseTask;
import com.bys.sangngoc.tasks.GetDetailDeliveryPointTask;
import com.bys.sangngoc.tasks.SwitchToProcessDeliveryTask;
import com.bys.sangngoc.utils.Constants;
import com.bys.sangngoc.utils.FileUtils;
import com.bys.sangngoc.utils.SharedPreferenceHelper;
import com.bys.sangngoc.utils.StringUtils;

/**
 * Created by Admin on 3/6/2018.
 */

public class DetailDriverDeliveryActivity extends BaseActivity implements View.OnClickListener, ApiListener {
    public static int REQUEST_REPORT_TROUBLE = 2229;
    public static int REQUEST_REPORT_DELIVERY = 2228;
    private RecyclerView mRcContent;
    private DetailDriveDeliveryAdapter mAdapter;
    private ArrayList<InforItem> mData = new ArrayList<>();
    private View mGroupButtons;
    private Button mBtnActionLeft, mBtnActionRight;
    private View mViewStatus;
    private DeliveryPoint mDeliveryPoint;
    private View mViewPhotos;
    private TextView mTvViewPhotos;
    private ArrayList<DeliveryItem> productsList = new ArrayList<>();

    @Override
    protected int initLayout() {
        return R.layout.activity_detail_driver_delivery;
    }

    @Override
    protected void initComponents() {
        mViewStatus = findViewById(R.id.view_status);
        if (getIntent().hasExtra(Constants.EXTRAX_DELIVERY_POINT)) {
            mDeliveryPoint = (DeliveryPoint) getIntent().getSerializableExtra(Constants.EXTRAX_DELIVERY_POINT);

            setTitle(getString(R.string.txt_detail_delivery));
            showNavLeft(R.drawable.ic_back, this);
            showNavRight(R.drawable.ic_location_nav, this);

            mBtnActionLeft = (Button) findViewById(R.id.btn_left);
            mBtnActionRight = (Button) findViewById(R.id.btn_right);
            mGroupButtons = findViewById(R.id.group_buttons);
            mGroupButtons.setVisibility(View.GONE);
            mViewPhotos = findViewById(R.id.view_photo);
            mViewPhotos.setVisibility(View.GONE);
            mRcContent = (RecyclerView) findViewById(R.id.rc_content);
            mRcContent.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            mAdapter = new DetailDriveDeliveryAdapter(this, mData);
            mRcContent.setAdapter(mAdapter);
            mTvViewPhotos = (TextView) findViewById(R.id.tv_view_photos);
            mTvViewPhotos.setPaintFlags(mTvViewPhotos.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            showLoading(true);
            new GetDetailDeliveryPointTask(this, mDeliveryPoint.getId(), this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    public void loadData() {
        mViewPhotos.setVisibility(View.GONE);
        mData.clear();
        mData.add(new InforItem(getString(R.string.txt_delivery_code), StringUtils.getString(this, mDeliveryPoint.getDeliveryPointNo()), InforItem.Action.STATUS, mDeliveryPoint.getStatus()));
        mData.add(new InforItem(getString(R.string.txt_order_code), StringUtils.getString(this, mDeliveryPoint.getSaleOrderNo())));
        mData.add(new InforItem(getString(R.string.txt_customer_code), StringUtils.getString(this, mDeliveryPoint.getDeliveryToCustomer() != null ? mDeliveryPoint.getDeliveryToCustomer().getCustomerNo() : "")));
        mData.add(new InforItem(getString(R.string.txt_customer_name), StringUtils.getString(this, mDeliveryPoint.getDeliveryAddressInfo() != null ? mDeliveryPoint.getDeliveryAddressInfo().getContactName() : "")));
        mData.add(new InforItem(getString(R.string.txt_address_2), StringUtils.getString(this, mDeliveryPoint.getDeliveryAddressInfo() != null ? mDeliveryPoint.getDeliveryAddressInfo().getAddress() : "")));
        mData.add(new InforItem(getString(R.string.txt_phone_number), StringUtils.getString(this, StringUtils.getString(this, mDeliveryPoint.getDeliveryAddressInfo() != null ? mDeliveryPoint.getDeliveryAddressInfo().getContactPhone() : "")), InforItem.Action.CALL));
        String productListName = "";
        if (mDeliveryPoint.getDeliveryItems() != null) {
            for (DeliveryItem items : mDeliveryPoint.getDeliveryItems()) {
                if (productListName.length() == 0) {
                    productListName += items.getProductDesc();
                } else {
                    productListName += ", " + items.getProductDesc();
                }
                try {
                    items.setQuantityTmp(items.getProductQtyOfTypes().get(0).getProductDeliveryQty()
                            + items.getProductQtyOfTypes().get(1).getProductDeliveryQty()
                            + items.getProductQtyOfTypes().get(2).getProductDeliveryQty());
                }catch (NullPointerException ex){

                }
                productsList.add(items);
            }
        }
//        if (productListName.length() > 13) {
//            productListName = productListName.substring(0, 12) + "...";
//        }
        if (mDeliveryPoint != null && (mDeliveryPoint.getStatus().equalsIgnoreCase(Constants.STATUS_PROCESSED) || mDeliveryPoint.getStatus().equalsIgnoreCase(Constants.STATUS_COMPLETE_NEED_ACCEPT) || mDeliveryPoint.getStatus().equalsIgnoreCase(Constants.STATUS_COMPLETE_ACCEPTED))) {
            mData.add(new InforItem(getString(R.string.txt_product_list), StringUtils.getString(this, productListName), InforItem.Action.LIST));
            mData.add(new InforItem(getString(R.string.txt_order_note), StringUtils.getString(this, mDeliveryPoint.getNotes())));
            mData.add(new InforItem(getString(R.string.txt_product_note_delivery), StringUtils.getString(this, mDeliveryPoint.getFeedback())));
        } else {
            mData.add(new InforItem(getString(R.string.txt_product_list_processing), StringUtils.getString(this, productListName), InforItem.Action.LIST));
            mData.add(new InforItem(getString(R.string.txt_order_note), StringUtils.getString(this, mDeliveryPoint.getNotes())));
            if (mDeliveryPoint != null && mDeliveryPoint.getStatus().equalsIgnoreCase(Constants.STATUS_FAILDED)) {
                mData.add(new InforItem(getString(R.string.txt_product_note_delivery), StringUtils.getString(this, mDeliveryPoint.getFeedback())));
            }
        }
        mData.add(new InforItem(getString(R.string.txt_expect_time), StringUtils.getFullDate2StringFromTimestamp(mDeliveryPoint.getExpectedDeliveryTime() * 1000)));
        //mData.add(new InforItem(getString(R.string.txt_passed_time), StringUtils.getFullDate2StringFromTimestamp(mDeliveryPoint.getActualDeliveryTime() * 1000)));

        if (mDeliveryPoint.getDeliveredStatusInfo() != null && mDeliveryPoint.getStatus().equalsIgnoreCase(Constants.STATUS_PROCESSED) || mDeliveryPoint.getStatus().equalsIgnoreCase(Constants.STATUS_COMPLETE_NEED_ACCEPT) || mDeliveryPoint.getStatus().equalsIgnoreCase(Constants.STATUS_COMPLETE_ACCEPTED)) {
            mData.add(new InforItem(getString(R.string.txt_deliveried_time), StringUtils.getFullDate2StringFromTimestamp(mDeliveryPoint.getDeliveredStatusInfo().getActualDeliveryTime() * 1000)));
        }
        if (mDeliveryPoint.getStatus().equalsIgnoreCase(Constants.STATUS_CANCEL)) {
            mData.add(new InforItem(getString(R.string.txt_why_cancel), StringUtils.getString(this, mDeliveryPoint.getCanceledStatusInfo() != null ? mDeliveryPoint.getCanceledStatusInfo().getCanceledReason() : "")));
        }
        if (mDeliveryPoint.getStatus().equalsIgnoreCase(Constants.STATUS_TROUBLE) && mDeliveryPoint.getIssuedStatusInfo() != null) {
            mData.add(new InforItem(getString(R.string.txt_type_trouble), getIssuesFomType(StringUtils.getString(this, mDeliveryPoint.getIssuedStatusInfo().getIssueType()))));
            mData.add(new InforItem(getString(R.string.txt_description), StringUtils.getString(this, mDeliveryPoint.getIssuedStatusInfo().getIssueDesc())));
        }

        mGroupButtons.setVisibility(View.GONE);
        mViewPhotos.setVisibility(View.GONE);
        if (mDeliveryPoint != null) {
            mViewStatus.setVisibility(View.VISIBLE);
            if (mDeliveryPoint.getStatus().equalsIgnoreCase(Constants.STATUS_PROCESSING)) {
                mViewStatus.setBackgroundColor(ContextCompat.getColor(this, R.color.color_status_blue));
                mGroupButtons.setVisibility(View.VISIBLE);
                mBtnActionLeft.setText(getString(R.string.txt_report_trouble));
                mBtnActionRight.setText(getString(R.string.txt_delivered));
                mBtnActionRight.setVisibility(View.VISIBLE);
            } else if (mDeliveryPoint.getStatus().equalsIgnoreCase(Constants.STATUS_DONT_PROCESSING)) {
                mViewStatus.setBackgroundColor(ContextCompat.getColor(this, R.color.color_status_orange));
                mGroupButtons.setVisibility(View.VISIBLE);
                mBtnActionLeft.setText(getString(R.string.txt_start_processing));
                mBtnActionRight.setVisibility(View.GONE);
            } else if (mDeliveryPoint.getStatus().equalsIgnoreCase(Constants.STATUS_CANCEL)) {
                mViewStatus.setBackgroundColor(ContextCompat.getColor(this, R.color.color_status_dark));
            } else if (mDeliveryPoint.getStatus().equalsIgnoreCase(Constants.STATUS_TROUBLE)) {
                mViewPhotos.setVisibility(View.VISIBLE);
                mTvViewPhotos.setText(getString(R.string.txt_view_photo));
                mViewStatus.setBackgroundColor(ContextCompat.getColor(this, R.color.color_status_red));
                mGroupButtons.setVisibility(View.VISIBLE);
                mBtnActionLeft.setText(getString(R.string.txt_continue_delivery));
                mTvViewPhotos.setText(getString(R.string.txt_view_photo));
                mBtnActionRight.setVisibility(View.GONE);
                mViewPhotos.setVisibility(View.VISIBLE);
            } else if (mDeliveryPoint.getStatus().equalsIgnoreCase(Constants.STATUS_FAILDED)) {
                mViewPhotos.setVisibility(View.VISIBLE);
                mTvViewPhotos.setText(getString(R.string.txt_view_photo));
                mViewStatus.setBackgroundColor(ContextCompat.getColor(this, R.color.color_status_dark));
                mViewPhotos.setVisibility(View.VISIBLE);

            } else if (mDeliveryPoint.getStatus().equalsIgnoreCase(Constants.STATUS_PROCESSED) || mDeliveryPoint.getStatus().equalsIgnoreCase(Constants.STATUS_COMPLETE_NEED_ACCEPT) || mDeliveryPoint.getStatus().equalsIgnoreCase(Constants.STATUS_COMPLETE_ACCEPTED)) {
                mViewPhotos.setVisibility(View.VISIBLE);
                mTvViewPhotos.setText(getString(R.string.txt_view_photo));
                mViewStatus.setBackgroundColor(ContextCompat.getColor(this, R.color.color_status_green));
                mViewPhotos.setVisibility(View.VISIBLE);

            }
        }
        mAdapter.setDeliveryPoint(mDeliveryPoint);
        mAdapter.notifyDataSetChanged();
    }

    public String getIssuesFomType(String type){
        if(type == null){
            return "";
        }
        String filterJson = SharedPreferenceHelper.getInstance(this).get(Constants.PREF_GENERAL_DATA);
        if(filterJson != null && filterJson.length() > 0){
            GetGeneralDataOutput generalData = new Gson().fromJson(filterJson, GetGeneralDataOutput.class);
            if(generalData != null && generalData.result.deliveryIssueType != null){
                for (KeyValue item : generalData.result.deliveryIssueType){
                    if(item.key.equalsIgnoreCase(type)){
                        return item.value;
                    }
                }
            }
        }
        return type;
    }

    @Override
    protected void addListener() {
        mViewPhotos.setOnClickListener(this);
        mBtnActionLeft.setOnClickListener(this);
        mBtnActionRight.setOnClickListener(this);
        mTvViewPhotos.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FileUtils.clearAllImageTmp(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imv_nav_left:
                finish();
                break;
            case R.id.tv_view_photos:
                Intent ii = new Intent(this, ViewPhotoDriverActivity.class);
                ii.putExtra(Constants.EXTRAX_DELIVERY_POINT, mDeliveryPoint);
                startActivity(ii);
                break;
            case R.id.imv_nav_right:
                Intent iii = new Intent(this, MapDeliveryActiviry.class);
                if(mDeliveryPoint != null && mDeliveryPoint.getDeliveryAddressInfo() != null) {
                    iii.putExtra(Constants.EXTRAX_LATITUDE, mDeliveryPoint.getDeliveryAddressInfo().getAddressLatitude());
                    iii.putExtra(Constants.EXTRAX_LONGITUDE, mDeliveryPoint.getDeliveryAddressInfo().getAddressLongitude());
                    iii.putExtra(Constants.EXTRAX_ADDRESS, mDeliveryPoint.getDeliveryAddressInfo().getAddress());
                    iii.putExtra(Constants.EXTRAX_STATUS, mDeliveryPoint.getStatus());
                }

                startActivity(iii);
                break;
            case R.id.view_photo:
                break;
            case R.id.btn_left:
                if (mBtnActionLeft.getText().toString().equalsIgnoreCase(getString(R.string.txt_start_processing))) {
                    showLoading(true);
                    new SwitchToProcessDeliveryTask(this, mDeliveryPoint.getId(), this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else if (mBtnActionLeft.getText().toString().equalsIgnoreCase(getString(R.string.txt_report_trouble))) {
                    Intent i = new Intent(this, ReportTroubleActivity.class);
                    i.putExtra(Constants.EXTRAX_DELIVERY_POINT, mDeliveryPoint);
                    startActivityForResult(i, REQUEST_REPORT_TROUBLE);
                } else if (mBtnActionLeft.getText().toString().equalsIgnoreCase(getString(R.string.txt_continue_delivery))){
                    showLoading(true);
                    new SwitchToProcessDeliveryTask(this, mDeliveryPoint.getId(), this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                break;
            case R.id.btn_right:
                if (mBtnActionRight.getText().toString().equalsIgnoreCase(getString(R.string.txt_delivered))) {
                    Intent i = new Intent(this, ConfirmDeliveredActivity.class);
                    i.putExtra(Constants.EXTRAX_DELIVERY_POINT, mDeliveryPoint);
                    i.putExtra(Constants.EXTRAX_PRODUCTS_LIST, productsList);
                    startActivityForResult(i, REQUEST_REPORT_DELIVERY);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_REPORT_DELIVERY) {
                showLoading(true);
                new GetDetailDeliveryPointTask(this, mDeliveryPoint.getId(), this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else if (requestCode == REQUEST_REPORT_TROUBLE) {
                showLoading(true);
                new GetDetailDeliveryPointTask(this, mDeliveryPoint.getId(), this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    @Override
    public void onConnectionOpen(BaseTask task) {

    }

    @Override
    public void onConnectionSuccess(BaseTask task, Object data) {
        if (task instanceof GetDetailDeliveryPointTask) {
            showLoading(false);
            GetDetailDeliveryPointOutput output = (GetDetailDeliveryPointOutput) data;
            if (output.success) {
                mDeliveryPoint = output.result;
            }
            loadData();
        } else if (task instanceof SwitchToProcessDeliveryTask){
            showLoading(false);
            BaseOutput output = (BaseOutput) data;
            if(output.success) {
                mDeliveryPoint.setStatus(Constants.STATUS_PROCESSING);
                Intent i = new Intent(Constants.BROADCAST_CHANGE_STATUS);
                i.putExtra(Constants.EXTRAX_DELIVERY_POINT, mDeliveryPoint);
                sendBroadcast(i);
                if(productsList !=null) {
                    productsList = new ArrayList<>();
                    loadData();
                } else {
                    loadData();
                }
            } else {
                StringUtils.errorMessageForDelivery(this, output.errorMessage);
            }
        }
    }

    @Override
    public void onConnectionError(BaseTask task, Exception exception) {
        if (task instanceof GetDetailDeliveryPointTask || task instanceof SwitchToProcessDeliveryTask) {
            showLoading(false);
            showAlert(exception);
        }
    }
}
