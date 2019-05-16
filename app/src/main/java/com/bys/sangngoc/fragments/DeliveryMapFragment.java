package com.bys.sangngoc.fragments;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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

import java.util.ArrayList;
import java.util.HashMap;

import com.bys.sangngoc.Manifest;
import com.bys.sangngoc.R;
import com.bys.sangngoc.listeners.DeliveryPointsListener;
import com.bys.sangngoc.models.DeliveryPoint;
import com.bys.sangngoc.models.User;
import com.bys.sangngoc.utils.Constants;
import com.bys.sangngoc.views.CustomDialogMapInforConfirm;

/**
 * Created by Admin on 3/16/2018.
 */

public class DeliveryMapFragment extends BaseFragment implements OnMapReadyCallback {
    private GoogleMap mGoogleMap;
    private View mMapView;
    private ArrayList<DeliveryPoint> mArrsData = new ArrayList<>();
    private HashMap<Marker, DeliveryPoint> mHmDeliverPoint = new HashMap<>();
    private DeliveryPointsListener mDeliveryPointsListener;
    private SupportMapFragment mMapFragment;
    private Marker mCurrentMarker;
    private CustomDialogMapInforConfirm mCustomDialogMapInforConfirm;

    private BroadcastReceiver mUpdateLocationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            double lat = intent.getDoubleExtra(Constants.EXTRAX_LATITUDE, -1);
            double lng = intent.getDoubleExtra(Constants.EXTRAX_LONGITUDE, -1);
            if (mGoogleMap != null && lat != -1 && lng != -1) {
                if (mCurrentMarker != null) {
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

    private BroadcastReceiver mChangeStatusBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(Constants.EXTRAX_DELIVERY_POINT)) {
                DeliveryPoint deliveryPoint = (DeliveryPoint) intent.getSerializableExtra(Constants.EXTRAX_DELIVERY_POINT);
                if (deliveryPoint != null) {
                    ArrayList<DeliveryPoint> deliveryPoints = new ArrayList<>();
                    boolean isAdded = false;
                    for (DeliveryPoint item : mArrsData) {
                        if (item.getId() == deliveryPoint.getId()) {
                            isAdded = true;
                            item.setStatus(deliveryPoint.getStatus());
                            if (item.getStatus().equalsIgnoreCase(Constants.STATUS_PROCESSING) || item.getStatus().equalsIgnoreCase(Constants.STATUS_DONT_PROCESSING)) {
                                deliveryPoints.add(item);
                            }
                        } else {
                            deliveryPoints.add(item);
                        }
                    }
                    if (!isAdded) {
                        deliveryPoints.add(deliveryPoint);
                    }
                    addData(deliveryPoints);
                }
            }
        }
    };

    public static DeliveryMapFragment newInstance(DeliveryPointsListener deliveryPointsListener) {
        DeliveryMapFragment fragment = new DeliveryMapFragment();
        fragment.mDeliveryPointsListener = deliveryPointsListener;
        return fragment;
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_delivery_map;
    }

    @Override
    protected void initComponents() {
        try {
            mContext.registerReceiver(mChangeStatusBroadcastReceiver, new IntentFilter(Constants.BROADCAST_CHANGE_STATUS));
            mContext.registerReceiver(mUpdateLocationBroadcastReceiver, new IntentFilter(Constants.BROADCAST_CHANGE_CURRENT_LOCATION));
        } catch (IllegalArgumentException e) {
        }
        mMapFragment = (SupportMapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mMapView = mMapFragment.getView();
        mMapFragment.getMapAsync(this);
        if (mDeliveryPointsListener != null) {
            mDeliveryPointsListener.loadedMap(true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            mContext.unregisterReceiver(mChangeStatusBroadcastReceiver);
            mContext.unregisterReceiver(mUpdateLocationBroadcastReceiver);
        } catch (IllegalArgumentException e) {
        }
    }


    public void addData(ArrayList<DeliveryPoint> deliveryPoints) {
        mArrsData.clear();
        mHmDeliverPoint.clear();
        if (mCustomDialogMapInforConfirm != null) {
            mCustomDialogMapInforConfirm.dismiss();
            mCustomDialogMapInforConfirm = null;
        }
        if (mGoogleMap != null) {
            mGoogleMap.clear();
        }
        for (DeliveryPoint item : deliveryPoints) {
            if (item.getStatus().equalsIgnoreCase(Constants.STATUS_PROCESSING) || item.getStatus().equalsIgnoreCase(Constants.STATUS_DONT_PROCESSING)) {
                mArrsData.add(item);
            }
        }
        if (mGoogleMap != null) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (DeliveryPoint item : mArrsData) {
                if (item != null && item.getDeliveryAddressInfo() != null) {
                    Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(item.getDeliveryAddressInfo().getAddressLatitude(), item.getDeliveryAddressInfo().getAddressLongitude()))
                            .icon(BitmapDescriptorFactory
                                    .fromResource(item.getStatus().equalsIgnoreCase(Constants.STATUS_PROCESSING) ? R.drawable.ic_marker_blue : R.drawable.ic_marker_orange)));
                    mHmDeliverPoint.put(marker, item);
                    builder.include(marker.getPosition());
                }
            }
            if (Constants.LATITUDE != -1 && Constants.LONGITUDE != -1) {
                if (mCurrentMarker != null) {
                    mCurrentMarker.remove();
                }
                mCurrentMarker = mGoogleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Constants.LATITUDE, Constants.LONGITUDE))
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.ic_marker_user)));
                mCurrentMarker.setTag(1);
                builder.include(mCurrentMarker.getPosition());
            }
            if (mHmDeliverPoint.size() > 0) {
                LatLngBounds bounds = builder.build();
                int padding = 300; // offset from edges of the map in pixels
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
//                int width = getResources().getDisplayMetrics().widthPixels;
//                int height = getResources().getDisplayMetrics().heightPixels;
//                int padding = (int) (width * 0.10);
//                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                mGoogleMap.animateCamera(cu);
            } else {
                if (mCurrentMarker != null) {
                    LatLng latLng = new LatLng(mCurrentMarker.getPosition().latitude, mCurrentMarker.getPosition().longitude);
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
                    mGoogleMap.animateCamera(cameraUpdate);
                }
            }
        }
    }

    @Override
    protected void addListener() {

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
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                if (mCurrentMarker != null && mCurrentMarker.getTag() != null && marker.getTag() != null && mCurrentMarker.getTag().equals(marker.getTag())) {
                    return false;
                }
                CountDownTimer mCountDownTimer = new CountDownTimer(500, 500) {
                    @Override
                    public void onTick(long l) {

                    }

                    @Override
                    public void onFinish() {
                        if (mCustomDialogMapInforConfirm != null) {
                            mCustomDialogMapInforConfirm.dismiss();
                            mCustomDialogMapInforConfirm = null;
                        }
                        mCustomDialogMapInforConfirm = new CustomDialogMapInforConfirm(mContext, mHmDeliverPoint.get(marker));
                        mCustomDialogMapInforConfirm.show();
                    }
                };
                mCountDownTimer.start();
                return false;
            }
        });

    }

}
