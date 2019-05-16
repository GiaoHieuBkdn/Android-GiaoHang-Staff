package com.bys.sangngoc.activities.drivers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import com.bys.sangngoc.R;
import com.bys.sangngoc.activities.BaseActivity;
import com.bys.sangngoc.activities.EditProfileActivity;
import com.bys.sangngoc.adapters.ImagesAdapter;
import com.bys.sangngoc.models.DeliveryPoint;
import com.bys.sangngoc.models.Image;
import com.bys.sangngoc.utils.Constants;
import com.bys.sangngoc.utils.StringUtils;
import com.bys.sangngoc.views.ItemOffsetDecoration;

/**
 * Created by Admin on 3/20/2018.
 */

public class ViewPhotoDriverActivity extends BaseActivity implements View.OnClickListener {
    private RecyclerView mRcImages;
    private ImagesAdapter mAdapter;
    private ArrayList<Image> mData = new ArrayList<>();
    private Uri mImageCaptureUri;
    private String myImagePath;
    private DeliveryPoint mCurrentDeliveryPoint;
    private TextView mTvPointNo, mTvCustomerNo, mTvOrderNo, mTvExpectTime, mTvDeliveryTime, mTvImageTitle, mTvStatus;
    private View mViewDeliveryTime;

    @Override
    protected int initLayout() {
        return R.layout.activity_view_photo_driver;
    }

    @Override
    protected void initComponents() {
        if (getIntent().hasExtra(Constants.EXTRAX_DELIVERY_POINT)) {
            mCurrentDeliveryPoint = (DeliveryPoint) getIntent().getSerializableExtra(Constants.EXTRAX_DELIVERY_POINT);
        }
        setTitle(getString(R.string.txt_view_all_images));
        showNavLeft(R.drawable.ic_back, this);
        mTvPointNo = (TextView) findViewById(R.id.tv_point_no);
        mTvCustomerNo = (TextView) findViewById(R.id.tv_customer_no);
        mTvOrderNo = (TextView) findViewById(R.id.tv_order_no);
        mTvExpectTime = (TextView) findViewById(R.id.tv_expect_time);
        mTvDeliveryTime = (TextView) findViewById(R.id.tv_delivery_time);
        mTvImageTitle = (TextView) findViewById(R.id.tv_images_title);
        mTvStatus = (TextView) findViewById(R.id.tv_status);
        mTvStatus.setSelected(true);
        mViewDeliveryTime = findViewById(R.id.view_delivery_time);

        mRcImages = (RecyclerView) findViewById(R.id.rc_content);
        mRcImages.setLayoutManager(new GridLayoutManager(this, 4));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.item_offset);
        mRcImages.addItemDecoration(itemDecoration);
        mAdapter = new ImagesAdapter(this, mData, true);
        mRcImages.setAdapter(mAdapter);
        loadData();

    }

    public void loadData() {
        if (mCurrentDeliveryPoint == null) {
            return;
        }
        mTvPointNo.setText(StringUtils.getString(this, mCurrentDeliveryPoint.getDeliveryPointNo()));
        mTvCustomerNo.setText(StringUtils.getString(this, mCurrentDeliveryPoint.getDeliveryToCustomer() != null ? mCurrentDeliveryPoint.getDeliveryToCustomer().getCustomerNo() : ""));
        mTvOrderNo.setText(StringUtils.getString(this, mCurrentDeliveryPoint.getSaleOrderNo()));
        mTvExpectTime.setText(StringUtils.getFullDate2StringFromTimestamp(mCurrentDeliveryPoint.getExpectedDeliveryTime() * 1000));
        if (mCurrentDeliveryPoint.getStatus().equalsIgnoreCase(Constants.STATUS_PROCESSED) && mCurrentDeliveryPoint.getDeliveredStatusInfo() != null) {
            mTvDeliveryTime.setText(StringUtils.getFullDate2StringFromTimestamp(mCurrentDeliveryPoint.getDeliveredStatusInfo().getActualDeliveryTime() * 1000));
            mViewDeliveryTime.setVisibility(View.VISIBLE);
        } else {
            mViewDeliveryTime.setVisibility(View.GONE);
        }
        if (mCurrentDeliveryPoint.getStatus().equalsIgnoreCase(Constants.STATUS_PROCESSING)) {
            mTvStatus.setBackgroundResource(R.drawable.bg_status_blue_top);
            mTvStatus.setText(getString(R.string.txt_status_progressing));
            mTvPointNo.setTextColor(ContextCompat.getColor(this, R.color.color_status_blue));
        } else if (mCurrentDeliveryPoint.getStatus().equalsIgnoreCase(Constants.STATUS_DONT_PROCESSING)) {
            mTvStatus.setBackgroundResource(R.drawable.bg_status_orange_top);
            mTvStatus.setText(getString(R.string.txt_status_dont_progressing));
            mTvPointNo.setTextColor(ContextCompat.getColor(this, R.color.color_status_orange));
        } else if (mCurrentDeliveryPoint.getStatus().equalsIgnoreCase(Constants.STATUS_CANCEL)) {
            mTvStatus.setBackgroundResource(R.drawable.bg_status_dark_top);
            mTvStatus.setText(getString(R.string.txt_status_cancel));
            mTvPointNo.setTextColor(ContextCompat.getColor(this, R.color.color_status_dark));
        } else if (mCurrentDeliveryPoint.getStatus().equalsIgnoreCase(Constants.STATUS_TROUBLE)) {
            mTvStatus.setBackgroundResource(R.drawable.bg_status_red_top);
            mTvStatus.setText(getString(R.string.txt_status_trouble));
            mTvPointNo.setTextColor(ContextCompat.getColor(this, R.color.color_status_red));
            mTvImageTitle.setText(getString(R.string.txt_photo_trouble));
            mData.clear();
            if (mCurrentDeliveryPoint.getIssuedStatusInfo() != null && mCurrentDeliveryPoint.getIssuedStatusInfo().getImages() != null) {
                for (Image item : mCurrentDeliveryPoint.getIssuedStatusInfo().getImages()) {
                    item.setPath(item.getThumbSizeUrl());
                    mData.add(item);
                }
            }
        } else if (mCurrentDeliveryPoint.getStatus().equalsIgnoreCase(Constants.STATUS_FAILDED)) {
            mTvStatus.setBackgroundResource(R.drawable.bg_status_dark_top);
            mTvStatus.setText(getString(R.string.txt_status_failed));
            mTvPointNo.setTextColor(ContextCompat.getColor(this, R.color.color_status_dark));
            mTvImageTitle.setText(getString(R.string.txt_images));
            mData.clear();
            if (mCurrentDeliveryPoint.getDeliveredStatusInfo() != null && mCurrentDeliveryPoint.getDeliveredStatusInfo().getImages() != null) {
                for (Image item : mCurrentDeliveryPoint.getDeliveredStatusInfo().getImages()) {
                    item.setPath(item.getThumbSizeUrl());
                    mData.add(item);
                }
            }
        } else {
            mTvStatus.setBackgroundResource(R.drawable.bg_status_green_top);
            if (mCurrentDeliveryPoint.getStatus().equalsIgnoreCase(Constants.STATUS_PROCESSED)) {
                mTvStatus.setText(getString(R.string.txt_status_completed));
            } else  if (mCurrentDeliveryPoint.getStatus().equalsIgnoreCase(Constants.STATUS_COMPLETE_NEED_ACCEPT)) {
                mTvStatus.setText(getString(R.string.txt_status_completed_need_accepted));
            } else {
                mTvStatus.setText(getString(R.string.txt_status_completed_accepted));
            }
            mTvPointNo.setTextColor(ContextCompat.getColor(this, R.color.color_status_green));
            mTvImageTitle.setText(getString(R.string.txt_images_delivery));
            mData.clear();
            if (mCurrentDeliveryPoint.getDeliveredStatusInfo() != null && mCurrentDeliveryPoint.getDeliveredStatusInfo().getImages() != null) {
                for (Image item : mCurrentDeliveryPoint.getDeliveredStatusInfo().getImages()) {
                    item.setPath(item.getThumbSizeUrl());
                    mData.add(item);
                }
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void addListener() {
        mAdapter.setItemListener(new ImagesAdapter.IOnItemClicklistener() {
            @Override
            public void onDeleteClick(final int position) {

            }

            @Override
            public void onItemClick(int position) {
                Intent i = new Intent(ViewPhotoDriverActivity.this, DetailPhotoDriveActivity.class);
                i.putExtra(Constants.EXTRAX_POSITION, position);
                i.putExtra(Constants.EXTRAX_DELIVERY_POINT, mCurrentDeliveryPoint);
                startActivity(i);
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imv_nav_left:
                finish();
                break;
            case R.id.imv_nav_right:
//                showDialogChoose();
                break;
        }
    }

    public void showDialogChoose() {
        new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.app_name))
                .setItems(R.array.arr_capture_2, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            onClickTakeAPhoto();
                        } else if (which == 1) {
                            onClickChooseFromGallery();
                        }
                    }
                }).show();
    }

    public void onClickTakeAPhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/TASKSAPP");
        if (!file.exists()) {
            file.mkdirs();
        }
        File f = new File(file.getPath() + "/" + SystemClock.currentThreadTimeMillis() + ".jpg");
        mImageCaptureUri = Uri.fromFile(f);
        myImagePath = f.getPath();
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        takePictureIntent.putExtra("return-data", true);
        takePictureIntent.putExtra(MediaStore.EXTRA_FINISH_ON_COMPLETION, true);
        startActivityForResult(takePictureIntent, EditProfileActivity.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    public void onClickChooseFromGallery() {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/TASKSAPP");
        if (!file.exists()) {
            file.mkdirs();
        }
        File f = new File(file.getPath() + "/" + SystemClock.currentThreadTimeMillis() + ".jpg");
        mImageCaptureUri = Uri.fromFile(f);
        myImagePath = f.getPath();

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), EditProfileActivity.SELECT_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case EditProfileActivity.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
                    try {
                        Bundle extras = null;
                        if (data != null) {
                            extras = data.getExtras();
                        }
                        if (data != null && extras != null && extras.get("data") != null) {
                            File f = new File(myImagePath);
                            Bitmap bitMap = (Bitmap) extras.get("data");
                            try {
                                f.createNewFile();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            bitMap.compress(Bitmap.CompressFormat.PNG, 0, bos);
                            byte[] bitmapdata = bos.toByteArray();
                            FileOutputStream fos;
                            try {
                                fos = new FileOutputStream(f);
                                try {
                                    fos.write(bitmapdata);
                                    mImageCaptureUri = Uri.fromFile(f);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        } else {
                            // Take with Uri
                        }
                    } catch (NullPointerException ex) {

                    }
                    mData.add(new Image(myImagePath));
                    mAdapter.notifyDataSetChanged();
                    break;
                case EditProfileActivity.SELECT_PICTURE:
                    Uri uriPhoto = data.getData();
                    File finalFile1 = new File(getRealPathFromURI(uriPhoto));
//                    mImageCaptureUri = data.getData();
//                    File finalFile1 = new File(getRealPathFromURI(mImageCaptureUri));
//                    String[] spl = finalFile1.getPath().split("/");
//                    String name1 = spl[spl.length - 1];
//                    String[] end = name1.split("\\.");
//                    String endName = end[end.length - 1];
//                    String newName = finalFile1.getPath().substring(0, finalFile1.getPath().length() - 1 - name1.length())
//                            + "/" + SystemClock.currentThreadTimeMillis() + "." + endName;
                    try {
                        copy(finalFile1, new File(myImagePath));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                    myImagePath = newName;
                    mData.add(new Image(myImagePath));
                    mAdapter.notifyDataSetChanged();
                    break;
            }

        }

    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = this.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    void copy(File source, File target) throws IOException {

        InputStream in = new FileInputStream(source);
        OutputStream out = new FileOutputStream(target);
        byte[] buf = new byte[1024];
        int len;

        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }

        in.close();
        out.close();
    }

    public String getTime(int hour, int min) {
        String format = "";
        if (hour == 0) {
            hour += 12;
            format = "AM";
        } else if (hour == 12) {
            format = "PM";
        } else if (hour > 12) {
            hour -= 12;
            format = "PM";
        } else {
            format = "AM";
        }
        return new StringBuilder().append(hour).append(" : ").append(min)
                .append(" ").append(format).toString();
    }
}
