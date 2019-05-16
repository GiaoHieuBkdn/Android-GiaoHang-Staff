package com.bys.sangngoc.activities.drivers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bys.sangngoc.models.DeliveryItem;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import com.bys.sangngoc.R;
import com.bys.sangngoc.activities.BaseActivity;
import com.bys.sangngoc.activities.EditProfileActivity;
import com.bys.sangngoc.adapters.ImagesAdapter;
import com.bys.sangngoc.api.ApiListener;
import com.bys.sangngoc.api.models.BaseOutput;
import com.bys.sangngoc.listeners.WaitResizeImageListener;
import com.bys.sangngoc.models.DeliveryPoint;
import com.bys.sangngoc.models.Image;
import com.bys.sangngoc.tasks.BaseTask;
import com.bys.sangngoc.tasks.SwitchToDeliveredDeliveryTask;
import com.bys.sangngoc.utils.Constants;
import com.bys.sangngoc.utils.FileUtils;
import com.bys.sangngoc.utils.StringUtils;
import com.bys.sangngoc.views.ItemOffsetDecoration;

/**
 * Created by Admin on 3/20/2018.
 */

public class ReportDeliveredActivity extends BaseActivity implements View.OnClickListener, ApiListener {
    private final int RQ_CONFIRM_DELIVERY = 9819;
    private RecyclerView mRcBrand;
    private ImagesAdapter mAdapter;
    private ArrayList<Image> mImagesData = new ArrayList<>();
    private View mViewAddImage;
    private TextView mTvDate, mTvTime, mTvStatus;
    private Uri mImageCaptureUri;
    private String myImagePath;
    private Button mBtnAction;
    private TextView mTvDeliveryCode, mTvOrderCode, mTvCustomerCode;
    private DeliveryPoint mCurrentDeliveryPoint;
    private Calendar mCalendar = Calendar.getInstance();
    private ArrayList<File> mHmFiles = new ArrayList<>();
    private HashMap<String, String> mHmData = new HashMap<>();
    private ArrayList<DeliveryItem> productsList = new ArrayList<>();

    @Override
    protected int initLayout() {
        return R.layout.activity_report_delivered;
    }

    @Override
    protected void initComponents() {
        setTitle(getString(R.string.txt_report_delivered));
        showNavLeft(R.drawable.ic_close, this);
        if (getIntent().hasExtra(Constants.EXTRAX_DELIVERY_POINT)) {
            mCurrentDeliveryPoint = (DeliveryPoint) getIntent().getSerializableExtra(Constants.EXTRAX_DELIVERY_POINT);
        }
        if (getIntent().hasExtra(Constants.EXTRAX_PRODUCTS_LIST)) {
            productsList = (ArrayList<DeliveryItem>) getIntent().getSerializableExtra(Constants.EXTRAX_PRODUCTS_LIST);
        }

        mTvDeliveryCode = findViewById(R.id.tv_delivery_code);
        mTvCustomerCode = findViewById(R.id.tv_customer_code);
        mTvOrderCode = findViewById(R.id.tv_order_code);
        mTvStatus = findViewById(R.id.tv_status);

        mBtnAction = findViewById(R.id.btn_action);
        mViewAddImage = findViewById(R.id.view_add_image);
        mTvDate = findViewById(R.id.view_date);
        mTvTime = findViewById(R.id.view_time);
        mRcBrand = findViewById(R.id.rc_content);
        mRcBrand.setLayoutManager(new GridLayoutManager(this, 4));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.item_offset);
        mRcBrand.addItemDecoration(itemDecoration);
        mAdapter = new ImagesAdapter(this, mImagesData);
        mRcBrand.setAdapter(mAdapter);

        loadData();

    }

    @SuppressLint("ResourceAsColor")
    public void loadData() {
        mTvDeliveryCode.setText(StringUtils.getString(this, mCurrentDeliveryPoint.getDeliveryPointNo()));
        mTvCustomerCode.setText(StringUtils.getString(this, mCurrentDeliveryPoint.getDeliveryToCustomer() != null ? mCurrentDeliveryPoint.getDeliveryToCustomer().getCustomerNo() : ""));
        mTvOrderCode.setText(StringUtils.getString(this, mCurrentDeliveryPoint.getSaleOrderNo()));
        if (mCurrentDeliveryPoint.getStatus().equalsIgnoreCase(Constants.STATUS_PROCESSING)) {
            mTvStatus.setBackgroundResource(R.drawable.bg_status_blue_top);
            mTvStatus.setText(R.string.txt_status_progressing);
            mTvDeliveryCode.setTextColor(ContextCompat.getColor(ReportDeliveredActivity.this,R.color.color_status_blue ));
        } else if (mCurrentDeliveryPoint.getStatus().equalsIgnoreCase(Constants.STATUS_DONT_PROCESSING)) {
            mTvStatus.setBackgroundResource(R.drawable.bg_status_orange_top);
            mTvStatus.setText(R.string.txt_status_dont_progressing);
            mTvDeliveryCode.setTextColor(ContextCompat.getColor(ReportDeliveredActivity.this,R.color.color_status_orange ));
        } else if (mCurrentDeliveryPoint.getStatus().equalsIgnoreCase(Constants.STATUS_CANCEL)) {
            mTvStatus.setBackgroundResource(R.drawable.bg_status_dark_top);
            mTvStatus.setText(R.string.txt_status_cancel);
            mTvDeliveryCode.setTextColor(ContextCompat.getColor(ReportDeliveredActivity.this,R.color.color_status_dark ));
        } else if (mCurrentDeliveryPoint.getStatus().equalsIgnoreCase(Constants.STATUS_TROUBLE)) {
            mTvStatus.setBackgroundResource(R.drawable.bg_status_red_top);
            mTvStatus.setText(R.string.txt_status_trouble);
            mTvDeliveryCode.setTextColor(ContextCompat.getColor(ReportDeliveredActivity.this,R.color.color_status_red ));
        } else {
            mTvStatus.setBackgroundResource(R.drawable.bg_status_green_top);
            mTvStatus.setText(R.string.txt_status_completed);
            mTvDeliveryCode.setTextColor(ContextCompat.getColor(ReportDeliveredActivity.this,R.color.color_status_green ));
        }

        mTvDate.setText(StringUtils.getDateStringFromTimestampFull(mCalendar.getTimeInMillis()));
        mTvTime.setText(getTime(mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE)));
    }

    @Override
    protected void addListener() {
        mBtnAction.setOnClickListener(this);
        mAdapter.setItemListener(new ImagesAdapter.IOnItemClicklistener() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onDeleteClick(final int position) {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(ReportDeliveredActivity.this);
                builder.setMessage(ReportDeliveredActivity.this.getString(R.string.txt_you_sure_delete_image))
                        .setPositiveButton(R.string.txt_accept, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                mImagesData.remove(position);
                                mAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(R.string.txt_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
            }
        });
        mTvDate.setOnClickListener(this);
        mTvTime.setOnClickListener(this);
        mViewAddImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imv_nav_left:
                finish();
                break;
            case R.id.view_date:
                showDatePicker(mTvDate);
                break;
            case R.id.view_time:
                showTimePicker(mTvTime);
                break;
            case R.id.view_add_image:
                showDialogChoose();
                break;
            case R.id.btn_action:
                if (validate()) {
                    /*AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(ReportDeliveredActivity.this);
                    builder.setCancelable(false);
                    builder.setMessage(ReportDeliveredActivity.this.getString(R.string.txt_confirm_report_delivery))
                            .setPositiveButton(R.string.txt_agree, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    submitData();
                                }
                            })
                            .setNegativeButton(R.string.txt_cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                    dialog.dismiss();
                                }
                            })
                            .show();*/
                    mHmData.clear();
                    mHmFiles.clear();
                    mHmData.put("DeliveryPointId", mCurrentDeliveryPoint.getId() + "");
                    mHmData.put("ActualDeliveryTime", (mCalendar.getTimeInMillis() + StringUtils.getOffsetInMillis()) / 1000 + "");
                    for (final Image item : mImagesData) {
                        mHmFiles.add(new File(item.getPath()));
                    }
                    Intent i = new Intent(this, ConfirmDeliveredActivity.class);
                    i.putExtra(Constants.EXTRAX_DELIVERY_POINT, mCurrentDeliveryPoint);
                    i.putExtra(Constants.EXTRAX_PRODUCTS_LIST, productsList);
                    i.putExtra(Constants.EXTRAX_HASHMAP_TIME, mHmData);
                    i.putExtra(Constants.EXTRAX_IMAGES, mHmFiles);
                    startActivityForResult(i, RQ_CONFIRM_DELIVERY);

                }

                break;
        }
    }

//    public void submitData(){
//        mHmData.clear();
//        mHmFiles.clear();
//        mHmData.put("DeliveryPointId", mCurrentDeliveryPoint.getId() + "");
//        mHmData.put("ActualDeliveryTime", (mCalendar.getTimeInMillis() + StringUtils.getOffsetInMillis()) / 1000 + "");
//        showLoading(true);
//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... voids) {
//                for (final Image item : mImagesData) {
//                    mHmFiles.add(new File(item.getPath()));
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                super.onPostExecute(aVoid);
//                new SwitchToDeliveredDeliveryTask(ReportDeliveredActivity.this, mCurrentDeliveryPoint.getId(), mHmData, mHmFiles, ReportDeliveredActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//            }
//        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//    }

    public boolean validate() {
        if(mCalendar.getTimeInMillis() > Calendar.getInstance().getTimeInMillis()){
            toast(R.string.txt_plz_choose_past_date_time);
            return false;
        }
        if (mImagesData.size() == 0) {
            toast(R.string.txt_warning_plz_choose_images_delivery);
            return false;
        }
        return true;
    }

    @Override
    public void onConnectionOpen(BaseTask task) {

    }

    @Override
    public void onConnectionSuccess(BaseTask task, Object data) {
        if (task instanceof SwitchToDeliveredDeliveryTask) {
            showLoading(false);
            BaseOutput output = (BaseOutput) data;
            if (output.success) {
                mCurrentDeliveryPoint.setStatus(Constants.STATUS_PROCESSED);
                Intent i = new Intent(Constants.BROADCAST_CHANGE_STATUS);
                i.putExtra(Constants.EXTRAX_DELIVERY_POINT, mCurrentDeliveryPoint);
                sendBroadcast(i);
                setResult(RESULT_OK);
                finish();
            } else {
                StringUtils.errorMessageForDelivery(this, output.errorMessage);
            }
        }
    }

    @Override
    public void onConnectionError(BaseTask task, Exception exception) {
        if (task instanceof SwitchToDeliveredDeliveryTask) {
            showLoading(false);
            showAlert(exception);
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
                case RQ_CONFIRM_DELIVERY:
                    setResult(RESULT_OK);
                    finish();
                    break;
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
//                    try {
//                        mImagesData.add(new Image(FileUtils.resizeImages(ReportDeliveredActivity.this, myImagePath).getPath()));
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    mAdapter.notifyDataSetChanged();
                    FileUtils.watingToResizeImage(this, myImagePath, new WaitResizeImageListener() {
                        @Override
                        public void resizedImage(String newPath) {
                            mImagesData.add(new Image(newPath));
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                    break;
                case EditProfileActivity.SELECT_PICTURE:
                    Uri uriPhoto = data.getData();
                    File finalFile1 = new File(getRealPathFromURI(uriPhoto));
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
                    FileUtils.watingToResizeImage(this, myImagePath, new WaitResizeImageListener() {
                        @Override
                        public void resizedImage(String newPath) {
                            mImagesData.add(new Image(newPath));
                            mAdapter.notifyDataSetChanged();
                        }
                    });
//                    try {
//                        mImagesData.add(new Image(FileUtils.resizeImages(ReportDeliveredActivity.this, myImagePath).getPath()));
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                    mAdapter.notifyDataSetChanged();
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

    public void showDatePicker(final TextView textView) {
        new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view,
                                          int year, int monthOfYear,
                                          int dayOfMonth) {
                        textView.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        mCalendar.set(Calendar.MONTH, monthOfYear);
                        mCalendar.set(Calendar.YEAR, year);
                    }
                }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void showTimePicker(final TextView textView) {
        new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                textView.setText(getTime(i, i1));
                mCalendar.set(Calendar.HOUR_OF_DAY, i);
                mCalendar.set(Calendar.MINUTE, i1);
            }
        }, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), false).show();
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
