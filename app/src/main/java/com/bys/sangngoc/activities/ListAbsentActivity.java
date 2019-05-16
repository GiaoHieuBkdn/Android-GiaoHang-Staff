package com.bys.sangngoc.activities;

import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.bys.sangngoc.R;
import com.bys.sangngoc.adapters.ListAbsentAdapter;
import com.bys.sangngoc.adapters.PopupMenuAdapter;
import com.bys.sangngoc.api.ApiListener;
import com.bys.sangngoc.api.models.AbsentListOutput;
import com.bys.sangngoc.api.models.GetRecentDateAbsentOutput;
import com.bys.sangngoc.models.Absent;
import com.bys.sangngoc.models.PopupMenuItem;
import com.bys.sangngoc.models.User;
import com.bys.sangngoc.tasks.BaseTask;
import com.bys.sangngoc.tasks.GetAbsentTask;
import com.bys.sangngoc.tasks.GetRecentDateAbsentTask;
import com.bys.sangngoc.utils.Constants;
import com.bys.sangngoc.utils.StringUtils;

import java.util.ArrayList;

/**
 * Created by Admin on 3/7/2018.
 */

public class ListAbsentActivity extends BaseActivity implements View.OnClickListener, ApiListener {
    private RecyclerView mRcAbsent;
    private ListAbsentAdapter mAbsentAdapter;
    private ArrayList<Absent> mAbsentDatas = new ArrayList<>();
    private int pageIndex = 0;
    private boolean isLoad = true;
    private TextView mTvNoData;
    private User mEmployee;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView mRcMenu;
    private PopupMenuAdapter mPopupMenuAdapter;
    private ArrayList<PopupMenuItem> mMenuData = new ArrayList<>();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mTvNumberAbsentDate, mTvRecentDate;
    private GetRecentDateAbsentOutput mGetRecentDateAbsentOutput;

    @Override
    protected int initLayout() {
        return R.layout.activity_list_absent;
    }

    @Override
    protected void initComponents() {
        mEmployee = (User) getIntent().getSerializableExtra("user");
        setTitle(getString(R.string.txt_absent));
        showNavLeft(R.drawable.ic_back, this);
        showNavRight(R.drawable.ic_3dots, this);
        mTvNoData = (TextView) findViewById(R.id.tv_no_data);
        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        mRcAbsent = (RecyclerView) findViewById(R.id.lv_absent);
        mTvNumberAbsentDate = findViewById(R.id.tv_remain_date_absent);
        mTvRecentDate = findViewById(R.id.tv_recent_date_absent);
        mRcAbsent.setLayoutManager(mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAbsentAdapter = new ListAbsentAdapter(ListAbsentActivity.this, mAbsentDatas);
        mRcAbsent.setAdapter(mAbsentAdapter);
        loadData();

        initPopupMenus();
    }

    public void loadData() {
        showLoading(true);
        new GetAbsentTask(ListAbsentActivity.this, mEmployee.getId(), pageIndex, Constants.LIMIT_ITEMS, ListAbsentActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new GetRecentDateAbsentTask(this, mEmployee.getId() + "", this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void initPopupMenus() {
        mRcMenu = (RecyclerView) findViewById(R.id.rc_menu);
        mRcMenu.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mPopupMenuAdapter = new PopupMenuAdapter(this, mMenuData);
        mRcMenu.setAdapter(mPopupMenuAdapter);

        mMenuData.clear();
        mMenuData.add(new PopupMenuItem(PopupMenuItem.MENU_ID.CREATE_ABSENT, getString(R.string.txt_create_absent)));
        mMenuData.add(new PopupMenuItem(PopupMenuItem.MENU_ID.RESIGNATION, getString(R.string.txt_create_resignation)));
        mPopupMenuAdapter.notifyDataSetChanged();

        mPopupMenuAdapter.setItemListener(new PopupMenuAdapter.IOnMenuItemClicklistener() {
            @Override
            public void onItemClick(PopupMenuItem.MENU_ID menuId) {
                if (menuId == PopupMenuItem.MENU_ID.CREATE_ABSENT) {
                    startActivityForResult(new Intent(ListAbsentActivity.this, CreateAbsentActivity.class), 1122);
                } else if (menuId == PopupMenuItem.MENU_ID.RESIGNATION) {
                    startActivity(new Intent(ListAbsentActivity.this, ResignationActivity.class));
                }
                mRcMenu.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1122) {
                pageIndex = 0;
                loadData();
            } else if (requestCode == ApproveLeaveActivity.RQ_ABSENT) {
                //Edit one
                if (data != null) {
                    Absent absent = (Absent) data.getSerializableExtra(Constants.EXTRAX_ABSENT);
                    if (data.getBooleanExtra(Constants.EXTRAX_ABSENT_CHANGE_STATUS_OR_DELETE, false)) {
                        pageIndex = 0;
                        loadData();
                    } else {
                        for (int i = 0; i < mAbsentDatas.size(); i++) {
                            if (mAbsentDatas.get(i).getId() == absent.getId()) {
                                mAbsentDatas.set(i, absent);
                            }
                        }
                        mAbsentAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }

    @Override
    protected void addListener() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
                pageIndex = 0;
                loadData();
            }
        });
        mRcAbsent.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();
                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount && isLoad) {
                        pageIndex++;
                        loadData();
                    }
                }
            }
        });
        mAbsentAdapter.setItemListener(new ListAbsentAdapter.IOnEmployeesClicklistener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(ListAbsentActivity.this, ApproveLeaveActivity.class);
                intent.putExtra(Constants.EXTRAX_USER, mEmployee);
                intent.putExtra(Constants.EXTRAX_ABSENT, mAbsentDatas.get(position));
                startActivityForResult(intent, ApproveLeaveActivity.RQ_ABSENT);
            }

            @Override
            public void onViewDetailClick(int position) {

            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Rect navRightRect = new Rect();
        mImvNavRight.getGlobalVisibleRect(navRightRect);
        if (!navRightRect.contains((int) ev.getRawX(), (int) ev.getRawY()) && mRcMenu != null) {
            Rect viewRect = new Rect();
            mRcMenu.getGlobalVisibleRect(viewRect);
            if (!viewRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                mRcMenu.setVisibility(View.GONE);
            }
        }
        return super.dispatchTouchEvent(ev);
    }


    @Override
    public void onClick(View v) {
        Intent i = null;
        switch (v.getId()) {
            case R.id.imv_nav_left:
                finish();
                break;
            case R.id.imv_nav_right:
                if (mRcMenu.getVisibility() == View.VISIBLE) {
                    mRcMenu.setVisibility(View.GONE);
                } else {
                    mRcMenu.setVisibility(View.VISIBLE);
                }
                break;

        }
    }

    @Override
    public void onConnectionOpen(BaseTask task) {

    }

    @Override
    public void onConnectionSuccess(BaseTask task, Object data) {
        if (task instanceof GetAbsentTask) {
            showLoading(false);
            AbsentListOutput output = (AbsentListOutput) data;
            if (output.result != null && output.result.items != null) {
                if (pageIndex == 0) {
                    mAbsentDatas.clear();
                }
                for (Absent item : output.result.items) {
                    mAbsentDatas.add(item);
                }
                if (output.result.items.size() < 10) {
                    isLoad = false;
                } else {
                    isLoad = true;
                }
                if (mAbsentDatas.size() > 0) {
                    mTvNoData.setVisibility(View.GONE);
                } else {
                    mTvNoData.setVisibility(View.VISIBLE);
                }
                mAbsentAdapter.notifyDataSetChanged();
            } else {
                showAlert(getString(R.string.err_unexpected_exception));
            }
            hideKeyBoard();
        } else if (task instanceof GetRecentDateAbsentTask) {
            mGetRecentDateAbsentOutput = (GetRecentDateAbsentOutput) data;
            if (mGetRecentDateAbsentOutput.success) {
                mTvNumberAbsentDate.setText(getString(R.string.txt_remain_date_absent, mGetRecentDateAbsentOutput.result.remainingleavedays + ""));
                if (mGetRecentDateAbsentOutput.result.leaveDays > 0) {
                    mTvRecentDate.setText(getString(R.string.txt_recent_date_absent, StringUtils.getDateStringFromTimestamp(mGetRecentDateAbsentOutput.result.lastLeaveDate * 1000)));
                } else {
                    mTvRecentDate.setText(getString(R.string.txt_recent_date_absent, getString(R.string.txt_no_text)));
                }
            }
        }
    }

    @Override
    public void onConnectionError(BaseTask task, Exception exception) {
        if (task instanceof GetAbsentTask) {
            showLoading(false);
            showAlert(exception);
        }
    }
}
