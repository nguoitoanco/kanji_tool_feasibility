package com.nvk.customview;

import android.graphics.Paint;
import android.graphics.Path;

/**
 * Created by nguoitoanco on 8/3/2016.
 */
public class DrawObject {
    public Path path;
    public int mColor;
    public int mSize;

    public DrawObject(Path path, int color, int size) {
        this.path = path;
        mColor = color;
        mSize = size;
    }
}
