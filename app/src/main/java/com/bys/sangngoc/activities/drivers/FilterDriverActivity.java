package com.bys.sangngoc.activities.drivers;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import com.bys.sangngoc.R;
import com.bys.sangngoc.activities.BaseActivity;
import com.bys.sangngoc.adapters.SpinnerAdapter;
import com.bys.sangngoc.api.models.GetGeneralDataOutput;
import com.bys.sangngoc.models.KeyValue;
import com.bys.sangngoc.utils.Constants;
import com.bys.sangngoc.utils.SharedPreferenceHelper;
import com.bys.sangngoc.utils.StringUtils;

/**
 * Created by Admin on 3/5/2018.
 */

public class FilterDriverActivity extends BaseActivity implements View.OnClickListener {
    private View mViewDate;
    private TextView mTvDate;
    private Spinner mSpinnerDepartment;
    private SpinnerAdapter mStatusAdapter;
    private ArrayList<String> mValuesData = new ArrayList<>();
    private ArrayList<KeyValue> mKeyValues = new ArrayList<>();
    private String mCurrentFilterStatus = "";
    private Calendar mCurrentCalendar = Calendar.getInstance();
    private Button mBtnDelete;
    private boolean mTypeFromHistory = false;
    private TextView mTvTitleDate;

    @Override
    protected int initLayout() {
        return R.layout.activity_filter_driver;
    }

    @Override
    protected void initComponents() {
        if(getIntent().hasExtra(Constants.EXTRAX_FROM_HISTORY)){
            mTypeFromHistory = getIntent().getBooleanExtra(Constants.EXTRAX_FROM_HISTORY, false);
        }
        if(getIntent().hasExtra(Constants.EXTRAX_STATUS)){
            mCurrentFilterStatus = getIntent().getStringExtra(Constants.EXTRAX_STATUS);
        }
        long currentFilterDateDelivery = 0;
        if(getIntent().hasExtra(Constants.EXTRAX_DATE_DELIVERY)){
            currentFilterDateDelivery = getIntent().getLongExtra(Constants.EXTRAX_DATE_DELIVERY, 0);
        }
        if(currentFilterDateDelivery != 0){
            mCurrentCalendar.setTimeInMillis(currentFilterDateDelivery);
        }
        mCurrentCalendar.set(Calendar.HOUR_OF_DAY, 0);
        mCurrentCalendar.set(Calendar.MINUTE, 0);
        mCurrentCalendar.set(Calendar.SECOND, 0);
        mCurrentCalendar.set(Calendar.MILLISECOND, 0);
        setTitle(getString(R.string.txt_filter));
        showNavLeft(R.drawable.ic_close, this);
        showNavRight(R.drawable.ic_tick, this);
        mViewDate = findViewById(R.id.view_date);
        mTvDate = (TextView) findViewById(R.id.tv_date);
        mTvTitleDate = (TextView) findViewById(R.id.tv_date_title);
        mBtnDelete = (Button) findViewById(R.id.btn_delete);
        mSpinnerDepartment = (Spinner) findViewById(R.id.spinner_department);
        mStatusAdapter = new SpinnerAdapter(this, mValuesData);
        mSpinnerDepartment.setAdapter(mStatusAdapter);

        if(mTypeFromHistory){
            mTvTitleDate.setText(getString(R.string.txt_filter_delivery_2));
        } else {
            mTvTitleDate.setText(getString(R.string.txt_filter_delivery_1));
        }
        loadData();
    }

    public void loadData() {
        mTvDate.setText(StringUtils.getDateStringFromTimestampFull(mCurrentCalendar.getTimeInMillis()));

        int currentPosition = 0;
        mValuesData.add(getString(R.string.txt_all));
        mKeyValues.add(new KeyValue("", ""));
        String filterJson = SharedPreferenceHelper.getInstance(this).get(Constants.PREF_GENERAL_DATA);
        if(filterJson != null && filterJson.length() > 0){
            GetGeneralDataOutput generalData = new Gson().fromJson(filterJson, GetGeneralDataOutput.class);
            ArrayList<KeyValue> mKeyValuesTmp = new ArrayList<>();
            if(mTypeFromHistory){
                if(generalData != null && generalData.result.historyDeliveryPointStatus != null){
                    mKeyValuesTmp = generalData.result.historyDeliveryPointStatus;
                }
            } else {
                if(generalData != null && generalData.result.toDoDeliveryPointStatus != null){
                    mKeyValuesTmp = generalData.result.toDoDeliveryPointStatus;
                }
            }

            int i = 1;
            for (KeyValue item : mKeyValuesTmp){
                mValuesData.add(item.value);
                mKeyValues.add(item);
                if(mCurrentFilterStatus != null && mCurrentFilterStatus.equalsIgnoreCase(item.key)){
                    currentPosition = i;
                }
                i++;
            }
        }
        mStatusAdapter.notifyDataSetChanged();
        mSpinnerDepartment.setSelection(currentPosition);

    }

    @Override
    protected void addListener() {
        mViewDate.setOnClickListener(this);
        mBtnDelete.setOnClickListener(this);
        mSpinnerDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mCurrentFilterStatus = mKeyValues.get(i).key;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imv_nav_left:
                finish();
                break;
            case R.id.imv_nav_right:
                Intent ii = new Intent();
                ii.putExtra(Constants.EXTRAX_STATUS, mCurrentFilterStatus);
                ii.putExtra(Constants.EXTRAX_DATE_DELIVERY, mCurrentCalendar.getTimeInMillis());
                setResult(RESULT_OK, ii);
                finish();
                break;
            case R.id.btn_delete:
                Intent i = new Intent();
                i.putExtra(Constants.EXTRAX_STATUS, "");
                i.putExtra(Constants.EXTRAX_DATE_DELIVERY, 0);
                setResult(RESULT_OK, i);
                finish();
                break;
            case R.id.view_date:
                new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view,
                                                  int year, int monthOfYear,
                                                  int dayOfMonth) {
                                mCurrentCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                mCurrentCalendar.set(Calendar.MONTH, monthOfYear);
                                mCurrentCalendar.set(Calendar.YEAR, year);
                                mCurrentCalendar.set(Calendar.HOUR_OF_DAY, 0);
                                mCurrentCalendar.set(Calendar.MINUTE, 0);
                                mCurrentCalendar.set(Calendar.SECOND, 0);
                                mCurrentCalendar.set(Calendar.MILLISECOND, 0);

                                mTvDate.setText(StringUtils.getDateStringFromTimestampFull(mCurrentCalendar.getTimeInMillis()));
                            }
                        }, mCurrentCalendar.get(Calendar.YEAR), mCurrentCalendar.get(Calendar.MONTH), mCurrentCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;

        }
    }
}
