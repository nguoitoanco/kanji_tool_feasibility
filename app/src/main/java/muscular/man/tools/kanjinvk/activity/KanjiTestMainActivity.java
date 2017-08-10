package muscular.man.tools.kanjinvk.activity;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.startapp.android.publish.StartAppAd;

import java.util.ArrayList;
import java.util.List;

import muscular.man.tools.kanjinvk.R;
import muscular.man.tools.kanjinvk.common.CommonActionListener.CallBackListener;
import muscular.man.tools.kanjinvk.common.CommonSharedPreferencesManager;
import muscular.man.tools.kanjinvk.model.dto.KanjiTestDto;
import muscular.man.tools.kanjinvk.model.storage.dao.KanjiTestDao;
import muscular.man.tools.kanjinvk.view.BaseActivity;
import muscular.man.tools.kanjinvk.view.adapter.KanjiTestMainAdapter;
import muscular.man.tools.kanjinvk.view.adapter.OptionItemListAdapter;

public class KanjiTestMainActivity extends BaseActivity
        implements CallBackListener<List<KanjiTestDto>>, OptionItemListAdapter.OnOptionItemOnClickListener
                ,View.OnClickListener
{

    private static final String TAG = KanjiTestMainActivity.class.getSimpleName();

    private OptionItemListAdapter mOptionItemListAdapter;
    private KanjiTestMainAdapter mAdapter;

    private RecyclerView mRecyclerView;
    private RecyclerView mOptionItemRecycler;
    private Button mCommitButton;

    private List<KanjiTestDto> mKanjiTestDtoList;
    private int numberSelected = 0;
    private int mAmount = 10;
    private boolean isReset = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kanji_test_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.kanji_main_test_toolbar);
        setSupportActionBar(toolbar);

        setTitle(getResources().getString(R.string.kanji_test_text));
        initView();
    }

    @Override
    public void initView() {
        mCommitButton = (Button) findViewById(R.id.kanji_test_main_commit_button);
        mCommitButton.setOnClickListener(this);

        mRecyclerView = (RecyclerView)findViewById(R.id.kanji_test_main_recycler_view);
        LinearLayoutManager lineLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(lineLayoutManager);

        mOptionItemRecycler = (RecyclerView) findViewById(R.id.option_item_recycler_view);
        mOptionItemRecycler.setHasFixedSize(true);

        LinearLayoutManager horizontalManager = new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL, false);
        mOptionItemRecycler.setLayoutManager(horizontalManager);
        boolean optionItemIsDisplay = CommonSharedPreferencesManager.loadBooleanPreference(
                getApplicationContext(), "optionItemIsDisplay", true);
        if (optionItemIsDisplay) {
            mOptionItemRecycler.setVisibility(View.VISIBLE);
        } else {
            mOptionItemRecycler.setVisibility(View.GONE);
        }

        List<KanjiTestDto> kanjiTestDtos = getIntent().getParcelableArrayListExtra("kanjiTestDtos");
        boolean isCustomTest = getIntent().getBooleanExtra("isCustomTest", false);

        mKanjiTestDtoList = new ArrayList<>();
        setAdapter();

        KanjiTestDao kanjiTestDao = new KanjiTestDao(getApplicationContext());
        mAmount = getIntent().getIntExtra("amount", 10);
        if (!isCustomTest) {
            kanjiTestDao.getKanjiTestDetailList(kanjiTestDtos, this);
        } else {
            int jlptType = getIntent().getIntExtra("jlptType", 0);
            kanjiTestDao.getCustomKanjiTestDetailList(mAmount, getSearchKey(jlptType), true, this);
        }
    }

    private String getSearchKey(int type) {
        return ("TES" + type + "%");
    }

    @Override
    public boolean onSuccess(List<KanjiTestDto> dtos) {
        findViewById(R.id.loading_panel).setVisibility(View.GONE);
        mKanjiTestDtoList = dtos;
        setAdapter();
        setOptionItemListAdapter();
        return false;
    }

    private void setAdapter() {
        mAdapter = new KanjiTestMainAdapter(mKanjiTestDtoList, this, isReset, new CallBackListener<Integer>() {
            @Override
            public boolean onSuccess(Integer pos) {
                setOptionItemListAdapter();
                numberSelected++;
                Log.d(TAG, "numberSelected:" + numberSelected);
                if (numberSelected == mAmount) {
                    mCommitButton.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });
        mAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mAdapter);
    }

    private void setOptionItemListAdapter() {
        mOptionItemListAdapter = new OptionItemListAdapter(mKanjiTestDtoList, this, this);
        mOptionItemListAdapter.notifyDataSetChanged();
        mOptionItemRecycler.setAdapter(mOptionItemListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_kanji_test_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.kanji_main_test_display_item_list_action:
                if (mOptionItemRecycler.getVisibility() == View.GONE) {
                    mOptionItemRecycler.setVisibility(View.VISIBLE);
                    CommonSharedPreferencesManager.saveBooleanPreference(
                            getApplicationContext(), "optionItemIsDisplay", true);
                } else {
                    mOptionItemRecycler.setVisibility(View.GONE);
                    CommonSharedPreferencesManager.saveBooleanPreference(
                            getApplicationContext(), "optionItemIsDisplay", false);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onOptionItemClick(int itemIndex) {
        mRecyclerView.smoothScrollToPosition(itemIndex);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.kanji_test_main_commit_button:
                displayTestResult();
                break;
        }
    }

    private void displayTestResult() {
        int adShow = CommonSharedPreferencesManager
                .loadIntPreference(getApplicationContext(), "adShow", 1);
        if (adShow % 5 == 0) {
            StartAppAd.showAd(this);
            adShow = 1;
        }

        adShow++;
        CommonSharedPreferencesManager
                .saveIntPreference(getApplicationContext(), "adShow", adShow);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.kanji_test_result_layout, null);
        dialogBuilder.setView(dialogView);

        TextView amountQuestions = (TextView) dialogView.findViewById(R.id.amount_of_question_view);
        String amountText = getResources().getString(R.string.kanji_test_result_question) + mAmount;
        amountQuestions.setText(amountText);

        TextView correctedAwsView = (TextView) dialogView.findViewById(R.id.amount_correct_answer_view);
        int correctedAws = 0;
        for (KanjiTestDto dto : mKanjiTestDtoList) {
            if (dto.selectedItem == Integer.parseInt(dto.aws.trim())) {
                correctedAws++;
            }
        }
        String correctedAwsText = getResources().getString(R.string.kanji_test_corrected_answer) + correctedAws;
        correctedAwsView.setText(correctedAwsText);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setIcon(R.mipmap.ic_launcher);
        alertDialog.setTitle("Test Result");
        dialogView.findViewById(R.id.preview_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isReset = false;
                setAdapter();
                mCommitButton.setVisibility(View.GONE);
                alertDialog.dismiss();
            }
        });

        dialogView.findViewById(R.id.try_again_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isReset = true;
                numberSelected = 0;
                for (KanjiTestDto dto : mKanjiTestDtoList) {
                    dto.selectedItem = -1;
                }
                setAdapter();
                setOptionItemListAdapter();
                mCommitButton.setVisibility(View.GONE);
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }
}
