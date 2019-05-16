package com.bys.sangngoc.activities.drivers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bys.sangngoc.R;
import com.bys.sangngoc.activities.BaseActivity;
import com.bys.sangngoc.activities.EditProfileActivity;
import com.bys.sangngoc.adapters.ConfirmDeliviredAdapter;
import com.bys.sangngoc.adapters.ImagesAdapter;
import com.bys.sangngoc.adapters.SpinnerAdapter;
import com.bys.sangngoc.api.ApiListener;
import com.bys.sangngoc.api.models.BaseOutput;
import com.bys.sangngoc.api.models.GetGeneralDataOutput;
import com.bys.sangngoc.listeners.WaitResizeImageListener;
import com.bys.sangngoc.models.DeliveryItem;
import com.bys.sangngoc.models.DeliveryPoint;
import com.bys.sangngoc.models.Image;
import com.bys.sangngoc.models.KeyValue;
import com.bys.sangngoc.models.ProductQtyOfTypes;
import com.bys.sangngoc.tasks.BaseTask;
import com.bys.sangngoc.tasks.SwitchToDeliveredDeliveryTask;
import com.bys.sangngoc.tasks.SwitchToFailedDeliveryTask;
import com.bys.sangngoc.utils.Constants;
import com.bys.sangngoc.utils.FileUtils;
import com.bys.sangngoc.utils.SharedPreferenceHelper;
import com.bys.sangngoc.utils.StringUtils;
import com.bys.sangngoc.views.ItemOffsetDecoration;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ConfirmDeliveredActivity extends BaseActivity implements View.OnClickListener, ApiListener {


    private TextView mTvDeliveryCode, mTvOrderCode, mTvCustomerCode;
    private File file;
    private Button mBtnConfirm, mBtnReportFail;
    private RecyclerView mRcItems = null;
    private ImageView vsig;
    private LinearLayout vSignature, vPaid;
    private ImageView imvDone, imvCancel;
    private ConfirmDeliviredAdapter mDeliveriedProductAdapter = null;
    private ArrayList<DeliveryItem> mData;
    private TextView tvNav, mTvStatus;
    private DeliveryPoint mCurrentDeliveryPoint;
    private Bitmap bitmap;
    private signature mSignature;
    private ImageView imgCancel, imgBack;
    private String DIRECTORY = Environment.getExternalStorageDirectory().getPath() + "/UserSignature/";
    private String pic_name = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
    private String StoredPath = DIRECTORY + pic_name + ".png";
    private ArrayList<DeliveryItem> productsList = new ArrayList<>();
    private HashMap<String, String> mHmData = new HashMap<>();
    private ArrayList<File> mHmFiles = new ArrayList<>();
    private EditText mEdtNotes;
    private CustomDialogSig dialog;
    private boolean checkSignature = false;

    private RecyclerView mRcImages;
    private ImagesAdapter mImagesAdapter;
    private ArrayList<Image> mImagesData = new ArrayList<>();
    private Uri mImageCaptureUri;
    private String myImagePath;
    private View mViewAddImage;
    private TextView mTvDate, mTvTime, tvCollected, tvOwed;
    private Calendar mCalendar = Calendar.getInstance();
    private boolean isAcceptedDelivery = false;
    private Spinner spReason;
    private RadioButton rdBtnPaid, rdBtnUnpaid;
    private LinearLayout layoutReason;
    private RadioGroup radioGroup;
    private EditText etPaid;
    private SpinnerAdapter spinnerAdapter;
    private ArrayList<String> mValuesData = new ArrayList<>();
    private ArrayList<KeyValue> mKeyValues = new ArrayList<>();

    private TextView tvVND;
    private DecimalFormat mDecimalFormat;
    private boolean hasFractionalPart;
    private DecimalFormat dfnd;

    @Override
    protected int initLayout() {
        return R.layout.activity_confirm_delivered;
    }

    @Override
    protected void initComponents() {
        if (getIntent().hasExtra(Constants.EXTRAX_DELIVERY_POINT)) {
            mCurrentDeliveryPoint = (DeliveryPoint) getIntent().getSerializableExtra(Constants.EXTRAX_DELIVERY_POINT);
        }
        if (getIntent().hasExtra(Constants.EXTRAX_PRODUCTS_LIST)) {
            productsList = (ArrayList<DeliveryItem>) getIntent().getSerializableExtra(Constants.EXTRAX_PRODUCTS_LIST);
        }
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(',');
        mDecimalFormat = new DecimalFormat("#,###.##", otherSymbols);
        mDecimalFormat.setDecimalSeparatorAlwaysShown(true);
        dfnd = new DecimalFormat("#,###", otherSymbols);
        tvNav = findViewById(R.id.tv_title);
        mEdtNotes = findViewById(R.id.tv_order_note);
        mTvCustomerCode = findViewById(R.id.tv_customer_code);
        mTvOrderCode = findViewById(R.id.tv_order_code);
        mTvDeliveryCode = findViewById(R.id.tv_delivery_code);
        mTvStatus = findViewById(R.id.tv_status);
        mBtnConfirm = findViewById(R.id.btn_action);
        mBtnReportFail = findViewById(R.id.btn_action_faild);
        imgCancel = findViewById(R.id.img_cancel);
        imgBack = findViewById(R.id.imv_nav_left);
        mRcItems = findViewById(R.id.rc_content);

        mViewAddImage = findViewById(R.id.view_add_image);
        mTvDate = findViewById(R.id.view_date);
        mTvTime = findViewById(R.id.view_time);
        rdBtnPaid = findViewById(R.id.rd_paid);
        rdBtnUnpaid = findViewById(R.id.rd_unpaid);
        vPaid = findViewById(R.id.layout_paid);
        radioGroup = findViewById(R.id.radio_group);
        tvCollected = findViewById(R.id.tv_money_collect);
        etPaid = findViewById(R.id.et_money_paid);
        tvOwed = findViewById(R.id.tv_money_owned);
        tvVND = findViewById(R.id.tv_vnd);
//        getWindow().setSoftInputMode(
//                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        mData = new ArrayList<>();
        for (DeliveryItem item : productsList) {
            if (item.getProductQtyOfTypes() != null) {
                for (ProductQtyOfTypes productQtyOfTypes : item.getProductQtyOfTypes()) {
                    if (productQtyOfTypes.getProductDeliveredQualityNo().equalsIgnoreCase("Good")) {
                        item.setDeliveredGoodQty(productQtyOfTypes.getProductDeliveryQty());
                        item.setDeliveredGoodQtyTmp(productQtyOfTypes.getProductDeliveryQty());
                        item.setProductAttributeGoodId(productQtyOfTypes.getProductDeliveredQualityId());
                        item.setProductAttributeGoodPrice(productQtyOfTypes.getProductDeliveredUnitPrice());
                    }
                    if (productQtyOfTypes.getProductDeliveredQualityNo().equalsIgnoreCase("Ugly")) {
                        item.setDeliveredBadQty(productQtyOfTypes.getProductDeliveryQty());
                        item.setDeliveredBadQtyTmp(productQtyOfTypes.getProductDeliveryQty());
                        item.setProductAttributeBadId(productQtyOfTypes.getProductDeliveredQualityId());
                        item.setProductAttributeBadPrice(productQtyOfTypes.getProductDeliveredUnitPrice());
                    }
                    if (productQtyOfTypes.getProductDeliveredQualityNo().equalsIgnoreCase("Error")) {
                        item.setDeliveredErrorQty(productQtyOfTypes.getProductDeliveryQty());
                        item.setDeliveredErrorQtyTmp(productQtyOfTypes.getProductDeliveryQty());
                        item.setProductAttributeErrorId(productQtyOfTypes.getProductDeliveredQualityId());
                        item.setProductAttributeErrorPrice(productQtyOfTypes.getProductDeliveredUnitPrice());
                    }
                }
                item.setDeliveredActualQty(item.getDeliveredGoodQty() + item.getDeliveredBadQty() + item.getDeliveredErrorQty());
                item.setmReason(mKeyValues);
            }

            mData.add(item);
        }

        if (mData.size() > 0) {
            mBtnConfirm.setEnabled(true);
        } else {
            mBtnConfirm.setEnabled(false);
        }
        boolean checkTmp = false;
        boolean checkStt = false;
        for (DeliveryItem item : mData) {
            if (item.getQuantityTmp() != 0) {
                checkTmp = true;
                break;
            }
        }
        for (DeliveryItem item : mData) {
            if (item.isStatus()) {
                checkStt = true;
                break;
            }
        }
        if (!checkTmp || !checkStt) {
            mBtnConfirm.setEnabled(false);
        } else {
            mBtnConfirm.setEnabled(true);
        }


        mDeliveriedProductAdapter = new ConfirmDeliviredAdapter(ConfirmDeliveredActivity.this, mData, mValuesData, mKeyValues);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setAutoMeasureEnabled(true);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRcItems.setLayoutManager(linearLayoutManager);
        mRcItems.addItemDecoration(new DividerItemDecoration(ConfirmDeliveredActivity.this, 0));
        mRcItems.setAdapter(mDeliveriedProductAdapter);
        vsig = findViewById(R.id.layoutCanvas);
        file = new File(DIRECTORY);
        if (!file.exists()) {
            file.mkdir();
        }

        mRcImages = findViewById(R.id.rc_images);
        mRcImages.setLayoutManager(new GridLayoutManager(this, 4));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.item_offset);
        mRcImages.addItemDecoration(itemDecoration);
        mImagesAdapter = new ImagesAdapter(this, mImagesData);
        mRcImages.setAdapter(mImagesAdapter);

        loadData();


    }

    @SuppressLint("ResourceAsColor")
    private void loadData() {
        tvNav.setText(R.string.txt_report_delivered);
        mTvDeliveryCode.setText(StringUtils.getString(this, mCurrentDeliveryPoint.getDeliveryPointNo()));
        mTvCustomerCode.setText(StringUtils.getString(this, mCurrentDeliveryPoint.getDeliveryToCustomer() != null ? mCurrentDeliveryPoint.getDeliveryToCustomer().getCustomerNo() : ""));
        mTvOrderCode.setText(StringUtils.getString(this, mCurrentDeliveryPoint.getSaleOrderNo()));

        if (mCurrentDeliveryPoint.getStatus().equalsIgnoreCase(Constants.STATUS_PROCESSING)) {
            mTvStatus.setBackgroundResource(R.drawable.bg_status_blue_top);
            mTvStatus.setText(R.string.txt_status_progressing);
            mTvDeliveryCode.setTextColor(ContextCompat.getColor(ConfirmDeliveredActivity.this, R.color.color_status_blue));
        } else if (mCurrentDeliveryPoint.getStatus().equalsIgnoreCase(Constants.STATUS_DONT_PROCESSING)) {
            mTvStatus.setBackgroundResource(R.drawable.bg_status_orange_top);
            mTvStatus.setText(R.string.txt_status_dont_progressing);
            mTvDeliveryCode.setTextColor(ContextCompat.getColor(ConfirmDeliveredActivity.this, R.color.color_status_orange));
        } else if (mCurrentDeliveryPoint.getStatus().equalsIgnoreCase(Constants.STATUS_CANCEL)) {
            mTvStatus.setBackgroundResource(R.drawable.bg_status_dark_top);
            mTvStatus.setText(R.string.txt_status_cancel);
            mTvDeliveryCode.setTextColor(ContextCompat.getColor(ConfirmDeliveredActivity.this, R.color.color_status_dark));
        } else if (mCurrentDeliveryPoint.getStatus().equalsIgnoreCase(Constants.STATUS_TROUBLE)) {
            mTvStatus.setBackgroundResource(R.drawable.bg_status_red_top);
            mTvStatus.setText(R.string.txt_status_trouble);
            mTvDeliveryCode.setTextColor(ContextCompat.getColor(ConfirmDeliveredActivity.this, R.color.color_status_red));
        } else {
            mTvStatus.setBackgroundResource(R.drawable.bg_status_green_top);
            mTvStatus.setText(R.string.txt_status_completed);
            mTvDeliveryCode.setTextColor(ContextCompat.getColor(ConfirmDeliveredActivity.this, R.color.color_status_green));
        }
        mTvDate.setText(StringUtils.getDateStringFromTimestampFull(mCalendar.getTimeInMillis()));
        mTvTime.setText(getTime(mCalendar.getTimeInMillis()));
        tvCollected.setText(StringUtils.formatPriceByDouble(mCurrentDeliveryPoint.getBalanceDueAmount()) + " (VNĐ)");
        tvOwed.setText(StringUtils.formatPriceByDouble(mCurrentDeliveryPoint.getBalanceDueAmount()) + " (VNĐ)");
        rdBtnUnpaid.setChecked(true);
        String filterJson = SharedPreferenceHelper.getInstance(this).get(Constants.PREF_GENERAL_DATA);

        if (filterJson != null && filterJson.length() > 0) {
            GetGeneralDataOutput generalData = new Gson().fromJson(filterJson, GetGeneralDataOutput.class);
            ArrayList<KeyValue> mKeyValuesTmp = new ArrayList<>();
            mKeyValuesTmp = generalData.result.differenceQtyConfigs;
            int i = 1;
            mValuesData.add("Không có lý do");
            if (mKeyValuesTmp != null) {
                for (KeyValue item : mKeyValuesTmp) {
                    mValuesData.add(item.value);
                    mKeyValues.add(item);
                }
            }
        }


    }


    @Override
    protected void addListener() {
        mBtnConfirm.setOnClickListener(this);
        mBtnReportFail.setOnClickListener(this);
        imgCancel.setOnClickListener(this);
        imgBack.setOnClickListener(this);
//        vsig.setOnClickListener(this);
        mTvDate.setOnClickListener(this);
        mTvTime.setOnClickListener(this);
        mViewAddImage.setOnClickListener(this);

//        NumberTextWatcher numberTextWatcher = new NumberTextWatcher(etPaid);
//        numberTextWatcher.setICustomTextChangeListener(new NumberTextWatcher.ICustomTextChangeListener() {
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                String value = etPaid.getText().toString().trim().replace(",", "");
//                if (value.length() > 0) {
//                    tvVND.setVisibility(View.VISIBLE);
//                } else {
//                    tvVND.setVisibility(View.GONE);
//                }
//                Double paid = Double.parseDouble((StringUtils.isEmptyDot(value) ? "0" : value));
//                tvOwed.setText(StringUtils.formatPriceByDouble(mCurrentDeliveryPoint.getBalanceDueAmount() - paid) + " (VNĐ)");
//            }
//        });
//        etPaid.addTextChangedListener(numberTextWatcher);
        etPaid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().contains(String.valueOf(mDecimalFormat.getDecimalFormatSymbols().getDecimalSeparator()))) {
                    hasFractionalPart = true;
                } else {
                    hasFractionalPart = false;
                }
                String value = etPaid.getText().toString().trim().replace(",", "");
                if (value.length() > 0) {
                    tvVND.setVisibility(View.VISIBLE);
                } else {
                    tvVND.setVisibility(View.GONE);
                }
                Double paid = Double.parseDouble((StringUtils.isEmptyDot(value) ? "0" : value));
                tvOwed.setText(StringUtils.formatPriceByDouble(mCurrentDeliveryPoint.getBalanceDueAmount() - paid) + " (VNĐ)");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                etPaid.removeTextChangedListener(this);
                try {
                    int inilen, endlen;
                    inilen = etPaid.getText().length();

                    String v = editable.toString().replace(String.valueOf(mDecimalFormat.getDecimalFormatSymbols().getGroupingSeparator()), "");
                    Number n = mDecimalFormat.parse(v);
                    int cp = etPaid.getSelectionStart();
                    if (hasFractionalPart) {
                        etPaid.setText(mDecimalFormat.format(n));
                    } else {
                        etPaid.setText(dfnd.format(n));
                    }
                    endlen = etPaid.getText().length();
                    int sel = (cp + (endlen - inilen));
                    if (sel > 0 && sel <= etPaid.getText().length()) {
                        etPaid.setSelection(sel);
                    } else {
                        // place cursor at the end?
                        etPaid.setSelection(etPaid.getText().length() - 1);
                    }
                } catch (NumberFormatException nfe) {
                    // do nothing?
                } catch (ParseException e) {
                    // do nothing?
                    etPaid.setText("");
                }

                etPaid.addTextChangedListener(this);

            }
        });

        mImagesAdapter.setItemListener(new ImagesAdapter.IOnItemClicklistener() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onDeleteClick(final int position) {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(ConfirmDeliveredActivity.this);
                builder.setMessage(ConfirmDeliveredActivity.this.getString(R.string.txt_you_sure_delete_image))
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
        mDeliveriedProductAdapter.setOnItemClickListener(new ConfirmDeliviredAdapter.IOnItemClickedListener() {
            @Override
            public void canCheckedData() {
                double totalQuanlity = 0;
                boolean tmp = false, checkStt = false;
                for (DeliveryItem item : mData) {
                    if (item.getQuantityTmp() != 0.0) {
                        tmp = true;
                        break;
                    }
//                    totalQuanlity += (item.getDeliveredGoodQty() + item.getDeliveredBadQty() + item.getDeliveredErrorQty());
                }
                for (DeliveryItem item : mData) {
                    if (item.isStatus()) {
                        checkStt = true;
                        break;
                    }
                }
                if (!tmp || !checkStt) {
                    mBtnConfirm.setEnabled(false);
                } else {
                    mBtnConfirm.setEnabled(true);
                }
//                if (totalQuanlity > 0) {
//                    //Enable "Xác nhận" button
//                    mBtnConfirm.setEnabled(true);
//                } else {
//                    //Disable "Xác nhận" button
//                    mBtnConfirm.setEnabled(false);
//                }

            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (rdBtnPaid.isChecked()) {
                    vPaid.setVisibility(View.VISIBLE);
                } else {
                    vPaid.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                    break;
                case EditProfileActivity.SELECT_PICTURE:
                    Uri uriPhoto = data.getData();
                    File finalFile1 = new File(FileUtils.getRealPathFromURI(ConfirmDeliveredActivity.this, uriPhoto));
                    try {
                        FileUtils.copy(finalFile1, new File(myImagePath));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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

    @SuppressLint("ResourceType")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
                if (!validateData()) {
                    return;
                }

                for (DeliveryItem item : mData) {
                    if (item.isStatus()) {
                        if ((item.getDeliveredGoodQty() + item.getDeliveredBadQty() + item.getDeliveredErrorQty()) == 0) {
                            Toast.makeText(ConfirmDeliveredActivity.this, R.string.txt_validate_0_value, Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                }

//                if (!checkSignature) {
//                    Toast.makeText(ConfirmDeliveredActivity.this, R.string.txt_validate_signature, Toast.LENGTH_LONG).show();
//                    return;
//                }

                isAcceptedDelivery = false;
                for (DeliveryItem item : mData) {
                    if ((item.getDeliveredGoodQty() != item.getDeliveredGoodQtyTmp()) || (item.getDeliveredBadQty() != item.getDeliveredBadQtyTmp()) || (item.getDeliveredErrorQty() != item.getDeliveredErrorQtyTmp())) {
                        isAcceptedDelivery = true;
                        break;
                    }
                }

//                if (mImagesData.size() == 0) {
//                    toast(R.string.txt_warning_plz_choose_images_delivery);
//                    return;
//                }


                if (isAcceptedDelivery) {
                    if (mImagesData.size() == 0) {
                        toast(R.string.txt_warning_plz_choose_images_delivery);
                        return;
                    }
                }

                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(ConfirmDeliveredActivity.this);
                builder.setMessage(ConfirmDeliveredActivity.this.getString(R.string.txt_confirm_report_delivery_2))
                        .setPositiveButton(R.string.txt_agree, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                submitData(isAcceptedDelivery);

                            }
                        })
                        .setNegativeButton(R.string.txt_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                                dialog.dismiss();
                            }
                        })
                        .show();


                /*if (isShowWarning) {
                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(ConfirmDeliveredActivity.this);
                    builder.setMessage(ConfirmDeliveredActivity.this.getString(R.string.txt_confirm_report_delivery))
                            .setPositiveButton(R.string.txt_acceptance, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    submitData(false);

                                }
                            })
                            .setNegativeButton(R.string.txt_unacceptance, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                    dialog.dismiss();
                                    submitData(true);
                                }
                            })
                            .show();
                } else {
                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(ConfirmDeliveredActivity.this);
                    builder.setMessage(ConfirmDeliveredActivity.this.getString(R.string.txt_confirm_report_delivery_2))
                            .setPositiveButton(R.string.txt_agree, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    submitData(true);

                                }
                            })
                            .setNegativeButton(R.string.txt_cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }*/
                break;
            case R.id.btn_action_faild:
                //Report faild
                if (mImagesData.size() == 0) {
                    toast(R.string.txt_warning_plz_choose_images_delivery);
                    return;
                }
                AlertDialog.Builder builderFaild = new AlertDialog.Builder(ConfirmDeliveredActivity.this);
                builderFaild.setMessage(ConfirmDeliveredActivity.this.getString(R.string.txt_sure_delivery_faild))
                        .setPositiveButton(R.string.txt_agree, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                submitDataOFailedDelivery();
                                dialog.dismiss();


                            }
                        })
                        .setNegativeButton(R.string.txt_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                                dialog.dismiss();
                            }
                        })
                        .show();
                break;
            case R.id.img_cancel:
                mSignature.clear();
                break;
            case R.id.img_confirm:
                Toast.makeText(ConfirmDeliveredActivity.this, "a", Toast.LENGTH_LONG).show();
                break;
            case R.id.layoutCanvas:
                dialog = new CustomDialogSig(ConfirmDeliveredActivity.this);
                dialog.show();
                break;
        }

    }

    public boolean validateData() {
        if (mCalendar.getTimeInMillis() > Calendar.getInstance().getTimeInMillis()) {
            toast(R.string.txt_plz_choose_past_date_time);
            return false;
        }
        boolean isOneOpenning = false;
        for (DeliveryItem item : mData) {
            if (item.isEdited()) {
                isOneOpenning = true;
                break;
            }
        }
        if (isOneOpenning) {
            Toast.makeText(ConfirmDeliveredActivity.this, R.string.txt_plz_done_entering, Toast.LENGTH_LONG).show();
            return false;
        }
        if (rdBtnPaid.isChecked() && etPaid.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(ConfirmDeliveredActivity.this, R.string.txt_input_payment, Toast.LENGTH_LONG).show();
            return false;
        } else if (rdBtnPaid.isChecked() && (StringUtils.formatPrice(Long.parseLong(etPaid.getText().toString().replace(",", ""))).equalsIgnoreCase("0"))) {
            Toast.makeText(ConfirmDeliveredActivity.this, R.string.txt_take_payment, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public void submitDataOFailedDelivery() {
        showLoading(true);
        mHmData.clear();
        mHmFiles.clear();

        mHmData.put("DeliveryPointId", mCurrentDeliveryPoint.getId() + "");
        for (final Image item : mImagesData) {
            mHmFiles.add(new File(item.getPath()));
        }
        mHmData.put("Feedback", mEdtNotes.getText().toString().trim());
        new SwitchToFailedDeliveryTask(ConfirmDeliveredActivity.this, mCurrentDeliveryPoint.getId(), mHmData, mHmFiles, ConfirmDeliveredActivity.this)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void submitData(boolean isAccepting) {
        showLoading(true);
        mHmData.clear();
        mHmFiles.clear();

        mHmData.put("DeliveryPointId", mCurrentDeliveryPoint.getId() + "");
        mHmData.put("ActualDeliveryTime", (mCalendar.getTimeInMillis() + StringUtils.getOffsetInMillis()) / 1000 + "");
        for (final Image item : mImagesData) {
            mHmFiles.add(new File(item.getPath()));
        }

        File file = new File(StoredPath);
        int i = 0;
        for (DeliveryItem item : mData) {
            try {
                mHmData.put("Items[" + i + "][ProductId]", String.valueOf(item.getProductId()));
                mHmData.put("Items[" + i + "][Note]", String.valueOf(item.getNoteItem()));

                mHmData.put("Items[" + i + "][QtyByQuanlities][0][ProductAttributeId]", item.getProductAttributeGoodId() + "");
                mHmData.put("Items[" + i + "][QtyByQuanlities][0][DeliveredQty]", item.getDeliveredGoodQty() + "");

                mHmData.put("Items[" + i + "][QtyByQuanlities][1][ProductAttributeId]", item.getProductAttributeBadId() + "");
                mHmData.put("Items[" + i + "][QtyByQuanlities][1][DeliveredQty]", item.getDeliveredBadQty() + "");

                mHmData.put("Items[" + i + "][QtyByQuanlities][2][ProductAttributeId]", item.getProductAttributeErrorId() + "");
                mHmData.put("Items[" + i + "][QtyByQuanlities][2][DeliveredQty]", item.getDeliveredErrorQty() + "");
                if ((item.getKeyReason() + "").equalsIgnoreCase("")) {
                    mHmData.put("Items[" + i + "][DifferenceQtyId]", 0 + "");
                } else {
                    mHmData.put("Items[" + i + "][DifferenceQtyId]", item.getKeyReason() + "");
                }
            } catch (Exception e) {
            }
            i++;
        }
        mHmData.put("Feedback", mEdtNotes.getText().toString().trim());
        mHmData.put("IsAccepting", String.valueOf(isAccepting));
        new SwitchToDeliveredDeliveryTask(ConfirmDeliveredActivity.this, mCurrentDeliveryPoint.getId(), mHmData, file, mHmFiles, ConfirmDeliveredActivity.this, etPaid.getText().toString().replace(",", ""))
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
                mCalendar.set(Calendar.HOUR_OF_DAY, i);
                mCalendar.set(Calendar.MINUTE, i1);
                textView.setText(getTime(mCalendar.getTimeInMillis()));
            }
        }, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), false).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FileUtils.clearAllSigTmp(ConfirmDeliveredActivity.this);
    }


    @Override
    public void onConnectionOpen(BaseTask task) {

    }

    @Override
    public void onConnectionSuccess(BaseTask task, Object data) {
        if (task instanceof SwitchToDeliveredDeliveryTask || task instanceof SwitchToFailedDeliveryTask) {
            showLoading(false);
            BaseOutput output = (BaseOutput) data;
            if (output.success) {
                if (task instanceof SwitchToFailedDeliveryTask) {
                    mCurrentDeliveryPoint.setStatus(Constants.STATUS_FAILDED);
                } else {
                    mCurrentDeliveryPoint.setStatus(Constants.STATUS_PROCESSED);
                }
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
        if (task instanceof SwitchToDeliveredDeliveryTask || task instanceof SwitchToFailedDeliveryTask) {
            showLoading(false);
            showAlert(exception);
        }
    }

    public class signature extends View {

        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        public boolean isNotEmpty() {
            return path != null && path.isEmpty();
        }

        public void save(View v, String StoredPath) {
            Log.v("log_tag", "Width: " + v.getWidth());
            Log.v("log_tag", "Height: " + v.getHeight());
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(vsig.getWidth(), vsig.getHeight(), Bitmap.Config.RGB_565);
            }
            Canvas canvas = new Canvas(bitmap);
            try {
                FileOutputStream mFileOutStream = new FileOutputStream(StoredPath);
                v.draw(canvas);

                bitmap.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream);
                mFileOutStream.flush();
                mFileOutStream.close();
            } catch (Exception e) {
                Log.v("log_tag", e.toString());
            }

        }

        public void clear() {
            path.reset();
           /* File file = new File(StoredPath);
            file.delete();*/
            invalidate();
        }


        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            //disable scroll
            vsig.getParent().requestDisallowInterceptTouchEvent(true);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:

                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;

                default:
                    debug("Ignored touch event: " + event.toString());
                    return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

        private void debug(String string) {

            Log.v("log_tag", string);

        }

        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }

    public class CustomDialogSig extends Dialog {

        public CustomDialogSig(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.layout_popup_signature);
            Window window = this.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.CENTER;
            wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
            wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            this.getWindow().setAttributes(wlp);
            init();
        }

        public void init() {
            imvCancel = findViewById(R.id.imv_cancel);
            imvDone = findViewById(R.id.imv_done);

            mSignature = new signature(getApplicationContext(), null);
            mSignature.setBackgroundColor(Color.WHITE);
            vSignature = findViewById(R.id.view_signature);
            vSignature.addView(mSignature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            imvDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mSignature.isNotEmpty()) {
                        Toast.makeText(ConfirmDeliveredActivity.this, R.string.txt_validate_signature, Toast.LENGTH_LONG).show();
                    } else {
                        mSignature.save(vSignature, StoredPath);
                        Bitmap bitmap = BitmapFactory.decodeFile(StoredPath);
                        vsig.setImageBitmap(bitmap);
                        checkSignature = true;
                        dialog.dismiss();
                    }
                }
            });
            imvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSignature.clear();
                }
            });
        }
    }

    public String getTime(long timeStamp) {
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("hh:mm aaa");
        return mSimpleDateFormat.format(new Date(timeStamp));
    }

}
