package com.bys.sangngoc.listeners;

/**
 * Created by Admin on 3/27/2018.
 */

public interface DeliveryPointsListener {
    public void loadedList(boolean isLoad);
    public void loadedMap(boolean isLoad);
    public void loadMoreData(int start);
    public void removeItem(int deliveryId);
    public void pullToRefresh();
}
