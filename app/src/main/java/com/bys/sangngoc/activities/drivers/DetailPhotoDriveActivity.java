package com.bys.sangngoc.activities.drivers;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

import com.bys.sangngoc.R;
import com.bys.sangngoc.activities.BaseActivity;
import com.bys.sangngoc.adapters.works.PhotoAdapter;
import com.bys.sangngoc.models.DeliveryPoint;
import com.bys.sangngoc.models.Image;
import com.bys.sangngoc.utils.Constants;

/**
 * Created by dangc on 3/22/2018.
 */

public class DetailPhotoDriveActivity extends BaseActivity implements View.OnClickListener {
    private ImageView mImvLeftNav;
    private ViewPager mViewPager;
    private PhotoAdapter mPhotoAdapter;
    private int mCurrentPosition;
    private DeliveryPoint mCurrentDeliveryPoint;
    private ArrayList<Image> mImages;
    @Override
    protected int initLayout() {
        return R.layout.activity_view_detail_photo;

    }

    @Override
    protected void initComponents() {
        setTitle(getString(R.string.txt_detail_photo));
        if(getIntent().hasExtra(Constants.EXTRAX_DELIVERY_POINT)){
            mCurrentDeliveryPoint = (DeliveryPoint) getIntent().getSerializableExtra(Constants.EXTRAX_DELIVERY_POINT);
            mCurrentPosition = getIntent().getIntExtra(Constants.EXTRAX_POSITION, 0);
        }

//        showNavRight(R.drawable.ic_delete2, this);
        mImvLeftNav = (ImageView) findViewById(R.id.imv_nav_left);
        mViewPager = (ViewPager) findViewById(R.id.pager);

        loadData();

        mPhotoAdapter = new PhotoAdapter(this, mImages);
        mViewPager.setAdapter(mPhotoAdapter);
        mViewPager.setCurrentItem(mCurrentPosition);
    }

    public void loadData(){
        mImages = new ArrayList<>();
        if (mCurrentDeliveryPoint.getStatus().equalsIgnoreCase(Constants.STATUS_TROUBLE)) {
            if (mCurrentDeliveryPoint.getIssuedStatusInfo() != null && mCurrentDeliveryPoint.getIssuedStatusInfo().getImages() != null) {
                for (Image item : mCurrentDeliveryPoint.getIssuedStatusInfo().getImages()) {
                    mImages.add(item);
                }
            }
        } else {
            if (mCurrentDeliveryPoint.getDeliveredStatusInfo() != null && mCurrentDeliveryPoint.getDeliveredStatusInfo().getImages() != null) {
                for (Image item : mCurrentDeliveryPoint.getDeliveredStatusInfo().getImages()) {
                    mImages.add(item);
                }
            }
        }
    }

    @Override
    protected void addListener() {
        mImvLeftNav.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imv_nav_left:
                finish();
                break;
            case R.id.imv_nav_right:
                showDialogDelete();
                break;
        }
    }

    private void showDialogDelete() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setMessage("Bạn có chắc muốn xóa?")
                .setPositiveButton(R.string.txt_accept, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setNegativeButton(R.string.txt_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }
}
