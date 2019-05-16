package com.bys.sangngoc.tasks;

import android.content.Context;

import com.bys.sangngoc.api.ApiListener;
import com.bys.sangngoc.api.models.DepartmentRoomOutput;

/**
 * Created by dcmen
 */
public class GetDepartmentsRoomFromDepartmentIdTask extends BaseTask<DepartmentRoomOutput> {
    private int mDepartmentId;

    public GetDepartmentsRoomFromDepartmentIdTask(Context context, int departmentId, ApiListener<DepartmentRoomOutput> listener) {
        super(context, listener);
        this.mDepartmentId = departmentId;
    }

    @Override
    protected DepartmentRoomOutput callApiMethod() throws Exception {
        return mApi.getDepartmentRoom(mDepartmentId);
    }
}
