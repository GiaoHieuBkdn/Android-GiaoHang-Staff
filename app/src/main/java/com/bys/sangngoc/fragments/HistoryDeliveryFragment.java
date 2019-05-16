package com.bys.sangngoc.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import com.bys.sangngoc.R;
import com.bys.sangngoc.activities.BaseActivity;
import com.bys.sangngoc.activities.MainActivity;
import com.bys.sangngoc.activities.drivers.DetailDriverDeliveryActivity;
import com.bys.sangngoc.activities.drivers.FilterDriverActivity;
import com.bys.sangngoc.adapters.DeliveryPointsAdapter;
import com.bys.sangngoc.api.ApiListener;
import com.bys.sangngoc.api.models.GetDeliveryPointsListOutput;
import com.bys.sangngoc.models.DeliveryPoint;
import com.bys.sangngoc.models.User;
import com.bys.sangngoc.tasks.BaseTask;
import com.bys.sangngoc.tasks.GetHistoryDeliveryPointsListTask;
import com.bys.sangngoc.utils.Constants;
import com.bys.sangngoc.utils.SharedPreferenceHelper;

/**
 * Created by Admin on 3/21/2018.
 */

public class HistoryDeliveryFragment extends BaseFragment implements View.OnClickListener, ApiListener {
    private int RQ_FILTER = 2233;
    private RecyclerView mRcDeliveryPoints;
    private DeliveryPointsAdapter mEmployeesListAdapter;
    private ArrayList<DeliveryPoint> mData = new ArrayList<>();
    private int mStart = 0;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private LinearLayoutManager mLinearLayoutManager;
    private boolean mIsLoadMore;
    private EditText mEdtSearch;
    private TextView mTvNoData;
    private String mCurrentFilterStatus = "";
    private long mCurrentFilterDateDelivery = 0;
    private ArrayList<DeliveryPoint> mDeliveryPoints = new ArrayList<>();
    private GetHistoryDeliveryPointsListTask mGetHistoryDeliveryPointsListTask;
    private boolean isLoading;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private CountDownTimer mCountDownTimer = new CountDownTimer(1500, 500) {
        @Override
        public void onTick(long l) {

        }

        @Override
        public void onFinish() {
            mStart = 0;
            mGetHistoryDeliveryPointsListTask = new GetHistoryDeliveryPointsListTask(mContext, SharedPreferenceHelper.getInstance(mContext).getInt(Constants.PREF_EMPLOYEES_ID), mEdtSearch.getText().toString(), mStart, Constants.LIMIT_ITEMS, mCurrentFilterStatus, mCurrentFilterDateDelivery/1000, HistoryDeliveryFragment.this);
            mGetHistoryDeliveryPointsListTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    };

    private BroadcastReceiver mChangeStatusBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(Constants.EXTRAX_DELIVERY_POINT)) {
                DeliveryPoint deliveryPoint = (DeliveryPoint) intent.getSerializableExtra(Constants.EXTRAX_DELIVERY_POINT);
                if (deliveryPoint != null) {
                    for (DeliveryPoint item : mData) {
                        if (item.getId() == deliveryPoint.getId()) {
                            item.setStatus(deliveryPoint.getStatus());
                        }
                    }
                    mEmployeesListAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    public static HistoryDeliveryFragment newInstance() {
        HistoryDeliveryFragment fragment = new HistoryDeliveryFragment();
        return fragment;
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_history_delivery;
    }

    @Override
    protected void initComponents() {
        mContext.registerReceiver(mChangeStatusBroadcastReceiver, new IntentFilter(Constants.BROADCAST_CHANGE_STATUS));

        ((MainActivity) mContext).showNavRight(R.drawable.ic_filter_nav, this);
        mSwipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.swipeRefreshLayout);
        mTvNoData = (TextView) mView.findViewById(R.id.tv_no_data);
        mEdtSearch = (EditText) mView.findViewById(R.id.edt_search);
        mRcDeliveryPoints = (RecyclerView) mView.findViewById(R.id.rc_content);
        mRcDeliveryPoints.setLayoutManager(mLinearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mEmployeesListAdapter = new DeliveryPointsAdapter(mContext, mData, true);
        mRcDeliveryPoints.setAdapter(mEmployeesListAdapter);

        mStart = 0;
        loadData();
    }


    public void loadData() {
        showLoading(true);
        mGetHistoryDeliveryPointsListTask = new GetHistoryDeliveryPointsListTask(mContext, SharedPreferenceHelper.getInstance(mContext).getInt(Constants.PREF_EMPLOYEES_ID), mEdtSearch.getText().toString(), mStart, Constants.LIMIT_ITEMS, mCurrentFilterStatus, mCurrentFilterDateDelivery/1000, this);
        mGetHistoryDeliveryPointsListTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    protected void addListener() {
        mEmployeesListAdapter.setItemListener(new DeliveryPointsAdapter.IOnDeliveryPointClicklistener() {
            @Override
            public void onItemClick(int position) {
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
                        if (mData.size() > 0 && mIsLoadMore && !isLoading) {
                            isLoading = true;
                            showLoading(true);
                            mStart++;
                            new GetHistoryDeliveryPointsListTask(mContext, SharedPreferenceHelper.getInstance(mContext).getInt(Constants.PREF_EMPLOYEES_ID), mEdtSearch.getText().toString(), mStart, Constants.LIMIT_ITEMS, mCurrentFilterStatus, mCurrentFilterDateDelivery/1000, HistoryDeliveryFragment.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        }
                    }
                }
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
                mStart = 0;
                loadData();
            }
        });
        mEdtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideKeyBoard();
                    mStart = 0;
                    loadData();
                    return true;
                }
                return false;
            }
        });
        mEdtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(mGetHistoryDeliveryPointsListTask != null){
                    mGetHistoryDeliveryPointsListTask.cancel(true);
                }
                mCountDownTimer.cancel();
                mCountDownTimer.start();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((MainActivity) mContext).hiddenNavRight();
        mContext.unregisterReceiver(mChangeStatusBroadcastReceiver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imv_nav_right:
                Intent i = new Intent(mContext, FilterDriverActivity.class);
                i.putExtra(Constants.EXTRAX_STATUS, mCurrentFilterStatus);
                i.putExtra(Constants.EXTRAX_DATE_DELIVERY, mCurrentFilterDateDelivery);
                i.putExtra(Constants.EXTRAX_FROM_HISTORY, true);
                startActivityForResult(i, RQ_FILTER);
                break;
        }
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
    public void onConnectionOpen(BaseTask task) {

    }

    @Override
    public void onConnectionSuccess(BaseTask task, Object data) {
        if (task instanceof GetHistoryDeliveryPointsListTask) {
            isLoading = false;
            showLoading(false);
            GetDeliveryPointsListOutput output = (GetDeliveryPointsListOutput) data;
            if(output.success){
                mIsLoadMore = output.result.hasNextPage;
                if(mStart == 0){
                    mData.clear();
                }
                for (DeliveryPoint item : output.result.items){
                    mData.add(item);
                }
                mEmployeesListAdapter.notifyDataSetChanged();
                if (mData.size() > 0) {
                    mTvNoData.setVisibility(View.GONE);
                } else {
                    mTvNoData.setVisibility(View.VISIBLE);
                }
            } else {
                showAlert(getString(R.string.err_unexpected_exception));
            }
        }
    }

    @Override
    public void onConnectionError(BaseTask task, Exception exception) {
        if (task instanceof GetHistoryDeliveryPointsListTask) {
            isLoading = false;
            showLoading(false);
            showAlert(exception);
        }
    }
}
