package com.bys.sangngoc.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import com.bys.sangngoc.fragments.DeliveryMapFragment;
import com.bys.sangngoc.fragments.DeliveryPointsFragment;
import com.bys.sangngoc.fragments.DeliveryPointsListFragment;
import com.bys.sangngoc.listeners.DeliveryPointsListener;

/**
 * Created by Admin on 3/6/2018.
 */

public class DeliveryPointsPagerAdapter extends FragmentStatePagerAdapter {
    private DeliveryPointsListener mDeliveryPointsListener;
    private DeliveryPointsListFragment mDeliveryPointsListFragment;
    private DeliveryMapFragment mDeliveryMapFragment;

    public DeliveryPointsPagerAdapter(FragmentManager fragmentManager, DeliveryPointsListener deliveryPointsListener) {
        super(fragmentManager);
        mDeliveryPointsListener = deliveryPointsListener;
        mDeliveryPointsListFragment = DeliveryPointsListFragment.newInstance(deliveryPointsListener);
        mDeliveryMapFragment = DeliveryMapFragment.newInstance(deliveryPointsListener);

    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return mDeliveryPointsListFragment;
            case 1:
                return mDeliveryMapFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (position >= getCount()) {
            FragmentManager manager = ((Fragment) object).getFragmentManager();
            FragmentTransaction trans = manager.beginTransaction();
            trans.remove((Fragment) object);
            trans.commit();
        }
    }
}
