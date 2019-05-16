package com.bys.sangngoc.tasks;

import android.content.Context;

import com.bys.sangngoc.api.ApiListener;
import com.bys.sangngoc.api.models.DepartmentOutput;


/**
 * Created by dcmen
 */
public class GetDepartmentsFromBranchIdTask extends BaseTask<DepartmentOutput> {
    private int mBranchId;

    public GetDepartmentsFromBranchIdTask(Context context, int branchId, ApiListener<DepartmentOutput> listener) {
        super(context, listener);
        this.mBranchId = branchId;
    }

    @Override
    protected DepartmentOutput callApiMethod() throws Exception {
        return mApi.getDepartment(mBranchId);
    }
}
