package com.bys.sangngoc.tasks;

import android.content.Context;

import com.bys.sangngoc.api.ApiListener;
import com.bys.sangngoc.api.models.BaseOutput;
import com.bys.sangngoc.api.objects.DeleteImagesInput;

/**
 * Created by dcmen
 */
public class DeleteImagesTaskTask extends BaseTask<BaseOutput> {
    private DeleteImagesInput mInput;

    public DeleteImagesTaskTask(Context context, DeleteImagesInput input, ApiListener<BaseOutput> listener) {
        super(context, listener);
        this.mInput = input;
    }

    @Override
    protected BaseOutput callApiMethod() throws Exception {
        return mApi.deleteImageTask(mInput);
    }
}
