package muscular.man.tools.kanjinvk.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import muscular.man.tools.kanjinvk.R;
import muscular.man.tools.kanjinvk.common.CommonSharedPreferencesManager;
import muscular.man.tools.kanjinvk.model.dto.KanjiDto;
import muscular.man.tools.kanjinvk.model.enums.ViewDetailMode;
import muscular.man.tools.kanjinvk.view.adapter.CompoundListAdapter;

import static muscular.man.tools.kanjinvk.common.CommonActionListener.CallBackListener;

/**
 * A simple {@link BaseFragment} subclass.
 */
public class KanjiDetailFragment extends BaseFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private RecyclerView compoundRecycleView;
    private TextView mOnTextView;
    private TextView mKunTextView;
    private TextView mMeanTextView;
    private TextView mWordTextView;
    private ViewGroup mContainer;
    private LayoutInflater mInflate;
//    private TextView mCompoundTextView;

    private static CallBackListener mCallBack;
    private BroadcastReceiver mReceiver;
    private BroadcastReceiver mChangeLanguageReceiver;

    private int mViewModeType = 0;
    private KanjiDto dto;

    public static KanjiDetailFragment newInstance(CallBackListener callBack) {
        mCallBack = callBack;
        return new KanjiDetailFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_kanji_detail, container, false);
        mContainer = container;
        mInflate = inflater;
        mViewModeType = CommonSharedPreferencesManager.loadIntPreference(getContext(), "viewModeType", 0);
        initView();

        // Register broadcast Change View mode
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mViewModeType = intent.getIntExtra("viewModeType", 0);
                setDisplayItems();
            }
        };

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(
                mReceiver, new IntentFilter("ChangeViewMode"));

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        PreferenceManager
                .getDefaultSharedPreferences(getContext())
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void initView() {
        dto = getArguments().getParcelable("kanjiDto");
        mOnTextView = (TextView) mView.findViewById(R.id.kanji_on_text_view);
        mKunTextView = (TextView) mView.findViewById(R.id.kanji_kun_text_view);
        mMeanTextView = (TextView) mView.findViewById(R.id.kanji_mean_text_view);
        mWordTextView = (TextView) mView.findViewById(R.id.kanji_word_text_view);

        compoundRecycleView = (RecyclerView) mView.findViewById(R.id.compound_recycler_view);
        LinearLayoutManager lineLayoutManager = new LinearLayoutManager(getContext());
        compoundRecycleView.setLayoutManager(lineLayoutManager);

        if (dto != null) {
            mOnTextView.setText(dto.onyomi);
            mKunTextView.setText(dto.kuniomi);

            // In case basic header
            if (dto.kid.contains("B")) {
                mMeanTextView.setText(dto.vnMean);
                String charBr = getString(R.string.ch_br);
                setCompoundsAdapter(charBr + " " + dto.vnCompound);
            } else {
                boolean isEnglish = CommonSharedPreferencesManager.loadBooleanPreference(
                        getContext(), "ContentIsEnglish", true);

                if (!isEnglish) {
                    mMeanTextView.setText(dto.vnMean);
                    setCompoundsAdapter(dto.vnCompound);
                } else {
                    mMeanTextView.setText(dto.enMean);
                    setCompoundsAdapter(dto.enCompound);
                }
            }



            mWordTextView.setText(dto.word);
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeCap(Paint.Cap.ROUND);
            mWordTextView.setLayerType(View.LAYER_TYPE_SOFTWARE, paint);

            mWordTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mViewModeType == ViewDetailMode.DEFAULT.toInt()) {
                        mViewModeType = ViewDetailMode.CLEAR.toInt();
                    } else if (mViewModeType == ViewDetailMode.CLEAR.toInt()) {
                        mViewModeType = ViewDetailMode.DEFAULT.toInt();
                    }
                    setDisplayItems();
                    mCallBack.onSuccess(v);
                }
            });

            setDisplayItems();
        }
    }

    private void setCompoundsAdapter(String strCompounds) {
        String[] compounds = strCompounds.trim().split("<br/>");
        CompoundListAdapter compoundAdapter = new CompoundListAdapter(compounds);
        compoundAdapter.notifyDataSetChanged();
        compoundRecycleView.setAdapter(compoundAdapter);
    }

    private void setDisplayItems() {
        if (mViewModeType == ViewDetailMode.DEFAULT.toInt()) {
            mOnTextView.setVisibility(View.VISIBLE);
            mView.findViewById(R.id.kanji_on_title_text_view).setVisibility(View.VISIBLE);
            mKunTextView.setVisibility(View.VISIBLE);
            mView.findViewById(R.id.kanji_kun_title_text_view).setVisibility(View.VISIBLE);
            mMeanTextView.setVisibility(View.VISIBLE);
            mView.findViewById(R.id.kanji_mean_title_text_view).setVisibility(View.VISIBLE);
            compoundRecycleView.setVisibility(View.VISIBLE);
            mView.findViewById(R.id.kanji_compound_title_text_view).setVisibility(View.VISIBLE);
        } else if (mViewModeType == ViewDetailMode.CLEAR.toInt()){
            mOnTextView.setVisibility(View.GONE);
            mView.findViewById(R.id.kanji_on_title_text_view).setVisibility(View.GONE);
            mKunTextView.setVisibility(View.GONE);
            mView.findViewById(R.id.kanji_kun_title_text_view).setVisibility(View.GONE);
            mMeanTextView.setVisibility(View.GONE);
            mView.findViewById(R.id.kanji_mean_title_text_view).setVisibility(View.GONE);
            compoundRecycleView.setVisibility(View.GONE);
            mView.findViewById(R.id.kanji_compound_title_text_view).setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mReceiver);
        PreferenceManager
                .getDefaultSharedPreferences(getContext())
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("ContentIsEnglish")) {
            boolean isEnglish = sharedPreferences.getBoolean("ContentIsEnglish", true);
            if (isEnglish) {
                mMeanTextView.setText(dto.enMean);
                setCompoundsAdapter(dto.enCompound);
            } else {
                mMeanTextView.setText(dto.vnMean);
                setCompoundsAdapter(dto.vnCompound);
            }
        }
    }
}
