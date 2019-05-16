package com.bys.sangngoc.tasks;

import android.content.Context;

import com.bys.sangngoc.api.ApiListener;
import com.bys.sangngoc.api.models.GetDeliveryPointsListOutput;

/**
 * Created by dcmen
 */
public class GetHistoryDeliveryPointsListTask extends BaseTask<GetDeliveryPointsListOutput> {
    private int mEmployeesId, mStart, mLimit;
    private long mDeliveryDate;
    private String mStatus;
    private String mSearchTerm;

    public GetHistoryDeliveryPointsListTask(Context context, int employeesId, String searchTerm, int start, int limit, String status, long deliveryDate, ApiListener<GetDeliveryPointsListOutput> listener) {
        super(context, listener);
        this.mEmployeesId = employeesId;
        this.mStart = start;
        this.mLimit = limit;
        this.mStatus = status;
        this.mDeliveryDate = deliveryDate;
        this.mSearchTerm = searchTerm;
    }

    @Override
    protected GetDeliveryPointsListOutput callApiMethod() throws Exception {
        return mApi.getHistoryDeliveryPointsList(mEmployeesId, mSearchTerm, mStart, mLimit, mStatus, mDeliveryDate);
    }
}
