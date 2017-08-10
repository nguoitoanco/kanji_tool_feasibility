package muscular.man.tools.kanjinvk.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import muscular.man.tools.kanjinvk.R;
import muscular.man.tools.kanjinvk.common.CommonSharedPreferencesManager;

/**
 * Created by KhanhNV10 on 30/03/2016.
 */
public class CustomTestOptionView extends Dialog
        implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    private static final String JLPT_TYPE_KEY = "CustomTestOptionView.jlptType";
    private static final String JLPT_AMOUNT_KEY = "CustomTestOptionView.amount";

    private OnCustomTestOptionListener mListener;

    private View mView;
    private SeekBar mSeekBar;
    private RadioButton[] customTestRadioButtons;
    private TextView amountTextView;

    private int mMax;
    private String mTitle;
    private String amountText;

    private int mAmount = 10;
    private int jlptType = 5;

    public CustomTestOptionView(Context context) {
        super(context);
    }

    public CustomTestOptionView(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected CustomTestOptionView(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public CustomTestOptionView(Context context,
                                OnCustomTestOptionListener listener,
                                String title, int max) {
        super(context);

        mListener = listener;
        mMax = max;
        mTitle = title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = LayoutInflater
                .from(getContext())
                .inflate(R.layout.custom_test_option_layout, new LinearLayout(getContext()), false);
        setContentView(mView);
        setTitle(mTitle);

        amountTextView = (TextView) mView.findViewById(R.id.amount_of_test_number_view);
        amountText = getContext().getResources().getString(R.string.test_amount);

        mAmount = CommonSharedPreferencesManager.loadIntPreference(getContext(), JLPT_AMOUNT_KEY, 5);
        amountTextView.setText(getAmountText());

        mSeekBar = ((SeekBar) mView.findViewById(R.id.amount_of_test_number));
        mSeekBar.setMax(mMax);
        mSeekBar.setProgress(mAmount/5 - 1);
        mSeekBar.setOnSeekBarChangeListener(this);

        customTestRadioButtons = new RadioButton[] {
                (RadioButton) mView.findViewById(R.id.custom_test_jlpt_n5)
                ,(RadioButton) mView.findViewById(R.id.custom_test_jlpt_n4)
                ,(RadioButton) mView.findViewById(R.id.custom_test_jlpt_n3)
                ,(RadioButton) mView.findViewById(R.id.custom_test_jlpt_n2)
                ,(RadioButton) mView.findViewById(R.id.custom_test_jlpt_n1)
        };

        mView.findViewById(R.id.custom_test_ok_button).setOnClickListener(this);
        mView.findViewById(R.id.custom_test_no_button).setOnClickListener(this);

        jlptType = CommonSharedPreferencesManager.loadIntPreference(
                getContext(), "CustomTestOptionView.jlptType", 5);

        for (int i = 0; i < customTestRadioButtons.length; i++) {
            customTestRadioButtons[i].setOnClickListener(this);
            customTestRadioButtons[i].setChecked(i == getIndexChecked());
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mAmount = (mSeekBar.getProgress() + 1) * 5;
        amountTextView.setText(getAmountText());
    }

    private String getAmountText() {
        return amountText + "" + mAmount;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void setBackGround() {

    }

    private int getIndexChecked() {
        int indexChecked = 0;
        switch (jlptType) {
            case 5:
                indexChecked = 0;
                break;
            case 4:
                indexChecked = 1;
                break;
            case 3:
                indexChecked = 2;
                break;
            case 2:
                indexChecked = 3;
                break;
            case 1:
                indexChecked = 4;
                break;
        }

        return indexChecked;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.custom_test_ok_button:
                mListener.confirmInfo(jlptType, mAmount, true);
                CommonSharedPreferencesManager.saveIntPreference(
                        getContext(), JLPT_TYPE_KEY, jlptType);
                CommonSharedPreferencesManager.saveIntPreference(
                        getContext(), JLPT_AMOUNT_KEY, mAmount);
                dismiss();
                break;
            case R.id.custom_test_no_button:
                cancel();
                break;
            case R.id.custom_test_jlpt_n5:
                jlptType = 5;
                break;
            case R.id.custom_test_jlpt_n4:
                jlptType = 4;
                break;
            case R.id.custom_test_jlpt_n3:
                jlptType = 3;
                break;
            case R.id.custom_test_jlpt_n2:
                jlptType = 2;
                break;
            case R.id.custom_test_jlpt_n1:
                jlptType = 1;
                break;
        }
    }

    public interface OnCustomTestOptionListener {
        public void confirmInfo(int jlpt, int amount, boolean isRandom);
    }
}
