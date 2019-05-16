package com.bys.sangngoc.activities.drivers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

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
import com.bys.sangngoc.adapters.SpinnerLightAdapter;
import com.bys.sangngoc.api.ApiListener;
import com.bys.sangngoc.api.models.BaseOutput;
import com.bys.sangngoc.api.models.GetGeneralDataOutput;
import com.bys.sangngoc.listeners.WaitResizeImageListener;
import com.bys.sangngoc.models.DeliveryPoint;
import com.bys.sangngoc.models.Image;
import com.bys.sangngoc.models.KeyValue;
import com.bys.sangngoc.tasks.BaseTask;
import com.bys.sangngoc.tasks.SwitchToIssuesDeliveryTask;
import com.bys.sangngoc.utils.Constants;
import com.bys.sangngoc.utils.FileUtils;
import com.bys.sangngoc.utils.SharedPreferenceHelper;
import com.bys.sangngoc.utils.StringUtils;
import com.bys.sangngoc.views.ItemOffsetDecoration;

/**
 * Created by Admin on 3/20/2018.
 */

public class ReportTroubleActivity extends BaseActivity implements View.OnClickListener, ApiListener {
    private RecyclerView mRcBrand;
    private ImagesAdapter mImagesAdapter;
    private ArrayList<Image> mImagesData = new ArrayList<>();
    private View mViewAddImage;
    private Uri mImageCaptureUri;
    private String myImagePath;
    private Spinner mSpinnerType;
    private ArrayList<String> mTypeData = new ArrayList<>();
    private SpinnerLightAdapter mTypeAdapter;
    private Button mBtnAction;
    private TextView mTvDeliveryCode;
    private EditText mEdtDescription;
    private DeliveryPoint mCurrentDeliveryPoint;
    private ArrayList<File> mHmFiles = new ArrayList<>();
    private HashMap<String, String> mHmData = new HashMap<>();
    private int mCurrentIssueType = 0;
    private ArrayList<KeyValue> mKeyValues = new ArrayList<>();

    @Override
    protected int initLayout() {
        return R.layout.activity_report_trouble;
    }

    @Override
    protected void initComponents() {
        setTitle(getString(R.string.txt_report_trouble));
        showNavLeft(R.drawable.ic_close, this);
        if (getIntent().hasExtra(Constants.EXTRAX_DELIVERY_POINT)) {
            mCurrentDeliveryPoint = (DeliveryPoint) getIntent().getSerializableExtra(Constants.EXTRAX_DELIVERY_POINT);
        }

        mTvDeliveryCode = (TextView) findViewById(R.id.tv_delivery_code);
        mEdtDescription = (EditText) findViewById(R.id.edt_description);
        mBtnAction = (Button) findViewById(R.id.btn_action);
        mSpinnerType = (Spinner) findViewById(R.id.spinner_type);
        mViewAddImage = findViewById(R.id.view_add_image);
        mRcBrand = (RecyclerView) findViewById(R.id.rc_content);
        mRcBrand.setLayoutManager(new GridLayoutManager(this, 4));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.item_offset);
        mRcBrand.addItemDecoration(itemDecoration);
        mImagesAdapter = new ImagesAdapter(this, mImagesData);
        mRcBrand.setAdapter(mImagesAdapter);

        mTypeAdapter = new SpinnerLightAdapter(this, mTypeData);
        mSpinnerType.setAdapter(mTypeAdapter);
        loadData();

    }

    public void loadData() {
        if(mTvDeliveryCode != null) {
            mTvDeliveryCode.setText(StringUtils.getString(this, mCurrentDeliveryPoint.getDeliveryPointNo()));
        }
        String filterJson = SharedPreferenceHelper.getInstance(this).get(Constants.PREF_GENERAL_DATA);
        if(filterJson != null && filterJson.length() > 0){
            GetGeneralDataOutput generalData = new Gson().fromJson(filterJson, GetGeneralDataOutput.class);
            if(generalData != null && generalData.result.deliveryIssueType != null){
                for (KeyValue item : generalData.result.deliveryIssueType){
                    mTypeData.add(item.value);
                    mKeyValues.add(item);
                }
            }
        }
        mTypeAdapter.notifyDataSetChanged();
    }

    @Override
    protected void addListener() {
        mBtnAction.setOnClickListener(this);
        mImagesAdapter.setItemListener(new ImagesAdapter.IOnItemClicklistener() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onDeleteClick(final int position) {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(ReportTroubleActivity.this);
                builder.setMessage(ReportTroubleActivity.this.getString(R.string.txt_you_sure_delete_image))
                        .setPositiveButton(R.string.txt_accept, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                mImagesData.remove(position);
                                mImagesAdapter.notifyDataSetChanged();
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
        mSpinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mCurrentIssueType = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mViewAddImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imv_nav_left:
                finish();
                break;
            case R.id.view_add_image:
                showDialogChoose();
                break;
            case R.id.btn_action:
                if (validate()) {
                    mHmData.clear();
                    mHmFiles.clear();
                    mHmData.put("DeliveryPointId", mCurrentDeliveryPoint.getId() + "");
                    mHmData.put("IssuedDate", Calendar.getInstance().getTimeInMillis() / 1000 + "");
                    mHmData.put("IssueType", mKeyValues.get(mCurrentIssueType).key);
                    mHmData.put("Description", mEdtDescription.getText().toString());
                    showLoading(true);
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            for (final Image item : mImagesData) {
                                mHmFiles.add(new File(item.getPath()));
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            new SwitchToIssuesDeliveryTask(ReportTroubleActivity.this, mCurrentDeliveryPoint.getId(), mHmData, mHmFiles, ReportTroubleActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        }
                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                break;
        }
    }

    public boolean validate() {
        if (mImagesData.size() == 0) {
            toast(R.string.txt_warning_plz_choose_images_issue);
            return false;
        }
        return true;
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

                    FileUtils.watingToResizeImage(this, myImagePath, new WaitResizeImageListener() {
                        @Override
                        public void resizedImage(String newPath) {
                            mImagesData.add(new Image(newPath));
                            mImagesAdapter.notifyDataSetChanged();
                        }
                    });
//                    try {
//                        mImagesData.add(new Image(FileUtils.resizeImages2(ReportTroubleActivity.this, myImagePath, true, false).getPath()));
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    mImagesAdapter.notifyDataSetChanged();
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

//                    try {
//                        mImagesData.add(new Image(FileUtils.resizeImages(ReportTroubleActivity.this, myImagePath).getPath()));
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    mImagesAdapter.notifyDataSetChanged();


//                    CountDownTimer mCountDownTimer2 = new CountDownTimer(500, 500) {
//                        @Override
//                        public void onTick(long l) {
//
//                        }
//
//                        @Override
//                        public void onFinish() {
//                            showLoading(true);
//                            new AsyncTask<Void, Void, Void>(){
//
//                                @Override
//                                protected Void doInBackground(Void... voids) {
//                                    try {
//                                        File file = FileUtils.resizeImages2(ReportTroubleActivity.this, myImagePath, true);
//                                        if(file != null) {
//                                            mImagesData.add(new Image(file.getPath()));
//                                        }
//                                    } catch (IOException e) {
//                                        e.printStackTrace();
//                                    }
//                                    return null;
//                                }
//
//                                @Override
//                                protected void onPostExecute(Void aVoid) {
//                                    super.onPostExecute(aVoid);
//                                    showLoading(false);
//                                    mImagesAdapter.notifyDataSetChanged();
//                                }
//                            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//                        }
//                    };
//                    mCountDownTimer2.start();

                    FileUtils.watingToResizeImage(this, myImagePath, new WaitResizeImageListener() {
                        @Override
                        public void resizedImage(String newPath) {
                            mImagesData.add(new Image(newPath));
                            mImagesAdapter.notifyDataSetChanged();
                        }
                    });
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

    @Override
    public void onConnectionOpen(BaseTask task) {

    }

    @Override
    public void onConnectionSuccess(BaseTask task, Object data) {
        if (task instanceof SwitchToIssuesDeliveryTask) {
            showLoading(false);
            BaseOutput output = (BaseOutput) data;
            if(output.success){
                mCurrentDeliveryPoint.setStatus(Constants.STATUS_TROUBLE);
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
        if (task instanceof SwitchToIssuesDeliveryTask) {
            showLoading(false);
            showAlert(exception);
        }
    }
}
