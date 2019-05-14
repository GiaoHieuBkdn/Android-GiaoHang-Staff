package com.bys.sangngoc.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bys.sangngoc.R;

/**
 * Created by Admin on 3/31/2017.
 */

public class LoadingImageView extends ImageView {

    public LoadingImageView(Context context) {
        super(context);
        init();
    }

    public LoadingImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadingImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LoadingImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void init(){
        try {
            BitmapDrawable frame1 = (BitmapDrawable) getResources().getDrawable(R.drawable.a);
            BitmapDrawable frame2 = (BitmapDrawable) getResources().getDrawable(
                    R.drawable.b);
            BitmapDrawable frame3 = (BitmapDrawable) getResources().getDrawable(
                    R.drawable.c);
            BitmapDrawable frame4 = (BitmapDrawable) getResources().getDrawable(
                    R.drawable.d);

            final AnimationDrawable Anim = new AnimationDrawable();
            Anim.addFrame(frame1, 200);
            Anim.addFrame(frame2, 200);
            Anim.addFrame(frame3, 200);
            Anim.addFrame(frame4, 200);
            Anim.setOneShot(false);
            this.setBackgroundDrawable(Anim);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    Anim.start();

                }
            }, 5000);

        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
