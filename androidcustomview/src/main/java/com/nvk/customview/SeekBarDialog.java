package com.nvk.customview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.nvk.listener.OnSeekBarChangeListener;

/**
 * Created by KhanhNV10 on 30/03/2016.
 */
public class SeekBarDialog extends Dialog implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {
    private OnSeekBarChangeListener mListener;
    private OnHorizontalScrollListener mHorizontalListener;

    private View mView;
    private int mMax;
    private int defaultValue = 0;
    private String mTitle;
    private SeekBar mSeekBar;
    private int mId;

    public SeekBarDialog(Context context) {
        super(context);
    }

    public SeekBarDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected SeekBarDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public SeekBarDialog(Context context,
                         int id, int max, int dValue,
                         OnSeekBarChangeListener seekBarChangeListener,
                         OnHorizontalScrollListener horizontalScrollListener) {
        super(context);
        mListener = seekBarChangeListener;
        mHorizontalListener = horizontalScrollListener;
        mMax = max;
        defaultValue = dValue;
        mId= id;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mView = LayoutInflater
                .from(getContext())
                .inflate(R.layout.seekbar_number_layout, new LinearLayout(getContext()), false);
        mSeekBar = ((SeekBar)mView.findViewById(R.id.seekbar_number));
        mSeekBar.setMax(mMax);
        mSeekBar.setOnSeekBarChangeListener(this);
        setContentView(mView);
        mSeekBar.setProgress(defaultValue);

        mView.findViewById(R.id.seek_bar_ok_button).setOnClickListener(this);
        mView.findViewById(R.id.seek_bar_no_button).setOnClickListener(this);
    }

    public void setValue(int value) {
        mSeekBar.setProgress(value);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mHorizontalListener.onUpdateValue(mId, progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.seek_bar_ok_button) {
            mListener.numberChanged(mId, mSeekBar.getProgress());
            dismiss();
        } else if (i == R.id.seek_bar_no_button) {
            cancel();
        }
    }

    public interface OnHorizontalScrollListener {
        public void onUpdateValue(int id, int value);
    }
}
