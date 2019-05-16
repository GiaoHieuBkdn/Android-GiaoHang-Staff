package com.bys.sangngoc.tasks;

import android.content.Context;

import com.bys.sangngoc.api.ApiListener;
import com.bys.sangngoc.api.models.GetDetailDeliveryPointOutput;
import com.bys.sangngoc.api.models.GetDetailDeliveryPointOutput;

/**
 * Created by dcmen
 */
public class GetDetailDeliveryPointTask extends BaseTask<GetDetailDeliveryPointOutput> {
    private int mDeliveryPointId;

    public GetDetailDeliveryPointTask(Context context, int deliveryPointId, ApiListener<GetDetailDeliveryPointOutput> listener) {
        super(context, listener);
        this.mDeliveryPointId = deliveryPointId;
    }

    @Override
    protected GetDetailDeliveryPointOutput callApiMethod() throws Exception {
        return mApi.getDetailDeliveryPoint(mDeliveryPointId);
    }
}
