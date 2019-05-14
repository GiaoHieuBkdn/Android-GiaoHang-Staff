package com.bys.sangngoc.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bys.sangngoc.fragments.ProfileFragment;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;

import com.bys.sangngoc.R;
import com.bys.sangngoc.adapters.MenuItemAdapter;
import com.bys.sangngoc.api.ApiListener;
import com.bys.sangngoc.api.models.BaseOutput;
import com.bys.sangngoc.fragments.DeliveryPointsFragment;
import com.bys.sangngoc.fragments.HistoryDeliveryFragment;
import com.bys.sangngoc.fragments.MyLocationFragment;
import com.bys.sangngoc.fragments.works.ChooseProjectFragment;
import com.bys.sangngoc.models.MenuItem;
import com.bys.sangngoc.models.User;
import com.bys.sangngoc.tasks.BaseTask;
import com.bys.sangngoc.tasks.GetGeneralDataTask;
import com.bys.sangngoc.tasks.LogoutTask;
import com.bys.sangngoc.utils.Constants;
import com.bys.sangngoc.utils.SharedPreferenceHelper;
import com.bys.sangngoc.utils.StringUtils;

public class MainActivity extends BaseActivity implements MenuItemAdapter.IOnMenuItemClicklistener, DrawerLayout.DrawerListener, View.OnClickListener, ApiListener {
    private int RQ_EDIT_PROFILE = 2342;

    public enum MENU_ITEM {MENU_DELIVERY_POINTS, MENU_MY_LOCATION, MENU_DELIVERY_HISTORY, MENU_DELIVERY_ABSENT, LOGOUT_MENU, EDIT_PROFLILE, MENU_WORK_MANAGE, MENU_WORK_HISTORY, PROFILE_MENU}

    private DrawerLayout mDrawerLayout;
    private View mLayoutSlideMenu;
    private RecyclerView mRecyclerViewMenu;
    private MENU_ITEM mCurrentMenu, mMenuBefore;
    private ImageView mImvEditProfile;
    private int mRoleType;
    private TextView mTvFullname, mTvEmployessCode;
    private ImageView mImvAvatar;
    private TextView mTvEmployessBranch;
    private DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true)
            .considerExifParams(true)
            .showImageForEmptyUri(R.drawable.ic_user_default).showImageOnFail(R.drawable.ic_user_default)
            .showImageOnLoading(R.drawable.ic_user_default).bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2).build();
    private Fragment mCurrentFragment;
    private TextView mTvVersionApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRoleType = getIntent().getIntExtra(Constants.EXTRAX_ROLE, Constants.ROLE_DRIVER);
        mTvVersionApp = findViewById(R.id.tv_version_app);
        mImvEditProfile = (ImageView) findViewById(R.id.imv_edit_profile);
        mTvFullname = (TextView) findViewById(R.id.tv_fullname);
        mTvEmployessBranch = (TextView) findViewById(R.id.tv_employees_branch);
        mTvEmployessCode = (TextView) findViewById(R.id.tv_employees_code);
        mImvAvatar = (ImageView) findViewById(R.id.imv_avatar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mLayoutSlideMenu = findViewById(R.id.layout_left_menu);
        mRecyclerViewMenu = (RecyclerView) findViewById(R.id.recyclerview_menu);
        mRecyclerViewMenu.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        ArrayList<MenuItem> menuItems = new ArrayList<>();
//        if (mRoleType == Constants.ROLE_DRIVER) {
        menuItems.add(new MenuItem(MENU_ITEM.MENU_DELIVERY_POINTS, R.drawable.ic_delivery_points, getString(R.string.txt_menu_delivery_points)));
//        menuItems.add(new MenuItem(MENU_ITEM.MENU_MY_LOCATION, R.drawable.ic_my_location, getString(R.string.txt_menu_my_location)));
        menuItems.add(new MenuItem(MENU_ITEM.MENU_DELIVERY_HISTORY, R.drawable.ic_delivery_history, getString(R.string.txt_menu_delivery_history)));
//        } else {
       /* menuItems.add(new MenuItem(MENU_ITEM.MENU_WORK_MANAGE, R.drawable.ic_manage_work, getString(R.string.txt_menu_work_manage)));
        menuItems.add(new MenuItem(MENU_ITEM.MENU_WORK_HISTORY, R.drawable.ic_history_work, getString(R.string.txt_menu_work_history)));*/
//        }
        menuItems.add(new MenuItem(MENU_ITEM.PROFILE_MENU, R.drawable.ic_profile_menu, getString(R.string.txt_menu_profile)));
        menuItems.add(new MenuItem(MENU_ITEM.LOGOUT_MENU, R.drawable.ic_logout, getString(R.string.txt_manage_logout)));
        MenuItemAdapter menuAdapter = new MenuItemAdapter(this, menuItems);
        menuAdapter.setItemListener(this);
        mRecyclerViewMenu.setAdapter(menuAdapter);

        if (mRoleType == Constants.ROLE_DRIVER) {
            setTitle(getString(R.string.txt_menu_delivery_points));
            setNewPage(DeliveryPointsFragment.newInstance());
            menuAdapter.setItemSelected(MENU_ITEM.MENU_DELIVERY_POINTS);
        } else {
            setTitle(getString(R.string.txt_menu_work_manage));
            setNewPage(ChooseProjectFragment.newInstance(0));
            menuAdapter.setItemSelected(MENU_ITEM.MENU_WORK_MANAGE);
        }
        mDrawerLayout.addDrawerListener(this);
        addListener();


        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            mTvVersionApp.setText("v" + pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void addListener() {
        if (mImvEditProfile != null) {
            mImvEditProfile.setOnClickListener(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfile();
        if (SharedPreferenceHelper.getInstance(this).get(Constants.PREF_GENERAL_DATA) == null) {
            new GetGeneralDataTask(this, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    public void loadProfile() {
        String userJson = SharedPreferenceHelper.getInstance(this).get(Constants.PREF_USER_PROFILE);
        if (userJson != null && userJson.length() > 0) {
            User user = new Gson().fromJson(userJson, User.class);
            mTvEmployessCode.setText(getString(R.string.txt_employees_code_2, StringUtils.getString(this, user.getEmployeeNo())));
            mTvFullname.setText(StringUtils.getString(this, user.getEmployeeName()));
            if(user.getDepartmentRoom() != null) {
                mTvEmployessBranch.setText(getString(R.string.txt_employees_branch, StringUtils.getString(this, user.getDepartmentRoom().getDepartmentRoomName())));
            } else {
                mTvEmployessBranch.setText(getString(R.string.txt_employees_branch, StringUtils.getString(this, "")));
            }
            if (user.getEmployeeAvatar() != null && user.getEmployeeAvatar().length() > 0) {
//                Picasso.with(this).load( (user.getEmployeeAvatar().contains("http://") ? "" : "http://") + user.getEmployeeAvatar()).into(mImvAvatar);
                ImageLoader.getInstance().displayImage((user.getEmployeeAvatar().contains("http://") ? "" : "http://") + user.getEmployeeAvatar(), mImvAvatar, options);
            } else {
                mImvAvatar.setImageResource(R.drawable.ic_user_default);
            }
        }
    }

    @Override
    public void onItemClick(MENU_ITEM menuId) {
        mCurrentMenu = menuId;
        mDrawerLayout.closeDrawer(mLayoutSlideMenu);
        mMenuBefore = menuId;
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {

    }

    @Override
    public void onDrawerClosed(View drawerView) {
        if (mCurrentMenu == null || mMenuBefore == null) {
            return;
        }
        mMenuBefore = null;
        switch (mCurrentMenu) {
            case MENU_DELIVERY_POINTS:
                setTitle(getString(R.string.txt_menu_delivery_points));
                mCurrentFragment = DeliveryPointsFragment.newInstance();
                setNewPage(mCurrentFragment);
                break;
            case MENU_MY_LOCATION:
                if(mCurrentFragment != null && mCurrentFragment instanceof MyLocationFragment){
                    return;
                }
                setTitle(getString(R.string.txt_menu_my_location));
                mCurrentFragment = MyLocationFragment.newInstance();
                setNewPage(mCurrentFragment);
                break;
            case MENU_DELIVERY_HISTORY:
                setTitle(getString(R.string.txt_menu_delivery_history));
                mCurrentFragment = HistoryDeliveryFragment.newInstance();
                setNewPage(mCurrentFragment);
                break;
            case MENU_WORK_MANAGE:
                setTitle(getString(R.string.txt_menu_work_manage));
                mCurrentFragment = ChooseProjectFragment.newInstance(0);
                setNewPage(mCurrentFragment);
                break;
            case MENU_WORK_HISTORY:
                setTitle(getString(R.string.txt_menu_work_history));
                mCurrentFragment = ChooseProjectFragment.newInstance(1);
                setNewPage(mCurrentFragment);
                break;
            case PROFILE_MENU:
                setTitle(getString(R.string.txt_menu_profile));
                setNewPage(ProfileFragment.newInstance());
                break;
            case EDIT_PROFLILE:
                Intent i = new Intent(this, ProfileFragment.class);
                startActivityForResult(i, RQ_EDIT_PROFILE);
                break;
            case LOGOUT_MENU:
                showPopupLogout();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RQ_EDIT_PROFILE && resultCode == RESULT_OK) {
            loadProfile();
        }
    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    @Override
    protected int initLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initComponents() {
        showNavLeft(R.drawable.ic_menu, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDrawerLayout.isDrawerOpen(mLayoutSlideMenu)) {
                    mDrawerLayout.closeDrawer(mLayoutSlideMenu);
                } else {
                    hideKeyBoard();
                    mDrawerLayout.openDrawer(mLayoutSlideMenu);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imv_edit_profile:
                mCurrentMenu = MENU_ITEM.EDIT_PROFLILE;
                mMenuBefore = MENU_ITEM.EDIT_PROFLILE;
                mDrawerLayout.closeDrawer(GravityCompat.START);
                break;
        }
    }

    @Override
    public void onConnectionOpen(BaseTask task) {

    }

    @Override
    public void onConnectionSuccess(BaseTask task, Object data) {
        if (task instanceof LogoutTask) {
            showLoading(false);
            BaseOutput output = (BaseOutput) data;
            if (output.success) {
                SharedPreferenceHelper.getInstance(this).clearSharePrefs();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            } else {
                showAlert(getString(R.string.err_unexpected_exception));
            }
        } else if (task instanceof GetGeneralDataTask) {
            if (data != null && data instanceof String) {
                SharedPreferenceHelper.getInstance(this).set(Constants.PREF_GENERAL_DATA, (String) data);
            }
        }
    }

    @Override
    public void onConnectionError(BaseTask task, Exception exception) {
        if (task instanceof LogoutTask) {
            showLoading(false);
            showAlert(exception);
        }
    }

    private void showPopupLogout() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(getString(R.string.txt_are_you_sure_logout))
                .setPositiveButton(R.string.txt_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        showLoading(true);
                        new LogoutTask(MainActivity.this, MainActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                })
                .setNegativeButton(R.string.txt_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }
}
