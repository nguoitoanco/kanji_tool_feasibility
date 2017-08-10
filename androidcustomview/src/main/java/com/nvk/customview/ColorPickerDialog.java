package com.nvk.customview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.nvk.listener.OnColorChangedListener;

/**
 * Created by KhanhNV10 on 30/03/2016.
 */
public class ColorPickerDialog extends Dialog {
    private OnColorChangedListener mListener;
    private int mInitialColor;

    public ColorPickerDialog(Context context) {
        super(context);
    }

    public ColorPickerDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected ColorPickerDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public ColorPickerDialog(Context context,
                             OnColorChangedListener listener,
                             int initialColor) {
        super(context);

        mListener = listener;
        mInitialColor = initialColor;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnColorChangedListener l = new OnColorChangedListener() {
            public void colorChanged(int color) {
                mListener.colorChanged(color);
                dismiss();
            }
        };

        setContentView(new ColorPickerView(getContext(), l, mInitialColor));
        setTitle("Pick a Color");
    }
}
