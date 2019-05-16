package com.bys.sangngoc.fragments;

import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;

import com.bys.sangngoc.R;
import com.bys.sangngoc.utils.Constants;
import com.bys.sangngoc.views.CustomDialogWarningMyLocation;

/**
 * Created by Admin on 3/16/2018.
 */

public class MyLocationFragment extends BaseFragment implements OnMapReadyCallback, View.OnClickListener {
    private GoogleMap mGoogleMap;
    private View mMapView;
    private SupportMapFragment mMapFragment;
    private Marker mCurrentMarker;
    private TextView mTvTitleMessage, mTvTitleMessage2, mTvDate, mTvTime;
    private View mViewTime;
    private Button mBtnAction;
    private Calendar mCalendar;


    public static MyLocationFragment newInstance() {
        MyLocationFragment fragment = new MyLocationFragment();
        return fragment;
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_my_location;
    }

    @Override
    protected void initComponents() {

        mTvTitleMessage = mView.findViewById(R.id.tv_title_message);
        mTvTitleMessage2 = mView.findViewById(R.id.tv_title_message_2);
        mTvDate = mView.findViewById(R.id.tv_date);
        mTvTime = mView.findViewById(R.id.tv_time);
        mViewTime = mView.findViewById(R.id.view_time);
        mBtnAction = mView.findViewById(R.id.btn_action);

        mMapFragment = (SupportMapFragment) getFragmentManager()
                .findFragmentById(R.id.map_my_location);
        mMapView = mMapFragment.getView();
        mMapFragment.getMapAsync(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            Fragment f = getFragmentManager().findFragmentById(R.id.map_my_location);
            if (f != null) {
                getFragmentManager().beginTransaction()
                        .remove(f).commit();
            }
        } catch (Exception e) {
        }
    }


    public void addMyLocation() {
        if (mGoogleMap != null) {
            mGoogleMap.clear();
        }
        if (mGoogleMap != null) {
            if (Constants.LATITUDE != -1 && Constants.LONGITUDE != -1) {
                if (mCurrentMarker != null) {
                    mCurrentMarker.remove();
                }
                mCurrentMarker = mGoogleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Constants.LATITUDE, Constants.LONGITUDE))
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.ic_marker_user)));
                mCurrentMarker.setTag(1);
                LatLng latLng = new LatLng(mCurrentMarker.getPosition().latitude, mCurrentMarker.getPosition().longitude);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
                mGoogleMap.animateCamera(cameraUpdate);
            }
        }
    }

    @Override
    protected void addListener() {
        mViewTime.setOnClickListener(this);
        mBtnAction.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_action:
                new CustomDialogWarningMyLocation(mContext).show();
                break;
            case R.id.view_time:
                showTimePicker(mTvTime);
                break;
        }
    }

    public void showTimePicker(final TextView textView) {
        if (mCalendar == null) {
            mCalendar = Calendar.getInstance();
        }
        new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                textView.setText(getTime(i, i1));
                mCalendar.set(Calendar.HOUR_OF_DAY, i);
                mCalendar.set(Calendar.MINUTE, i1);
            }
        }, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), false).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
            View locationButton = ((View) mMapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            rlp.setMargins(0, 100, 25, 0);
        }
        addMyLocation();
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
