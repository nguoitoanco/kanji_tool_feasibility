package muscular.man.tools.kanjinvk.activity;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.ads.MobileAds;
import com.startapp.android.publish.StartAppSDK;

import muscular.man.tools.kanjinvk.R;
import muscular.man.tools.kanjinvk.common.CommonSharedPreferencesManager;
import muscular.man.tools.kanjinvk.model.enums.Tabs;
import muscular.man.tools.kanjinvk.view.BaseActivity;
import muscular.man.tools.kanjinvk.view.adapter.KanjiPagerAdapter;

public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "JBdsJDA26qcIinHd5EtwLsJr2";
    private static final String TWITTER_SECRET = "UxFlua06Lgu3m4mQmu2NqqK4OrGwaK8j3I221jS3BTpXybxqxZ";

    private ViewPager mViewPager;
    private KanjiPagerAdapter mPagerAdapter;
    private View mLanguageMenuItem;
    private Menu mMenu;
    DrawerLayout mDrawer;
    private PopupMenu mSettingPopupMenu;

    private int currentSelectItem = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StartAppSDK.init(this, "206735566", true);

        setContentView(R.layout.activity_main);
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-9948683300202690~8585778764");

        initView();
    }

    @Override
    public void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.home_tab_title);

//        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        mDrawer.setDrawerListener(toggle);
//        toggle.syncState();

//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
//        navigationView.setItemIconTintList(null);
        createTabLayout();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_setting) {
            mLanguageMenuItem = findViewById(R.id.action_setting);
//            changeContentLanguage();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Create Tab layout
     */
    private void createTabLayout() {
        // Setup the viewpager
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mPagerAdapter = new KanjiPagerAdapter(getSupportFragmentManager(), getApplicationContext());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(this);

        // Setup the tab bar
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setTabsFromPagerAdapter(mPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(
                getApplicationContext(), R.color.colorWhite));
        // Set custom view
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            View customTab = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_tab_layout, null);
            ImageView imageView = (ImageView)customTab.findViewById(R.id.tab_icon);
            imageView.setImageResource(Tabs.getIcons()[i]);

            TabLayout.Tab tab =  tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(customTab);
                if (i == 0) {
                    customTab.setSelected(true);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        resetToolbarPosition();

        Fragment currentFragment = mPagerAdapter
                .getRegisteredFragment(mViewPager.getCurrentItem());
        FragmentManager fragmentManager = currentFragment.getChildFragmentManager();
        int count = fragmentManager.getBackStackEntryCount();
        if (count > 0) {
            fragmentManager.popBackStack();
            if (count == 1) {
                setTitle(R.string.home_tab_title);
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    private void resetToolbarPosition() {
        // Reset position of toolbar
        CoordinatorLayout coordinator = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        AppBarLayout appbar = (AppBarLayout) findViewById(R.id.main_appbar_layout);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appbar.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        int[] consumed = new int[2];
        behavior.onNestedPreScroll(coordinator, appbar, null, 0, -1000, consumed);
    }

    @Override
    public void onPageSelected(int position) {
        resetToolbarPosition();
        if (position != Tabs.HOME.getIndex()) {
            CommonSharedPreferencesManager.saveBooleanPreference(
                    getApplicationContext(), "searchViewVisible", false);

            if (position == Tabs.BOOKMARK.getIndex()) {
                setTitle(R.string.bookmark_tab_title);
            }

            if (position == Tabs.ABOUT.getIndex()) {
                setTitle(R.string.about_tab_title);
            }
        } else {
            CommonSharedPreferencesManager.saveBooleanPreference(
                    getApplicationContext(), "searchViewVisible", true);

            Fragment currentFragment = mPagerAdapter
                    .getRegisteredFragment(mViewPager.getCurrentItem());
            FragmentManager fragmentManager = currentFragment.getChildFragmentManager();
            int count = fragmentManager.getBackStackEntryCount();
            if (count == 0) {
                setTitle(R.string.home_tab_title);
            } else {
                String title = CommonSharedPreferencesManager.loadPreference(
                    getApplicationContext(), "backUpTitle", getString(R.string.home_tab_title));
                setTitle(title);
            }
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

//    @SuppressWarnings("StatementWithEmptyBody")
//    @Override
//    public boolean onNavigationItemSelected(MenuItem item) {
//        // Handle navigation view item clicks here.
//        int id = item.getItemId();
//        if (id != currentSelectItem) {
//
//            currentSelectItem = id;
//            switch (id) {
//                case R.id.nav_jlpt_kanji_n5:
//                    mViewPager.setCurrentItem(0);
////                    openSearchResult(Categories.JLPT_N5.index, null);
////                    tabLayout.getTabAt(0).select();
//                    break;
//                case R.id.nav_jlpt_kanji_n4:
//                    mViewPager.setCurrentItem(0);
////                    openSearchResult(Categories.JLPT_N4.index, null);
//                    break;
//                case R.id.nav_jlpt_kanji_n3:
//                    mViewPager.setCurrentItem(0);
////                    openSearchResult(Categories.JLPT_N3.index, null);
//                    break;
//                case R.id.nav_jlpt_kanji_n2:
//                    mViewPager.setCurrentItem(0);
////                    openSearchResult(Categories.JLPT_N2.index, null);
//                    break;
//                case R.id.nav_jlpt_kanji_n1:
//                    mViewPager.setCurrentItem(0);
////                    openSearchResult(Categories.JLPT_N1.index, null);
//                    break;
//                case R.id.nav_jlpt_kanji_test:
////                    mViewPager.setCurrentItem(0);
//                    break;
////                case R.id.nav_app_setting:
////                    break;
//                case R.id.nav_app_about:
//                    break;
//            }
//        }
//
//        mDrawer.closeDrawer(GravityCompat.START);
//        return true;
//    }

}
