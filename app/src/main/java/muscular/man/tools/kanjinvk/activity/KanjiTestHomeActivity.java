package muscular.man.tools.kanjinvk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import muscular.man.tools.kanjinvk.R;
import muscular.man.tools.kanjinvk.model.enums.JlptTabs;
import muscular.man.tools.kanjinvk.util.StringUtils;
import muscular.man.tools.kanjinvk.view.BaseActivity;
import muscular.man.tools.kanjinvk.view.CustomTestOptionView;
import muscular.man.tools.kanjinvk.view.adapter.KanjiTestPagerAdapter;

public class KanjiTestHomeActivity extends BaseActivity implements ViewPager.OnPageChangeListener, CustomTestOptionView.OnCustomTestOptionListener {

    private static final String TAG = KanjiTestHomeActivity.class.getSimpleName();

    private ViewPager mViewPager;
    private KanjiTestPagerAdapter mPagerAdapter;
    private View mLanguageMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kanji_test_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.kanji_test_home_toolbar);
        setSupportActionBar(toolbar);

        // Set default title.
        String defaultTitle = getResources().getString(R.string.kanji_test_n5_title);
        setTitle(defaultTitle);

        // Init default title.
        initView();
    }


    @Override
    public void initView() {
        createTabLayout();
    }

    /**
     * Create Tab layout
     */
    private void createTabLayout() {
        // Setup the viewpager
        mViewPager = (ViewPager) findViewById(R.id.kanji_test_home_view_pager);
        mPagerAdapter = new KanjiTestPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(this);

        // Setup the tab bar
        TabLayout tabLayout = (TabLayout) findViewById(R.id.kanji_test_home_tab_layout);
        tabLayout.setTabsFromPagerAdapter(mPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);
        // Set custom view
        int[] titles = JlptTabs.getTitles();
        for (int i = 0; i < titles.length ; i++) {
            View customTab = LayoutInflater.from(getApplicationContext()).inflate(R.layout.jlpt_tab_layout, null);
            TextView tabTitle = (TextView) customTab.findViewById(R.id.tab_title_text_view);

            TabLayout.Tab tab =  tabLayout.getTabAt(i);

            tabTitle.setText(titles[i]);

            if (tab != null) {
                tab.setCustomView(customTab);
                if (i == 0) {
                    customTab.setSelected(true);
                }
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        String title = StringUtils.EMPTY;
        switch (position) {
            case 0:
                title = getResources().getString(R.string.kanji_test_n5_title);
                break;
            case 1:
                title = getResources().getString(R.string.kanji_test_n4_title);
                break;
            case 2:
                title = getResources().getString(R.string.kanji_test_n3_title);
                break;
            case 3:
                title = getResources().getString(R.string.kanji_test_n2_title);
                break;
            case 4:
                title = getResources().getString(R.string.kanji_test_n1_title);
                break;
        }

        setTitle(title);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_kanji_test_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_create_custom_test:
                new CustomTestOptionView(this, this, "Custom Test Option", 9).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void confirmInfo(int jlptType, int amount, boolean isRandom) {
        startMainTest(jlptType, amount, isRandom);
    }

    private void startMainTest(int jlptType, int amount, boolean isRandom) {
        Intent intent = new Intent(getApplicationContext(), KanjiTestMainActivity.class);
        intent.putExtra("jlptType", jlptType);
        intent.putExtra("amount",amount);
        intent.putExtra("isCustomTest", true);

        startActivity(intent);
    }
}
