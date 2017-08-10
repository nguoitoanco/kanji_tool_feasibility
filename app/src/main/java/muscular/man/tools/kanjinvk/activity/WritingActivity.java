package muscular.man.tools.kanjinvk.activity;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.nvk.customview.ColorPickerDialog;
import com.nvk.customview.CustomNvkTextView;
import com.nvk.customview.DrawingView;
import com.nvk.customview.PathView;
import com.nvk.customview.SeekBarDialog;
import com.nvk.listener.OnColorChangedListener;
import com.nvk.listener.OnSeekBarChangeListener;

import muscular.man.tools.kanjinvk.R;
import muscular.man.tools.kanjinvk.common.CommonSharedPreferencesManager;
import muscular.man.tools.kanjinvk.model.constant.Constant;
import muscular.man.tools.kanjinvk.model.dto.KanjiDto;
import muscular.man.tools.kanjinvk.model.enums.SizeEnum;
import muscular.man.tools.kanjinvk.util.StringUtils;
import muscular.man.tools.kanjinvk.util.ViewUtils;
import muscular.man.tools.kanjinvk.view.BaseView;
import muscular.man.tools.kanjinvk.view.adapter.KanjiWritingTemplateAdapter;

public class WritingActivity extends AppCompatActivity
        implements BaseView, OnColorChangedListener, OnSeekBarChangeListener, View.OnClickListener
                , SeekBarDialog.OnHorizontalScrollListener {
    private static final String TAG = WritingActivity.class.getSimpleName();

    private static final int ANIMATION_PROCESS_VALUE_DEFAULT = 1;
    private static final int SIZE_PICK_PROCESS_VALUE_DEFAULT = 2;


    private CustomNvkTextView mKanjiStrokeNumberView;
    private PathView mPathView;
    private DrawingView mDrawingView;
    private SeekBarDialog mTimeDialog;
    private SeekBarDialog mPenSizeDialog;
    private RecyclerView mTemplateRecyclerView;
    private RecyclerView mPracticeRecyclerView;

    private KanjiDto mKanjiDto;
    private boolean isWriting = false;
    private int duration = (ANIMATION_PROCESS_VALUE_DEFAULT + 1) * 1000;
    private int pathWidth = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing);
        initView();
    }

    @Override
    public void initView() {
        setActionBar();
        setTitle(getString(R.string.kanji_writing_paractice_title));
        mKanjiDto = getIntent().getParcelableExtra("kanjiDto");
//        int currentPos = getIntent().getIntExtra("currentPos", 0);

        setKanjiWordInfos();


        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setLandCaseHelpLayout();
        } else {
            setPortraitLayout();
        }

        setDrawView();
        setFooterToolbar();
    }

    public void setActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.kanji_writing_toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_close_white_36dp);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setDrawView() {
        mDrawingView = (DrawingView) findViewById(R.id.writing_exercise_view);

        int selectedColor = CommonSharedPreferencesManager.loadIntPreference(
                getApplicationContext(), Constant.WRITING_PRACTICE_SELECTED_COLOR_KEY, Color.BLUE);
        mDrawingView.setCurrentColor(selectedColor);

        int selectedPenSizeProcess = CommonSharedPreferencesManager.loadIntPreference(
                getApplicationContext(), Constant.WRITING_PRACTICE_SIZE_OF_PEN_KEY, SIZE_PICK_PROCESS_VALUE_DEFAULT);
        mDrawingView.setStrokeSize(selectedPenSizeProcess + 10);
    }

    private void setFooterToolbar() {
        findViewById(R.id.pen_size).setOnClickListener(this);
        findViewById(R.id.clear_all).setOnClickListener(this);
        findViewById(R.id.color_picker).setOnClickListener(this);
        findViewById(R.id.undo_text_view).setOnClickListener(this);
        findViewById(R.id.redo_text_view).setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.writing_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.time_option:
                showTimeOption();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showTimeOption() {
        int selectedProcess = CommonSharedPreferencesManager.loadIntPreference(
                getApplicationContext(), Constant.WRITING_PRACTICE_ANIMATION_TIME_KEY, ANIMATION_PROCESS_VALUE_DEFAULT);
        mTimeDialog = new SeekBarDialog(
                this, Constant.WRITING_PRACTICE_TIMING_PICK_DIALOG_ID, 14, selectedProcess, this, this);
        mTimeDialog.setTitle("Pick a Time(Second):" + (selectedProcess + 1));
        mTimeDialog.show();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
//        if (mPathView != null) {
//            mPathView.clearAnimation();
//        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    private void createDialogColorPicker() {
        Paint mPaint;

        mPaint = new Paint();
        // on button click
        new ColorPickerDialog(this, this, mPaint.getColor()).show();
    }

    @Override
    public void colorChanged(int color) {
        mDrawingView.setCurrentColor(color);
        CommonSharedPreferencesManager.saveIntPreference(
                getApplicationContext(), Constant.WRITING_PRACTICE_SELECTED_COLOR_KEY, color);
    }

    @Override
    public void numberChanged(int id, int process) {
        switch (id) {
            case Constant.WRITING_PRACTICE_TIMING_PICK_DIALOG_ID:
                duration = 1000 * (process + 1);
                CommonSharedPreferencesManager.saveIntPreference(
                        getApplicationContext(), Constant.WRITING_PRACTICE_ANIMATION_TIME_KEY, process);
                break;
            case Constant.WRITING_PRACTICE_SIZE_PICK_DIALOG_ID:
                mDrawingView.setStrokeSize(10 + process);
                CommonSharedPreferencesManager.saveIntPreference(
                        getApplicationContext(), Constant.WRITING_PRACTICE_SIZE_OF_PEN_KEY, process);
                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pen_size:
                int selectedProcess = CommonSharedPreferencesManager.loadIntPreference(
                        getApplicationContext(), Constant.WRITING_PRACTICE_SIZE_OF_PEN_KEY, SIZE_PICK_PROCESS_VALUE_DEFAULT);
                mPenSizeDialog = new SeekBarDialog(
                        this, Constant.WRITING_PRACTICE_SIZE_PICK_DIALOG_ID, 10, selectedProcess, this, this);
                mPenSizeDialog.setTitle("Pick a size:" + (selectedProcess + 10));
                mPenSizeDialog.show();
                break;

            case R.id.color_picker:
                createDialogColorPicker();
                break;

            case R.id.clear_all:
                mDrawingView.clearAll();
                break;

            case R.id.undo_text_view:
                mDrawingView.undo();
                break;
            case R.id.redo_text_view:
                mDrawingView.redo();
                break;
            case R.id.path_view:
                if (isWriting) {
                    mPathView.clearAnimation();
                    isWriting = false;
                } else {
                    mPathView.getSequentialPathAnimator().delay(500).duration(duration).interpolator(null).start();
                    isWriting = true;
                }
                break;
        }
    }

//    @Override
//    public void onSelectItem(int itemIndex) {
//        Log.d(TAG, "" + itemIndex);
//        setKanjiWordInfos(itemIndex);
//        ViewUtils.setSquareSizeOnDevice(mKanjiStrokeNumberView, getApplicationContext(), SizeEnum.RATE_TWO.rate);
//        ViewUtils.setSquareSizeOnDevice(mPathView, getApplicationContext(), SizeEnum.RATE_TWO.rate);
//    }

    private void setKanjiWordInfos() {
        mPathView = (PathView) findViewById(R.id.path_view);

        mKanjiStrokeNumberView = (CustomNvkTextView) findViewById(R.id.kanji_stroke_number_view);
        mKanjiStrokeNumberView.setText(mKanjiDto.word.trim());
        int id = getResources().getIdentifier(mKanjiDto.kid.toLowerCase(), "raw", getPackageName());
        Log.d(TAG, mKanjiDto.kid.toLowerCase());
        mPathView.setSvgResource(id);

        mPathView.setInit(pathWidth, false, false, false);
        mPathView.setPathColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        mPathView.setOnClickListener(this);
    }

    @Override
    public void onUpdateValue(int id, int process) {
        switch (id) {
            case Constant.WRITING_PRACTICE_SIZE_PICK_DIALOG_ID:
                mPenSizeDialog.setTitle("Pick a size:" + (10 + process));
                break;
            case Constant.WRITING_PRACTICE_TIMING_PICK_DIALOG_ID:
                mTimeDialog.setTitle("Pick a time(second):" + (1 + process));
                break;
        }
    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        setContentView(R.layout.activity_writing);
//
//        setKanjiWordInfos();
//
//        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            setLandCaseHelpLayout();
//        } else {
//            setPortraitLayout();
//        }
//
//        setDrawView();
//        setFooterToolbar();
//
//    }

    private void setPortraitLayout() {
        ViewUtils.setSquareSizeOnWidthDevice(
                findViewById(R.id.kanji_stroke_number_card_view), getApplicationContext(), SizeEnum.RATE_TWO.rate, -16);
        ViewUtils.setSquareSizeOnWidthDevice(
                findViewById(R.id.kanji_path_card_view), getApplicationContext(), SizeEnum.RATE_TWO.rate, -16);

        TextView templateView = (TextView) findViewById(R.id.kanji_template_view);
        TextView practiceView = (TextView) findViewById(R.id.kanji_practice_view);
        ViewUtils.setSquareSizeOnWidthDevice(findViewById(R.id.kanji_writing_template_card_view),
                getApplicationContext(), SizeEnum.RATE_TWO.rate, -16);
        ViewUtils.setSquareSizeOnWidthDevice(findViewById(R.id.kanji_writing_practice_card_view),
                getApplicationContext(), SizeEnum.RATE_TWO.rate, -16);

        templateView.setText(mKanjiDto.word.trim());
        practiceView.setText(StringUtils.EMPTY);
    }

    private void setLandCaseHelpLayout() {
        mTemplateRecyclerView = (RecyclerView) findViewById(R.id.kanji_writing_template_recycler_view);
        mTemplateRecyclerView.setHasFixedSize(true);

        mPracticeRecyclerView = (RecyclerView) findViewById(R.id.kanji_writing_practice_recycler_view);
        mPracticeRecyclerView.setHasFixedSize(true);

        ViewUtils.setSquareSizeOnWidthDevice(findViewById(
                R.id.kanji_stroke_number_card_view), getApplicationContext(), SizeEnum.RATE_FIVE.rate, -40);
        ViewUtils.setSquareSizeOnWidthDevice(
                findViewById(R.id.kanji_path_card_view), getApplicationContext(), SizeEnum.RATE_FIVE.rate, -40);
        ViewUtils.setHeightByWidth(findViewById(
                R.id.kanji_writing_template_recycler_view), getApplicationContext(), SizeEnum.RATE_FIVE.rate, 0);
        ViewUtils.setHeightByWidth(findViewById(
                R.id.kanji_writing_practice_recycler_view), getApplicationContext(), SizeEnum.RATE_FIVE.rate, 0);

        setWritingTemplateAdapter();
    }

    private void setWritingTemplateAdapter() {
        LinearLayoutManager templateManager = new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager practiceManager = new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.HORIZONTAL, false);
        mTemplateRecyclerView.setLayoutManager(templateManager);
        mPracticeRecyclerView.setLayoutManager(practiceManager);

        KanjiWritingTemplateAdapter templateAdapter = new KanjiWritingTemplateAdapter(mKanjiDto, this, true);
        templateAdapter.notifyDataSetChanged();
        mTemplateRecyclerView.setAdapter(templateAdapter);

        KanjiWritingTemplateAdapter practiceAdapter = new KanjiWritingTemplateAdapter(mKanjiDto, this, false);
        practiceAdapter.notifyDataSetChanged();
        mPracticeRecyclerView.setAdapter(practiceAdapter);
    }
}
