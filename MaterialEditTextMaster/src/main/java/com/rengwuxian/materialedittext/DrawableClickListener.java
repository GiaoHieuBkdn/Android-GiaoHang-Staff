package com.rengwuxian.materialedittext;

/**
 * Created by Admin on 1/4/2018.
 */

public interface DrawableClickListener {
    public static enum DrawablePosition { TOP, BOTTOM, LEFT, RIGHT };
    public void onClick(DrawablePosition target);
}
