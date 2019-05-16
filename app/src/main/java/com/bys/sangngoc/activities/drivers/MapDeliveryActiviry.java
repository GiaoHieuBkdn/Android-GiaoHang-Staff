package com.bys.sangngoc.activities.drivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

import com.bys.sangngoc.R;
import com.bys.sangngoc.activities.BaseActivity;
import com.bys.sangngoc.utils.Constants;
import com.bys.sangngoc.views.CustomDialogMapInforConfirm;

/**
 * Created by Admin on 3/21/2018.
 */

public class MapDeliveryActiviry extends BaseActivity implements OnMapReadyCallback, View.OnClickListener {
    private GoogleMap mGoogleMap;
    private View mMapView;
    private double mLatitude, mLongitude;
    private String mAddress;
    private Marker mCurrentMarker;
    private String mTypeStatus;

    private BroadcastReceiver mUpdateLocationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            double lat = intent.getDoubleExtra(Constants.EXTRAX_LATITUDE, -1);
            double lng = intent.getDoubleExtra(Constants.EXTRAX_LONGITUDE, -1);
            if(mGoogleMap != null && lat != -1 && lng != -1) {
                if(mCurrentMarker != null){
                    mCurrentMarker.remove();
                }
                mCurrentMarker = mGoogleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lat, lng))
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.ic_marker_user)));
                mCurrentMarker.setTag(1);
            }
        }
    };

    @Override
    protected int initLayout() {
        return R.layout.activity_delivery_map;
    }

    @Override
    protected void initComponents() {
        registerReceiver(mUpdateLocationBroadcastReceiver, new IntentFilter(Constants.BROADCAST_CHANGE_CURRENT_LOCATION));
        mLatitude = getIntent().getDoubleExtra(Constants.EXTRAX_LATITUDE, 0);
        mLongitude = getIntent().getDoubleExtra(Constants.EXTRAX_LONGITUDE, 0);
        mAddress = getIntent().getStringExtra(Constants.EXTRAX_ADDRESS);
        if(mAddress == null){
            mAddress = "";
        }
        if(getIntent().hasExtra(Constants.EXTRAX_STATUS)) {
            mTypeStatus = getIntent().getStringExtra(Constants.EXTRAX_STATUS);
        }
        setTitle(getString(R.string.txt_delivery_location));
        showNavLeft(R.drawable.ic_back, this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mMapView = mapFragment.getView();
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mUpdateLocationBroadcastReceiver);
    }

    @Override
    protected void addListener() {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        }
        addMarker();
    }

    public void addMarker() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        int resId = R.drawable.ic_marker_blue;
        if (mTypeStatus.equalsIgnoreCase(Constants.STATUS_PROCESSING)) {
            resId = R.drawable.ic_marker_blue;
        } else if (mTypeStatus.equalsIgnoreCase(Constants.STATUS_DONT_PROCESSING)) {
            resId = R.drawable.ic_marker_orange;
        } else if (mTypeStatus.equalsIgnoreCase(Constants.STATUS_CANCEL)) {
            resId = R.drawable.ic_marker_dark;
        } else if (mTypeStatus.equalsIgnoreCase(Constants.STATUS_TROUBLE)) {
            resId = R.drawable.ic_marker_red;
        }  else if (mTypeStatus.equalsIgnoreCase(Constants.STATUS_FAILDED)) {
            resId = R.drawable.ic_marker_dark;
        } else {
            resId = R.drawable.ic_marker_green;
        }
        Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(mLatitude, mLongitude))
                .title(mAddress)
                .icon(BitmapDescriptorFactory
                        .fromResource(resId)));
        builder.include(marker.getPosition());
        if(Constants.LATITUDE != -1 && Constants.LONGITUDE != -1){
            if(mCurrentMarker != null){
                mCurrentMarker.remove();
            }
            mCurrentMarker = mGoogleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(Constants.LATITUDE, Constants.LONGITUDE))
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.ic_marker_user)));
            mCurrentMarker.setTag(1);
            builder.include(mCurrentMarker.getPosition());

            if(mLatitude != 0 && mLongitude != 0) {
                LatLngBounds bounds = builder.build();
//                int padding = 100; // offset from edges of the map in pixels
                int width = getResources().getDisplayMetrics().widthPixels;
                int height = getResources().getDisplayMetrics().heightPixels;
                int padding = (int) (width * 0.32); // offset from edges of the map 12% of screen
//                mGoogleMap.setPadding(50, 50, 50, 50);
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                mGoogleMap.animateCamera(cu);
            } else {
                LatLng latLng = new LatLng(Constants.LATITUDE, Constants.LONGITUDE);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
                mGoogleMap.animateCamera(cameraUpdate);
            }
        } else {
            LatLng latLng = new LatLng(mLatitude, mLongitude);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
            mGoogleMap.animateCamera(cameraUpdate);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imv_nav_left:
                finish();
                break;
        }
    }
}
