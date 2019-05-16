package com.bys.sangngoc.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import com.bys.sangngoc.R;
import com.bys.sangngoc.activities.MainActivity;
import com.bys.sangngoc.activities.drivers.FilterDriverActivity;
import com.bys.sangngoc.adapters.DeliveryPointsPagerAdapter;
import com.bys.sangngoc.api.ApiListener;
import com.bys.sangngoc.api.models.GetDeliveryPointsListOutput;
import com.bys.sangngoc.listeners.DeliveryPointsListener;
import com.bys.sangngoc.models.DeliveryPoint;
import com.bys.sangngoc.tasks.BaseTask;
import com.bys.sangngoc.tasks.GetDeliveryPointsListTask;
import com.bys.sangngoc.utils.Constants;
import com.bys.sangngoc.utils.SharedPreferenceHelper;
import com.bys.sangngoc.utils.StringUtils;

/**
 * Created by Admin on 3/9/2018.
 */

public class DeliveryPointsFragment extends BaseFragment implements View.OnClickListener, ApiListener {
    private int RQ_FILTER = 2233;
    private Button mBtnListTab, mBtnMapTab, mCurrentBtnTab;
    private ViewPager mVpContent;
    private DeliveryPointsPagerAdapter mVpAdaper;
    private int mStart = 0;
    private boolean mIsLoadMore;
    private boolean mIsLoadedList, mIsLoadedMap;
    private String mCurrentFilterStatus = "";
    private long mCurrentFilterDateDelivery = 0;
    private ArrayList<DeliveryPoint> mDeliveryPoints = new ArrayList<>();

    private DeliveryPointsListener mDeliveryPointsListener = new DeliveryPointsListener() {
        @Override
        public void loadMoreData(int start) {
            if(mIsLoadMore) {
                mStart++;
                loadData();
            }
        }

        @Override
        public void removeItem(int deliveryId) {
            if(mDeliveryPoints != null){
                for (int i = mDeliveryPoints.size() - 1; i>=0; i--){
                    if(mDeliveryPoints.get(i).getId() == deliveryId){
                        mDeliveryPoints.remove(i);
                    }
                }
            }
        }

        @Override
        public void loadedList(boolean isLoad) {
            mIsLoadedList = isLoad;
            mStart = 0;
            loadData();
        }

        @Override
        public void loadedMap(boolean isLoad) {
            mIsLoadedMap = isLoad;
            mStart = 0;
            loadData();
        }

        @Override
        public void pullToRefresh() {
            mStart = 0;
            loadData();
        }

    };

    public static DeliveryPointsFragment newInstance() {
        DeliveryPointsFragment fragment = new DeliveryPointsFragment();
        return fragment;
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_delivery_points;
    }

    @Override
    protected void initComponents() {
        mVpContent = (ViewPager) mView.findViewById(R.id.vp_content);
        mBtnListTab = (Button) mView.findViewById(R.id.btn_list_tab);
        mBtnMapTab = (Button) mView.findViewById(R.id.btn_map_tab);

        mCurrentBtnTab = mBtnListTab;
        mCurrentBtnTab.setSelected(true);

        mVpAdaper = new DeliveryPointsPagerAdapter(getFragmentManager(), mDeliveryPointsListener);
        mVpContent.setAdapter(mVpAdaper);
    }

    public void loadData(){
        if(mIsLoadedList && mIsLoadedMap) {
            showLoading(true);
            new GetDeliveryPointsListTask(mContext, SharedPreferenceHelper.getInstance(mContext).getInt(Constants.PREF_EMPLOYEES_ID), mStart, Constants.LIMIT_ITEMS, mCurrentFilterStatus, mCurrentFilterDateDelivery > 0 ? (mCurrentFilterDateDelivery + StringUtils.getOffsetInMillis()) / 1000 : 0, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Override
    protected void addListener() {
        mBtnListTab.setOnClickListener(this);
        mBtnMapTab.setOnClickListener(this);
        ((MainActivity) mContext).showNavRight(R.drawable.ic_filter_nav, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, FilterDriverActivity.class);
                i.putExtra(Constants.EXTRAX_STATUS, mCurrentFilterStatus);
                i.putExtra(Constants.EXTRAX_DATE_DELIVERY, mCurrentFilterDateDelivery);
                i.putExtra(Constants.EXTRAX_FROM_HISTORY, false);
                startActivityForResult(i, RQ_FILTER);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            if (requestCode == RQ_FILTER && data != null) {
                mCurrentFilterDateDelivery = data.getLongExtra(Constants.EXTRAX_DATE_DELIVERY, 0);
                mCurrentFilterStatus = data.getStringExtra(Constants.EXTRAX_STATUS);
                mStart = 0;
                loadData();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((MainActivity) mContext).hiddenNavRight();
        try {
            Fragment f = getFragmentManager().findFragmentById(R.id.map);
            if (f != null) {
                getFragmentManager().beginTransaction()
                        .remove(f).commit();
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_list_tab:
                if (mCurrentBtnTab != null) {
                    mCurrentBtnTab.setSelected(false);
                }
                mCurrentBtnTab = mBtnListTab;
                mCurrentBtnTab.setSelected(true);
                mVpContent.setCurrentItem(0);
                break;
            case R.id.btn_map_tab:
                if (mCurrentBtnTab != null) {
                    mCurrentBtnTab.setSelected(false);
                }
                mCurrentBtnTab = mBtnMapTab;
                mCurrentBtnTab.setSelected(true);
                mVpContent.setCurrentItem(1);
                break;
        }
    }

    @Override
    public void onConnectionOpen(BaseTask task) {

    }

    @Override
    public void onConnectionSuccess(BaseTask task, Object data) {
        if (task instanceof GetDeliveryPointsListTask) {
            showLoading(false);
            GetDeliveryPointsListOutput output = (GetDeliveryPointsListOutput) data;
            if(output.success){
                mIsLoadMore = output.result.hasNextPage;
                if(mStart == 0){
                    mDeliveryPoints.clear();
                }
                if(output.result != null && output.result.items != null) {
                    for (DeliveryPoint item : output.result.items) {
                        mDeliveryPoints.add(item);
                    }
                }
                ((DeliveryPointsListFragment)mVpAdaper.getItem(0)).addData(mDeliveryPoints);
                ((DeliveryMapFragment)mVpAdaper.getItem(1)).addData(mDeliveryPoints);
            } else {
                showAlert(getString(R.string.err_unexpected_exception));
            }
        }
    }

    @Override
    public void onConnectionError(BaseTask task, Exception exception) {
        if (task instanceof GetDeliveryPointsListTask) {
            showLoading(false);
            showAlert(exception);
        }
    }
}
