package com.bys.sangngoc.fragments;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import com.bys.sangngoc.R;
import com.bys.sangngoc.activities.drivers.DetailDriverDeliveryActivity;
import com.bys.sangngoc.activities.drivers.ReportTroubleActivity;
import com.bys.sangngoc.adapters.DeliveryPointsAdapter;
import com.bys.sangngoc.listeners.DeliveryPointsListener;
import com.bys.sangngoc.models.DeliveryPoint;
import com.bys.sangngoc.models.User;
import com.bys.sangngoc.utils.Constants;

/**
 * Created by Admin on 3/2/2018.
 */

public class DeliveryPointsListFragment extends BaseFragment implements View.OnClickListener {
    private RecyclerView mRcDeliveryPoints;
    private DeliveryPointsAdapter mEmployeesListAdapter;
    private ArrayList<DeliveryPoint> mData = new ArrayList<>();
    private DeliveryPointsListener mDeliveryPointsListener;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private LinearLayoutManager mLinearLayoutManager;
    private TextView mTvNoData;
    private boolean isLoading;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private BroadcastReceiver mChangeStatusBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(Constants.EXTRAX_DELIVERY_POINT)) {
                DeliveryPoint deliveryPoint = (DeliveryPoint) intent.getSerializableExtra(Constants.EXTRAX_DELIVERY_POINT);
                if (deliveryPoint != null) {
                    for (DeliveryPoint item : mData) {
                        if (item.getId() == deliveryPoint.getId()) {
                            item.setStatus(deliveryPoint.getStatus());
                            if (item.getStatus().equalsIgnoreCase(Constants.STATUS_PROCESSED) || item.getStatus().equalsIgnoreCase(Constants.STATUS_CANCEL)
                                    || item.getStatus().equalsIgnoreCase(Constants.STATUS_FAILDED)
                                    || item.getStatus().equalsIgnoreCase(Constants.STATUS_COMPLETE_NEED_ACCEPT)
                                    || item.getStatus().equalsIgnoreCase(Constants.STATUS_COMPLETE_ACCEPTED)) {
                                mData.remove(item);
                                if(mDeliveryPointsListener != null){
                                    mDeliveryPointsListener.removeItem(item.getId());
                                }
                                break;
                            }
                        }
                    }
                    mEmployeesListAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    public static DeliveryPointsListFragment newInstance(DeliveryPointsListener deliveryPointsListener) {
        DeliveryPointsListFragment fragment = new DeliveryPointsListFragment();
        fragment.mDeliveryPointsListener = deliveryPointsListener;
        return fragment;
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_delivery_points_list;
    }

    @Override
    protected void initComponents() {
        mContext.registerReceiver(mChangeStatusBroadcastReceiver, new IntentFilter(Constants.BROADCAST_CHANGE_STATUS));
        mTvNoData = (TextView) mView.findViewById(R.id.tv_no_data);
        mSwipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.swipeRefreshLayout);
        mRcDeliveryPoints = (RecyclerView) mView.findViewById(R.id.rc_content);
        mRcDeliveryPoints.setLayoutManager(mLinearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mEmployeesListAdapter = new DeliveryPointsAdapter(mContext, mData, false);
        mRcDeliveryPoints.setAdapter(mEmployeesListAdapter);

        loadData();
        if(mDeliveryPointsListener != null) {
            mDeliveryPointsListener.loadedList(true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mContext.unregisterReceiver(mChangeStatusBroadcastReceiver);
    }

    public void addData(ArrayList<DeliveryPoint> deliveryPoints) {
        isLoading = false;
        mData.clear();
        if (deliveryPoints != null) {
            for (DeliveryPoint item : deliveryPoints) {
                mData.add(item);
            }
            mEmployeesListAdapter.notifyDataSetChanged();
        }
        if (mData.size() > 0) {
            mTvNoData.setVisibility(View.GONE);
        } else {
            mTvNoData.setVisibility(View.VISIBLE);
        }
    }

    public void loadData() {
        mEmployeesListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void addListener() {
        mEmployeesListAdapter.setItemListener(new DeliveryPointsAdapter.IOnDeliveryPointClicklistener() {
            @Override
            public void onItemClick(int position) {
//                Intent i = new Intent(mContext, ReportTroubleActivity.class);
                Intent i = new Intent(mContext, DetailDriverDeliveryActivity.class);
                i.putExtra(Constants.EXTRAX_DELIVERY_POINT, mData.get(position));
                startActivity(i);
            }
        });
        mRcDeliveryPoints.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = mLinearLayoutManager.getChildCount();
                    totalItemCount = mLinearLayoutManager.getItemCount();
                    pastVisiblesItems = mLinearLayoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        if (mData.size() > 0 && !isLoading) {
                            isLoading = true;
                            if (mDeliveryPointsListener != null) {
                                mDeliveryPointsListener.loadMoreData(mData.size());
                            }
                        }
                    }
                }
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
                if (mDeliveryPointsListener != null) {
                    mDeliveryPointsListener.pullToRefresh();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }
}
