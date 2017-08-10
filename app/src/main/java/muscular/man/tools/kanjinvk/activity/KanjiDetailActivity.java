package muscular.man.tools.kanjinvk.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.nvk.customview.SeekBarDialog;
import com.nvk.listener.OnSeekBarChangeListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import muscular.man.tools.kanjinvk.BuildConfig;
import muscular.man.tools.kanjinvk.R;
import muscular.man.tools.kanjinvk.common.CommonSharedPreferencesManager;
import muscular.man.tools.kanjinvk.model.constant.Constant;
import muscular.man.tools.kanjinvk.model.dto.KanjiDto;
import muscular.man.tools.kanjinvk.model.enums.ViewDetailMode;
import muscular.man.tools.kanjinvk.model.storage.dao.KanjiDao;
import muscular.man.tools.kanjinvk.util.KanjiUtils;
import muscular.man.tools.kanjinvk.util.StringUtils;
import muscular.man.tools.kanjinvk.view.adapter.KanjiDetailPagerAdapter;

import static muscular.man.tools.kanjinvk.common.CommonActionListener.CallBackListener;

public class KanjiDetailActivity extends AppCompatActivity
        implements CallBackListener<List<KanjiDto>>, OnSeekBarChangeListener, View.OnClickListener,
        SeekBarDialog.OnHorizontalScrollListener {

    private static final String TAG = KanjiDetailActivity.class.getSimpleName();
    private static final int SLIDE_SHOW_PROCESS_DEFAULT = 1;

    private SeekBarDialog mSlideShowTimeDialog;
    private KanjiDetailPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;
    private ImageView mPreviousView;
    private ImageView mNextView;
    private TextView mChangeLanguageView;


    private View mHistoryMenu;
    private View mPracticeMenu;

    private View mModeMenu;
    private View mShareMenu;
    private View mSettingMenu;
    private Menu mMenu;

    private PopupMenu mSettingPopupMenu;
    private PopupMenu mModePopupMenu;

    private List<KanjiDto> mKanjiDtoList;
    private List<String> mKanjiIdList;

    private int mCurrentPos = 0;
    private int mViewModeType = 0;

    private boolean isRandom = false;
    private boolean isSliding = false;
    private int delayTime = (SLIDE_SHOW_PROCESS_DEFAULT + 1) * 1000;

    private Handler mHandler;
    private Runnable mRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
//        TwitterAuthConfig authConfig =  new TwitterAuthConfig("consumerKey", "consumerSecret");
//        Fabric.with(this, new TwitterCore(authConfig), new TweetComposer());

        setContentView(R.layout.activity_kanji_detail);

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-9948683300202690~8585778764");
        AdView mAdView = (AdView) findViewById(R.id.detail_ad_view);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        setActionBar();
        setTitle(StringUtils.EMPTY);

        mCurrentPos = getIntent().getIntExtra("currentPos", 0);
        mKanjiIdList = getIntent().getStringArrayListExtra("kanjiIds");

        mKanjiDtoList = new ArrayList<>();
        KanjiDao kanjiDao = new KanjiDao(getApplicationContext());
        kanjiDao.getKanjiDetailList(mKanjiIdList, this);
    }

    public void setActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.kanji_detail_toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_close_white_36dp);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setDisplayItems(int currentPos) {
        if (mViewModeType == ViewDetailMode.DEFAULT.toInt()) {
            mPreviousView.setVisibility(View.VISIBLE);
            mNextView.setVisibility(View.VISIBLE);
            findViewById(R.id.kanji_detail_appbar).setVisibility(View.VISIBLE);
            if (!mKanjiIdList.get(0).contains("B")) {
                mChangeLanguageView.setVisibility(View.VISIBLE);
                setChangeLanguageText();
            }
        } else if (mViewModeType == ViewDetailMode.CLEAR.toInt()) {
            mPreviousView.setVisibility(View.GONE);
            mNextView.setVisibility(View.GONE);
            findViewById(R.id.kanji_detail_appbar).setVisibility(View.INVISIBLE);
            mChangeLanguageView.setVisibility(View.GONE);
        }

        if (mKanjiDtoList.size() == 1) {
            mPreviousView.setVisibility(View.GONE);
            mNextView.setVisibility(View.GONE);
        } else if (currentPos == 0) {
            mPreviousView.setVisibility(View.GONE);
        }

        if (isSliding) {
            mMenu.findItem(R.id.action_bookmark).setVisible(false);
            mMenu.findItem(R.id.action_setting).setVisible(false);
            mMenu.findItem(R.id.action_share).setVisible(false);
            mMenu.findItem(R.id.action_practice).setVisible(false);
            mMenu.findItem(R.id.action_mode).setVisible(true);
        } else if (mMenu != null) {
            mMenu.findItem(R.id.action_bookmark).setVisible(true);
            mMenu.findItem(R.id.action_setting).setVisible(true);
            mMenu.findItem(R.id.action_share).setVisible(true);
            mMenu.findItem(R.id.action_practice).setVisible(true);
            if (mKanjiIdList.size() > 2) {
                mMenu.findItem(R.id.action_mode).setVisible(true);
            } else {
                mMenu.findItem(R.id.action_mode).setVisible(false);
            }
        }
    }

    private void registerActionListener() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mViewPager.setCurrentItem(position);
                setDisplayItems(position);
                mCurrentPos = position;
                updateBookmarkIcon();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (mKanjiDtoList.size() > 1) {
            mPreviousView.setOnClickListener(this);
            mNextView.setOnClickListener(this);
        }

        mChangeLanguageView.setOnClickListener(this);
    }

    private void showModeOption() {
        if (isSliding) {
            stopSlideShow();
        } else {
           int selectedProcess = CommonSharedPreferencesManager.loadIntPreference(
                    getApplicationContext(), Constant.KANJI_DETAIL_SLIDE_SHOW_TIME_KEY, SLIDE_SHOW_PROCESS_DEFAULT);
            mSlideShowTimeDialog = new SeekBarDialog(this,
                    Constant.KANJI_DETAIL_SLIDE_SHOW_TIME_DIALOG_ID, 9, selectedProcess, this, this);
            mSlideShowTimeDialog.setTitle("Pick a time(second):" + (selectedProcess + 1));
            mSlideShowTimeDialog.show();
        }

    }

    private void showSettings() {
        if (mSettingPopupMenu == null) {
            mSettingPopupMenu = new PopupMenu(KanjiDetailActivity.this, mSettingMenu);
            mSettingPopupMenu.getMenuInflater().inflate(
                    R.menu.view_detail_setting_popup_menu, mSettingPopupMenu.getMenu());
            mSettingPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (!item.isChecked()) {
                        item.setChecked(true);
                        isRandom = (item.getItemId() == R.id.detail_random_setting_action);
                        if (isRandom) {
                            Collections.shuffle(mKanjiDtoList);
                        } else {
                            Collections.sort(mKanjiDtoList, new Comparator<KanjiDto>() {
                                @Override
                                public int compare(KanjiDto lhs, KanjiDto rhs) {
                                    return lhs.kid.compareTo(rhs.kid);
                                }
                            });
                        }

                        Log.d(TAG, "" + item.isChecked());
                    }
                    return false;
                }
            });
        }
        setCheckRandom();
        mSettingPopupMenu.show();
    }

    private void setCheckRandom() {
        if (isRandom) {
            mSettingPopupMenu.getMenu().findItem(R.id.detail_random_setting_action).setChecked(true);
        } else {
            mSettingPopupMenu.getMenu().findItem(R.id.detail_default_setting_action).setChecked(true);
        }
    }
    private void startSlideShowMode() {
        isSliding = true;
        setDisplayItems(mCurrentPos);
        mMenu.findItem(R.id.action_mode).setIcon(android.R.drawable.ic_lock_power_off);
        mHandler = new Handler();
        if (mRunnable == null) {
            mRunnable = new Runnable() {
                @Override
                public void run() {
                    mViewPager.setCurrentItem(mCurrentPos++, true);
                    mHandler.postDelayed(mRunnable, delayTime);
                    if (mCurrentPos == mKanjiDtoList.size() - 1) {
                        if (isRandom) {
                            Collections.shuffle(mKanjiDtoList);
                        }
                        mCurrentPos = 0;
                    } else {
                        mCurrentPos++;
                    }
                }
            };
        }
//        mBackgroundThread = new Thread(mRunnable);
//        mBackgroundThread.start();
        mRunnable.run();
    }

    private void stopSlideShow() {
        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
            isSliding = false;
//            mModeView.setText("Mode");
            setDisplayItems(mCurrentPos);
            mMenu.findItem(R.id.action_mode).setIcon(R.mipmap.ic_slideshow_white_36dp);
            mMenu.findItem(R.id.action_bookmark).setVisible(true);
            mMenu.findItem(R.id.action_setting).setVisible(true);
            mMenu.findItem(R.id.action_share).setVisible(true);
//            mMenu.findItem(R.id.action_history).setVisible(true);
            mMenu.findItem(R.id.action_practice).setVisible(true);
        }
    }

    private void resumeSlideShow() {
//        if (mHandler != null) {
//            mHandler.postDelayed(mRunnable, 1000);
//        }
    }


    private void showHistory(KanjiDto dto) {
//        FragmentTransaction ft= getSupportFragmentManager().beginTransaction();
//        KanjiHistoryFragment khf = new KanjiHistoryFragment();
//        ft.add(R.id.kanji_detail_container, khf).commit();
//        Toolbar toolbar = (Toolbar) findViewById(R.id.kanji_detail_toolbar);
//        toolbar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAppleGreen));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.kanji_detail_menu, menu);
        mMenu = menu;
        if (mKanjiDtoList != null && mKanjiDtoList.size() > 0) {
            updateBookmarkIcon();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_practice:
                mPracticeMenu = findViewById(R.id.action_practice);
//                showHistory(mKanjiDtoList.get(mCurrentPos));
                showWritingScreen(mKanjiDtoList.get(mCurrentPos));
                break;
//            case R.id.action_history:
//                mHistoryMenu = findViewById(R.id.action_history);
//                showHistory(mKanjiDtoList.get(mCurrentPos));
//                break;
            case R.id.action_mode:
                mModeMenu = findViewById(R.id.action_mode);
                showModeOption();
                break;
            case R.id.action_share:
                mShareMenu = findViewById(R.id.action_share);
                shareFacebook(mKanjiDtoList.get(mCurrentPos));
//              showShareOption(mKanjiDtoList.get(mCurrentPos));
                break;
            case R.id.action_bookmark:
                addBookmark();
                break;
            case android.R.id.home:
                finish();
                break;
            case R.id.action_setting:
                mSettingMenu = findViewById(R.id.action_setting);
                showSettings();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addBookmark() {
        if (!mKanjiDtoList.get(mCurrentPos).isBookmarked) {
            final KanjiDto dto = mKanjiDtoList.get(mCurrentPos);
            final KanjiDao kanjiDao = new KanjiDao(getApplicationContext());
            kanjiDao.getKanjiInfoList(null, true, new CallBackListener<List<KanjiDto>>() {
                @Override
                public boolean onSuccess(List<KanjiDto> kanjiDtos) {
                    if (kanjiDtos.size() >= 30) {
                        showErrorDialog(getString(R.string.not_allow_add_more_bookmark_messge));
                        return false;
                    }

                    kanjiDao.updateKanjiBookmark(dto, true, new CallBackListener<Boolean>() {
                        @Override
                        public boolean onSuccess(Boolean aBoolean) {
                            mKanjiDtoList.get(mCurrentPos).isBookmarked = true;
                            updateBookmarkIcon();
                            CommonSharedPreferencesManager.saveBooleanPreference(
                                    getApplicationContext(), "isBookmarkChanged", true);
                            return false;
                        }
                    });
                    return false;
                }
            });

        }
    }

    private void updateBookmarkIcon() {
        if(mMenu != null) {
            if (mKanjiDtoList.get(mCurrentPos).isBookmarked) {
                mMenu.findItem(R.id.action_bookmark).setIcon(R.mipmap.ic_grade_blue_50_36dp);
            } else {
                mMenu.findItem(R.id.action_bookmark).setIcon(R.mipmap.ic_grade_white_36dp);
            }
        }
    }

    private void showWritingScreen(KanjiDto dto) {
        Intent intent = new Intent(getApplicationContext(), WritingActivity.class);
        intent.putExtra("kanjiDto", dto);
//        intent.putExtra("currentPos", mCurrentPos);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onPause");
        }
        stopSlideShow();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(BuildConfig.DEBUG) {
            Log.d(TAG, "onResume");
        }
        resumeSlideShow();
    }

    @Override
    public boolean onSuccess(List<KanjiDto> kanjiDtos) {
        findViewById(R.id.loading_panel).setVisibility(View.GONE);
        mKanjiDtoList = kanjiDtos;
        mPagerAdapter = new KanjiDetailPagerAdapter(getSupportFragmentManager(), mKanjiDtoList, new CallBackListener() {
            @Override
            public boolean onSuccess(Object o) {
                if (mViewModeType == ViewDetailMode.CLEAR.toInt()) {
                    mViewModeType = ViewDetailMode.DEFAULT.toInt();
                } else {
                    mViewModeType = ViewDetailMode.CLEAR.toInt();
                }
                setDisplayItems(mCurrentPos);

                CommonSharedPreferencesManager.saveIntPreference(getApplicationContext(),
                        "viewModeType", mViewModeType);

                // Send broad cast to inform that change view mode
                Intent intentInform = new Intent("ChangeViewMode");
                intentInform.putExtra("viewModeType", mViewModeType);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intentInform);
                return true;
            }
        });

        mViewPager = (ViewPager) findViewById(R.id.kanji_detail_pager);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(mCurrentPos);

        mPreviousView = (ImageView) findViewById(R.id.previous_kanji);
        mNextView = (ImageView) findViewById(R.id.next_kanji);

        mChangeLanguageView = (TextView) findViewById(R.id.change_language_view);

        mViewModeType = CommonSharedPreferencesManager.loadIntPreference(getApplicationContext(), "viewModeType", 0);

        registerActionListener();
//        setDisplayToolbar();
        setDisplayItems(mCurrentPos);
        setChangeLanguageText();
        updateBookmarkIcon();
        return false;
    }

    private void setChangeLanguageText() {
        if (mChangeLanguageView.getVisibility() != View.VISIBLE) {
            return;
        }

        boolean isEnglish = CommonSharedPreferencesManager.loadBooleanPreference(
                getApplicationContext(), Constant.CONTENT_LANGUAGE_KEY, true);
        if (isEnglish) {
            mChangeLanguageView.setText("V");
        } else {
            mChangeLanguageView.setText("E");
        }
    }

    private void showErrorDialog(String msg) {
        AlertDialog alertDialog = new AlertDialog.Builder(
                KanjiDetailActivity.this).create();

        // Setting Dialog Title
        alertDialog.setTitle("Alert Dialog");

        alertDialog.setIcon(R.mipmap.ic_launcher);
        // Setting Dialog Message
        alertDialog.setMessage(msg);

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void numberChanged(int id, int process) {
        Log.d(TAG, "Choose Process:" + process);
        if (id == Constant.KANJI_DETAIL_SLIDE_SHOW_TIME_DIALOG_ID) {
            delayTime = (process + 1) * 1000;
            startSlideShowMode();
            CommonSharedPreferencesManager.saveIntPreference(
                    getApplicationContext(), Constant.KANJI_DETAIL_SLIDE_SHOW_TIME_KEY, process);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.change_language_view:
                changeContentLanguage();
                break;
            case R.id.previous_kanji:
                mCurrentPos = mViewPager.getCurrentItem() - 1;
                mViewPager.setCurrentItem(mCurrentPos);
                setDisplayItems(mCurrentPos);
                updateBookmarkIcon();
                break;
            case R.id.next_kanji:
                if (mCurrentPos >= mKanjiDtoList.size() - 1) {
                    if (isRandom) {
                        Collections.shuffle(mKanjiDtoList);
                    }
                    mCurrentPos = 0;
                } else {
                    mCurrentPos = mViewPager.getCurrentItem() + 1;
                }

                mViewPager.setCurrentItem(mCurrentPos);
                setDisplayItems(mCurrentPos);
                updateBookmarkIcon();
        }
    }

    private void changeContentLanguage() {
        if (mChangeLanguageView.getVisibility() != View.VISIBLE) {
            return;
        }

        boolean isEnglish = CommonSharedPreferencesManager.loadBooleanPreference(
                getApplicationContext(), "ContentIsEnglish", true);
        if (isEnglish) {
            CommonSharedPreferencesManager.saveBooleanPreference(
                    getApplicationContext(), "ContentIsEnglish", false);
            mChangeLanguageView.setText("E");
        } else {
            CommonSharedPreferencesManager.saveBooleanPreference(
                    getApplicationContext(), "ContentIsEnglish", true);
            mChangeLanguageView.setText("V");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
//        mKanjiIdList.addAll(getIntent().getStringArrayListExtra("kanjiIds"));
        Log.d(TAG, "onNewIntent");

        String kanjiNotificationId = CommonSharedPreferencesManager
                .loadPreference(getApplicationContext(), Constant.KANJI_NOTIFICATION_ID_KEY, null);
        if (kanjiNotificationId != null) {
            Log.d(TAG, "Kanji Id Notification:" + kanjiNotificationId);
            reUpdateKanjiListView(kanjiNotificationId);
        }
    }

    private void reUpdateKanjiListView(final String kanjiId) {
        if (!mKanjiIdList.contains(kanjiId)) {
            KanjiDao kanjiDao = new KanjiDao(getApplicationContext());
            kanjiDao.getKanjiById(kanjiId, new CallBackListener<KanjiDto>() {
                @Override
                public boolean onSuccess(KanjiDto dto) {
                    mCurrentPos = 0;
                    mKanjiIdList.add(0, kanjiId);
                    mKanjiDtoList.add(0, dto);
                    mPagerAdapter.notifyDataSetChanged();
                    mViewPager.setAdapter(mPagerAdapter);
                    mViewPager.setCurrentItem(mCurrentPos);

                    registerActionListener();
                    setDisplayItems(mCurrentPos);
                    setChangeLanguageText();
                    updateBookmarkIcon();
                    return true;
                }
            });
        } else {
            mCurrentPos = mKanjiIdList.indexOf(kanjiId);
            mViewPager.setCurrentItem(mCurrentPos);
            setDisplayItems(mCurrentPos);
            updateBookmarkIcon();
        }
    }


//    private void showShareOption(final KanjiDto dto) {
//        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
//        View dialogView = getLayoutInflater().inflate(R.layout.kanji_detail_share_dialog, null);
//        dialogBuilder.setView(dialogView);
//
//        final AlertDialog alertDialog = dialogBuilder.create();
//        alertDialog.setIcon(R.mipmap.ic_launcher);
//        alertDialog.setTitle("Sharing Info");
//        dialogView.findViewById(R.id.share_facebook_view).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                shareFacebook(dto);
//                alertDialog.dismiss();
//            }
//        });
//
//        dialogView.findViewById(R.id.share_twitter_view).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                shareTwitter(dto);
//                alertDialog.dismiss();
//            }
//        });
//
//        alertDialog.show();
//
////
//    }

    private void shareFacebook(KanjiDto dto) {
        if(KanjiUtils.isAppInstalled(getApplicationContext(), "com.facebook.katana")) {
            LayoutInflater inflater =(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View mView = inflater.inflate(R.layout.kanji_detail_sharing_info, null, false);
            RelativeLayout view = (RelativeLayout) mView.findViewById(R.id.kanji_sharing_info_layout);
            ((TextView) view.findViewById(R.id.kanji_sharing_info_word)).setText(dto.word);
            ((TextView) view.findViewById(R.id.kanji_sharing_info_on)).setText(dto.onyomi);
            ((TextView) view.findViewById(R.id.kanji_sharing_info_kun)).setText(dto.kuniomi);

            boolean contentIsEnglish = CommonSharedPreferencesManager.loadBooleanPreference(
                    getApplicationContext(), Constant.CONTENT_LANGUAGE_KEY, true);
            if (contentIsEnglish) {
                ((TextView) view.findViewById(R.id.kanji_sharing_info_mean)).setText(dto.enMean);
            } else {
                ((TextView) view.findViewById(R.id.kanji_sharing_info_mean)).setText(dto.vnMean);
            }

            Bitmap bContent = loadBitmapFromView(view);

            SharePhoto photo = new SharePhoto.Builder()
                    .setBitmap(bContent)
                    .build();
            SharePhotoContent content = new SharePhotoContent.Builder()
                    .addPhoto(photo)
                    .build();
            ShareDialog shareDialog = new ShareDialog(this);
            shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);
        } else {
            Intent i = new Intent(android.content.Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.facebook.katana"));
            startActivity(i);
        }

    }
//
//    private void shareTwitter(KanjiDto dto) {
//        LayoutInflater inflater =(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View mView = inflater.inflate(R.layout.kanji_detail_sharing_info, null, false);
//        RelativeLayout view = (RelativeLayout) mView.findViewById(R.id.kanji_sharing_info_layout);
//        ((TextView) view.findViewById(R.id.kanji_sharing_info_word)).setText(dto.word);
//        ((TextView) view.findViewById(R.id.kanji_sharing_info_on)).setText(dto.onyomi);
//        ((TextView) view.findViewById(R.id.kanji_sharing_info_kun)).setText(dto.kuniomi);
//
//        boolean contentIsEnglish = CommonSharedPreferencesManager.loadBooleanPreference(
//                getApplicationContext(), Constant.CONTENT_LANGUAGE_KEY, true);
//        if (contentIsEnglish) {
//            ((TextView) view.findViewById(R.id.kanji_sharing_info_mean)).setText(dto.enMean);
//        } else {
//            ((TextView) view.findViewById(R.id.kanji_sharing_info_mean)).setText(dto.vnMean);
//        }
//
//        Bitmap bContent = loadBitmapFromView(view);
//        if(KanjiUtils.isAppInstalled(getApplicationContext(), "com.twitter.android")) {
//
//
//            try {
//                Intent shareIntent = new Intent(Intent.ACTION_SEND);
//                shareIntent.setPackage("com.twitter.android");
//                String path = MediaStore.Images.Media.insertImage(
//                        getApplicationContext().getContentResolver(), bContent, "Kanji NVK", null);
//                Uri phototUri = Uri.parse(path);
//                shareIntent.setType("image/png");
//                shareIntent.putExtra(Intent.EXTRA_STREAM, phototUri);
////                shareIntent.setClassName("com.twitter.android","com.twitter.android.PostActivity");
//                startActivity(Intent.createChooser(shareIntent, "Share Image"));
//            } catch (Exception e) {
//                Intent i = new Intent();
//                i.putExtra(Intent.EXTRA_TEXT, "error");
//                i.setAction(Intent.ACTION_VIEW);
//                i.setData(Uri.parse("https://mobile.twitter.com/compose/tweet"));
//                startActivity(i);
//            }
//        } else {
////            Intent i = new Intent(android.content.Intent.ACTION_VIEW);
////            i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.twitter.android"));
////            startActivity(i);
//            String path = MediaStore.Images.Media.insertImage(
//                    getApplicationContext().getContentResolver(), bContent, "Kanji NVK", null);
//            Uri phototUri = Uri.parse(path);
//            Intent i = new Intent();
//            i.putExtra(Intent.EXTRA_STREAM, phototUri);
//            i.setAction(Intent.ACTION_VIEW);
//            i.setData(Uri.parse("https://mobile.twitter.com/compose/tweet"));
//            startActivity(i);
//        }
//    }

    private Bitmap loadBitmapFromView(View view) {
        view.setDrawingCacheEnabled(true);
        view.setLayoutParams(new ViewGroup.LayoutParams(650, 200));
        view.measure(650, 200);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.buildDrawingCache(true);
        Bitmap b = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        return b;
    }

    @Override
    public void onUpdateValue(int id, int process) {
        mSlideShowTimeDialog.setTitle("Pick a time(second):" + (1 + process));
    }
}
