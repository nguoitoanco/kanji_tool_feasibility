package muscular.man.tools.kanjinvk.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.startapp.android.publish.StartAppAd;
import com.startapp.android.publish.StartAppSDK;
import com.startapp.android.publish.splash.SplashConfig;

import java.util.ArrayList;
import java.util.List;

import muscular.man.tools.kanjinvk.R;
import muscular.man.tools.kanjinvk.common.CommonActionListener;
import muscular.man.tools.kanjinvk.common.CommonSharedPreferencesManager;
import muscular.man.tools.kanjinvk.model.constant.Constant;
import muscular.man.tools.kanjinvk.model.dto.KanjiDto;
import muscular.man.tools.kanjinvk.model.dto.KanjiTestDto;
import muscular.man.tools.kanjinvk.model.enums.Categories;
import muscular.man.tools.kanjinvk.model.storage.dao.KanjiDao;
import muscular.man.tools.kanjinvk.model.storage.dao.KanjiTestDao;
import muscular.man.tools.kanjinvk.task.GetDataListFromFileTask;
import muscular.man.tools.kanjinvk.task.GetKanjiTestListFromFileTask;
import muscular.man.tools.kanjinvk.util.ReminderService;

public class SplashActivity extends Activity {

    // Splash screen timer
    private boolean isKanjiPrepareFinish = false;
    private boolean isKanjiTestPrepareFinish = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StartAppSDK.init(this, "206735566", true);

        final boolean isFirstRun = CommonSharedPreferencesManager
                .loadBooleanPreference(getApplicationContext(), Constant.IS_FIRST_RUN, true);
        final boolean isDataError = CommonSharedPreferencesManager
                .loadBooleanPreference(getApplicationContext(), Constant.IS_DATA_ERROR, false);
        final boolean isDataReset = CommonSharedPreferencesManager
                .loadBooleanPreference(getApplicationContext(), Constant.IS_DATA_RESET, false);

        setContentView(R.layout.activity_splash);
//        MobileAds.initialize(getApplicationContext(), "ca-app-pub-9948683300202690/3957102768");
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-9948683300202690/4875432762");
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doMainTask(isFirstRun, isDataError, isDataReset);
            }
        }, 2000);
    }

    private void doMainTask(boolean isFirstRun, boolean isDataError, boolean isDataReset) {
        if (isFirstRun || isDataError || isDataReset) {
            findViewById(R.id.loading_status_layout).setVisibility(View.VISIBLE);
            preparingData();
        } else {
            boolean haveAlreadyReminder = CommonSharedPreferencesManager
                    .loadBooleanPreference(getApplicationContext(), Constant.HAVE_ALREADY_REMINDER_KEY, false);
            if (!haveAlreadyReminder) {
                ReminderService reminderService = new ReminderService();
                reminderService.createNewReminder(getApplicationContext());
                CommonSharedPreferencesManager.saveBooleanPreference(
                        getApplicationContext(), Constant.HAVE_ALREADY_REMINDER_KEY, true);
            }
            gotoMainActivity();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    private void preparingData() {

        // Preparing kanji data list
        prepareKanjiList();

        // Preparing kanji test data list
        prepareKanjiTestList();

    }

    private void prepareKanjiList() {
        new GetDataListFromFileTask(Categories.ALL.dataUrl, getApplicationContext(), new CommonActionListener.CallBackAfterFinishTaskListener<List<KanjiDto>>() {
            @Override
            public boolean onSuccess(List<KanjiDto> kanjiDtos) {
                List<KanjiDto> kanjDtos = new ArrayList<>(kanjiDtos);
                KanjiDao kanjiDao = new KanjiDao(getApplicationContext());
                kanjiDao.insertKanjiList(kanjDtos, new CommonActionListener.CallBackListener<Boolean>() {
                    @Override
                    public boolean onSuccess(Boolean isSuccess) {
                        isKanjiPrepareFinish = true;
                        onFinishPreparingData();

                        return false;
                    }
                });
                return true;
            }

            @Override
            public boolean onError(String message) {
                CommonSharedPreferencesManager.saveBooleanPreference(getApplicationContext(), Constant.IS_DATA_ERROR, true);
                gotoMainActivity();
                return false;
            }
        }).execute();
    }


    private void prepareKanjiTestList() {
        new GetKanjiTestListFromFileTask(Categories.TEST.dataUrl, getApplicationContext(), new CommonActionListener.CallBackAfterFinishTaskListener<List<KanjiTestDto>>() {
            @Override
            public boolean onSuccess(List<KanjiTestDto> kanjiTestDtos) {
                List<KanjiTestDto> kanjDtos = new ArrayList<>(kanjiTestDtos);
                KanjiTestDao kanjiTestDao = new KanjiTestDao(getApplicationContext());
                kanjiTestDao.insertKanjiList(kanjDtos, new CommonActionListener.CallBackListener<Boolean>() {
                    @Override
                    public boolean onSuccess(Boolean isSuccess) {
                        isKanjiTestPrepareFinish = true;
                        onFinishPreparingData();

                        return false;
                    }
                });
                return true;
            }

            @Override
            public boolean onError(String message) {
                CommonSharedPreferencesManager.saveBooleanPreference(getApplicationContext(), Constant.IS_DATA_ERROR, true);
                gotoMainActivity();
                return false;
            }
        }).execute();
    }

    private void onFinishPreparingData() {
        CommonSharedPreferencesManager.saveBooleanPreference(getApplicationContext(), Constant.IS_DATA_ERROR, false);
        CommonSharedPreferencesManager.saveBooleanPreference(getApplicationContext(), Constant.IS_FIRST_RUN, false);
        CommonSharedPreferencesManager.saveBooleanPreference(getApplicationContext(), Constant.IS_DATA_RESET, false);

        if (isKanjiPrepareFinish && isKanjiTestPrepareFinish) {
            gotoMainActivity();
        }
    }

    private void gotoMainActivity() {
        Intent i = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
