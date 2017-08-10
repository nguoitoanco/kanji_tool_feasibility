package muscular.man.tools.kanjinvk.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import muscular.man.tools.kanjinvk.BuildConfig;
import muscular.man.tools.kanjinvk.R;
import muscular.man.tools.kanjinvk.activity.KanjiDetailActivity;
import muscular.man.tools.kanjinvk.common.CommonSharedPreferencesManager;
import muscular.man.tools.kanjinvk.model.dto.KanjiBlockDto;
import muscular.man.tools.kanjinvk.model.dto.KanjiDto;
import muscular.man.tools.kanjinvk.model.enums.Blocks;
import muscular.man.tools.kanjinvk.model.enums.Categories;
import muscular.man.tools.kanjinvk.model.enums.SizeEnum;
import muscular.man.tools.kanjinvk.model.enums.ViewListType;
import muscular.man.tools.kanjinvk.model.storage.dao.KanjiDao;
import muscular.man.tools.kanjinvk.util.StringUtils;
import muscular.man.tools.kanjinvk.view.ContextMenuRecyclerView;
import muscular.man.tools.kanjinvk.view.adapter.KanjiSearchResultAdapter;

import static muscular.man.tools.kanjinvk.common.CommonActionListener.CallBackListener;
import static muscular.man.tools.kanjinvk.common.CommonActionListener.OnclickItemListener;
import static muscular.man.tools.kanjinvk.fragment.DropDownAnimationFragment.DropDown;

/**
 * A simple {@link BaseFragment} subclass.
 */
public class KanjiSearchResultFragment extends BaseFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener, View.OnClickListener {
    private static final String TAG = KanjiSearchResultFragment.class.getSimpleName();

    private static final String CATEGORY_SEARCH = "CATEGORY_SEARCH";
    private static final String CONTENT_SEARCH = "CONTENT_SEARCH";
    private static final int MIN_NUMBER_TO_BREAK_BLOCK = 5;

    private ContextMenuRecyclerView mRecyclerView;
    private DropDown dropDown;

    private TextView mChangeLanguageView;
    private TextView optionTextView;

    private KanjiSearchResultAdapter mAdapter;
    private List<KanjiDto> mKanjiList;
    private PopupMenu mDisplayTypeMenu;
    private MenuItem mSearchItem;

    private LinearLayoutManager mLinearLayoutManager;
    private GridLayoutManager mGridLayoutManager;

    public static KanjiSearchResultFragment newInstance(int category, String content) {
        Bundle bundle = new Bundle();
        bundle.putInt(CATEGORY_SEARCH, category);
        bundle.putString(CONTENT_SEARCH, content);
        KanjiSearchResultFragment fragment = new KanjiSearchResultFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_kanji_search_result, container, false);

        initView();
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        int viewListType = CommonSharedPreferencesManager.loadIntPreference(getContext(), "ViewListType", 0);
        if (viewListType == ViewListType.DETAIL.index) {
            setAdapter();
        }
        setLanguageText();

        PreferenceManager
                .getDefaultSharedPreferences(getContext())
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void initView() {
        super.initView();
        setHasOptionsMenu(true);

        dropDown = DropDown.HIDE_OPTION;

        mRecyclerView = (ContextMenuRecyclerView) mView.findViewById(R.id.kanji_list_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager lineLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(lineLayoutManager);
        mKanjiList = new ArrayList<>();

        mChangeLanguageView = (TextView)mView.findViewById(R.id.change_language_view);
        mChangeLanguageView.setOnClickListener(this);

//        optionTextView = (TextView) getActivity()
//                .findViewById(R.id.movies_action_bar)
//                .findViewById(R.id.movie_type_option);

        getKanjiList();

//        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//
//                int lastVisibleItem = 0;
//                if (getViewListType() == ViewListType.GRID.index) {
//                    lastVisibleItem = mGridLayoutManager.findLastVisibleItemPosition();
//                } else {
//                    lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();
//                }
//
//                if (lastVisibleItem >= (mKanjiList.size() - 1)) {
//                    getKanjiList();
//                }
//            }
//        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG, "onCreateOptionsMenu");
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.filter_menu, menu);
        if (mSearchItem == null) {
            inflater.inflate(R.menu.filter_menu, menu);
            mSearchItem = menu.findItem(R.id.filter_action);
            SearchView searchView = (SearchView)mSearchItem.getActionView();
            searchView.setLayoutParams(new LinearLayoutCompat.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            searchView.setOnQueryTextListener(this);
        }

        setDisplayFilterItem();
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mAdapter.getFilter().filter(newText);
        return super.onQueryTextChange(newText);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        int viewListType = getViewListType();
        if (viewListType == ViewListType.GRID.index) {
            int orientation = getContext().getResources().getConfiguration().orientation;
            int numColumns = SizeEnum.RATE_FOUR.rate;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                numColumns = SizeEnum.RATE_SIX.rate;
            }
            mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), numColumns));
            mAdapter = new KanjiSearchResultAdapter(mKanjiList, ViewListType.GRID, getContext(), numColumns, itemOnclickListener);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return super.onQueryTextSubmit(query);
    }

    /**
     * Set display type view and save setting into share preference
     */
    private void setDisplayTypeMenu() {

        final ImageView displayTypeView = (ImageView) mView.findViewById(R.id.search_result_display_type_view);
        final int viewListType = getViewListType();
        if (mKanjiList.size() > 0) {
            displayTypeView.setVisibility(View.VISIBLE);
            setViewListType(viewListType);
        }

        displayTypeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDisplayTypeMenu == null) {
                    mDisplayTypeMenu = new PopupMenu(getContext(), displayTypeView);
                    mDisplayTypeMenu.getMenuInflater().inflate(R.menu.view_list_type_popup_menu, mDisplayTypeMenu.getMenu());
                    setCheckViewListTypeOption(viewListType);
                    mDisplayTypeMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (!item.isChecked()) {
                                changeDisplayTypeView(item.getItemId());
                                item.setChecked(true);
                            }
                            if (BuildConfig.DEBUG) {
                                Log.d(TAG, "" + item.isChecked());
                            }
                            return false;
                        }
                    });
                }
                mDisplayTypeMenu.show();
            }
        });
    }

    private void changeDisplayTypeView(int itemId) {
        switch (itemId) {
            case R.id.block_type_view_action:
                CommonSharedPreferencesManager.saveIntPreference(getContext(), "ViewListType", ViewListType.BLOCK.index);
                setViewListType(ViewListType.BLOCK.index);
                break;

            case R.id.grid_type_view_action:
                CommonSharedPreferencesManager.saveIntPreference(getContext(), "ViewListType", ViewListType.GRID.index);
                setViewListType(ViewListType.GRID.index);
                break;

            case R.id.detail_type_view_action:
                CommonSharedPreferencesManager.saveIntPreference(getContext(), "ViewListType", ViewListType.DETAIL.index);
                setViewListType(ViewListType.DETAIL.index);
                break;
        }
    }

    private void setCheckViewListTypeOption(int type) {
        if (type == ViewListType.BLOCK.index) {
            mDisplayTypeMenu.getMenu().findItem(R.id.block_type_view_action).setChecked(true);
        }

        if (type == ViewListType.GRID.index) {
            mDisplayTypeMenu.getMenu().findItem(R.id.grid_type_view_action).setChecked(true);
        }

        if (type == ViewListType.DETAIL.index) {
            mDisplayTypeMenu.getMenu().findItem(R.id.detail_type_view_action).setChecked(true);
        }
    }

    private void setViewListType(int type) {
        if (mLinearLayoutManager == null) {
            mLinearLayoutManager = new LinearLayoutManager(getContext());
        }


        int orientation = getContext().getResources().getConfiguration().orientation;
        int numColumns = SizeEnum.RATE_FOUR.rate;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            numColumns = SizeEnum.RATE_SIX.rate;
        }

        if (mGridLayoutManager == null) {
            mGridLayoutManager = new GridLayoutManager(getContext(), numColumns);
        }

        if (type == ViewListType.BLOCK.index) {
            mRecyclerView.setLayoutManager(mLinearLayoutManager);
            mAdapter = new KanjiSearchResultAdapter(getBlocks(), ViewListType.BLOCK, getContext(), itemOnclickListener, 0);
            mRecyclerView.setAdapter(mAdapter);
        }

        if (type == ViewListType.GRID.index) {
            mRecyclerView.setLayoutManager(mGridLayoutManager);
            mAdapter = new KanjiSearchResultAdapter(mKanjiList, ViewListType.GRID, getContext(), numColumns, itemOnclickListener);
            mRecyclerView.setAdapter(mAdapter);
        }

        if (type == ViewListType.DETAIL.index) {
            mRecyclerView.setLayoutManager(mLinearLayoutManager);
            mAdapter = new KanjiSearchResultAdapter(mKanjiList, ViewListType.DETAIL, getContext(), SizeEnum.RATE_FOUR.rate, itemOnclickListener);
            mRecyclerView.setAdapter(mAdapter);
        }

        setDisplayFilterItem();
        setChangeLanguageView();
    }

    /**
     * Get block in case view block type
     *
     */
    private List<KanjiBlockDto> getBlocks() {
        List<KanjiBlockDto> blockDtos = new ArrayList<>();
        int numberBlock = mKanjiList.size() / Blocks.BLOCK_10.elementOfLine;
        int start = 0;
        int end = 0;

        for (int i = 0; i < numberBlock; i++) {
            start = i * Blocks.BLOCK_10.elementOfLine;
            end = start + Blocks.BLOCK_10.elementOfLine;

            KanjiBlockDto dto = new KanjiBlockDto();
            if (i < numberBlock - 1) {
                dto.setKanjiDtos(mKanjiList.subList(start, end));
                blockDtos.add(dto);
            } else  {
                if ((mKanjiList.size() - end) < MIN_NUMBER_TO_BREAK_BLOCK) {
                    dto.setKanjiDtos(mKanjiList.subList(start, mKanjiList.size()));
                    blockDtos.add(dto);
                } else  {
                    dto.setKanjiDtos(mKanjiList.subList(start, end));
                    blockDtos.add(dto);

                    KanjiBlockDto dtoEnd = new KanjiBlockDto();
                    dtoEnd.setKanjiDtos(mKanjiList.subList(end, mKanjiList.size()));
                    blockDtos.add(dtoEnd);
                }
            }
        }

        return blockDtos;
    }

    private void getKanjiList() {
//        int eachLoadSize = 20;
//        int viewType = getViewListType();
//        if (viewType == ViewListType.BLOCK.index) {
//            eachLoadSize = 100;
//        }
        KanjiDao kanjiDao = new KanjiDao(getContext());
        kanjiDao.getKanjiInfoList(getSearchKey(), false, new CallBackListener<List<KanjiDto>>() {
            @Override
            public boolean onSuccess(List<KanjiDto> kanjiDtos) {
                if (isVisible() && kanjiDtos.size() > 0) {
                    Log.d(TAG, "" + kanjiDtos.size());
                    mKanjiList = new ArrayList<>(kanjiDtos);
                    setAdapter();
                    setHeader();
//                    setDisplayFilterItem();
                    setDisplayTypeMenu();
                }
                mView.findViewById(R.id.loading_panel).setVisibility(View.GONE);
                return true;
            }
        });
    }

    private void setAdapter() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        } else {
            mAdapter = new KanjiSearchResultAdapter(mKanjiList, ViewListType.DETAIL, getContext(), SizeEnum.RATE_FOUR.rate, itemOnclickListener);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    private void setHeader() {
        // Set text header
        String headerText = String.format(getContext().getString(R.string.kanji_search_result_header_text),mKanjiList.size());
        TextView headerView = (TextView) mView.findViewById(R.id.search_result_header_view);
        headerView.setVisibility(View.VISIBLE);
        headerView.setText(headerText);
        setChangeLanguageView();
    }

    private String getSearchKey() {
        String dataUrl = StringUtils.EMPTY;
        int category = getArguments().getInt(CATEGORY_SEARCH);

        switch (category) {
            case 1:
                dataUrl = Categories.JLPT_N1.dataUrl;
                break;
            case 2:
                dataUrl = Categories.JLPT_N2.dataUrl;
                break;
            case 3:
                dataUrl = Categories.JLPT_N3.dataUrl;
                break;
            case 4:
                dataUrl = Categories.JLPT_N4.dataUrl;
                break;
            case 5:
                dataUrl = Categories.JLPT_N5.dataUrl;
                break;
            case 6:
                dataUrl = Categories.BASIC_SET.dataUrl;
                break;
        }
        return dataUrl;
    }

    OnclickItemListener<List<KanjiDto>> itemOnclickListener = new OnclickItemListener<List<KanjiDto>>() {
        @Override
        public boolean onSuccess(List<KanjiDto> dtos, int pos) {
            View view = mSearchItem.getActionView();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                view.clearFocus();
            }
            List<String> kanjiIds = new ArrayList<>();
            for (KanjiDto dto : dtos) {
                kanjiIds.add(dto.kid);
            }
            Intent intent = new Intent(getContext(), KanjiDetailActivity.class);
            intent.putStringArrayListExtra("kanjiIds", (ArrayList<String>) kanjiIds);
            intent.putExtra("currentPos", pos);
            startActivity(intent);
            return true;
        }
    };

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        int viewListType = sharedPreferences.getInt("ViewListType", 0);
        if (mSearchItem!= null && key.equals("searchViewVisible")) {
            boolean isVisible = sharedPreferences.getBoolean("searchViewVisible", false);
            boolean isNotBlockType = (viewListType != ViewListType.BLOCK.index);
            mSearchItem.setVisible(isVisible && isNotBlockType);
        }

        if (key.equals("ContentIsEnglish")) {
            if (viewListType == ViewListType.DETAIL.index) {
                mAdapter = new KanjiSearchResultAdapter(mKanjiList, ViewListType.DETAIL, getContext(), SizeEnum.RATE_FOUR.rate, itemOnclickListener);
                mAdapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(mAdapter);
            }
        }
    }

    private int getViewListType() {
        return CommonSharedPreferencesManager.loadIntPreference(
                getContext(), "ViewListType", ViewListType.DETAIL.index);
    }

    private void setDisplayFilterItem() {
        if (mSearchItem != null) {
            boolean isVisible = CommonSharedPreferencesManager.loadBooleanPreference(
                    getContext(), "searchViewVisible", true);
            int viewListType = getViewListType();
            boolean isNotBlockType = (viewListType != ViewListType.BLOCK.index);
            mSearchItem.setVisible(isVisible && isNotBlockType);
        }
    }

    private void setChangeLanguageView() {
        boolean isVisible = (getViewListType() == ViewListType.DETAIL.index);
        if (isVisible && mKanjiList.size() > 0) {
            mChangeLanguageView.setVisibility(View.VISIBLE);

        } else {
            mChangeLanguageView.setVisibility(View.GONE);
        }
    }

    private void setLanguageText() {
        boolean isEnglish = CommonSharedPreferencesManager.loadBooleanPreference(
                getContext(), "ContentIsEnglish", true);
        if (isEnglish) {
            mChangeLanguageView.setText("V");
        } else {
            mChangeLanguageView.setText("E");
        }
    }

    private void changeContentLanguage() {
        boolean isEnglish = CommonSharedPreferencesManager.loadBooleanPreference(
                getContext(), "ContentIsEnglish", true);
        CommonSharedPreferencesManager.saveBooleanPreference(
                getContext(), "ContentIsEnglish", !isEnglish);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.change_language_view:
                changeContentLanguage();
                setLanguageText();
                break;
        }
    }

//    private void showAnimationFragment() {
//        if (!dropDown.isShown()) {
//            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
//            DropDownAnimationFragment dropDownAnimationFragment = new DropDownAnimationFragment();
//
//            CallBackListener<Integer> callBackListener = new CallBackListener<Integer>() {
//                @Override
//                public boolean onSuccess(Integer integer) {
//                    category = (int)data;
//                    dropDown = DropDown.HIDE_OPTION;
//                    reFreshRecyclerView(recyclerView);
//                    optionTextView.setCompoundDrawablesWithIntrinsicBounds(
//                            0, 0, R.mipmap.ic_arrow_drop_down, 0);
//                    optionTextView.setText(getString(OptionView.values()[category].getTitleId()));
//                    movieTitle = optionTextView.getText().toString();
//                    return true;
//                }
//            };
//
//            dropDownAnimationFragment.setServiceCallBackListener(callBackListener);
//
////            fragmentTransaction.add(R.id.main_screen, dropDownAnimationFragment);
////            fragmentTransaction.addToBackStack(null);
////            fragmentTransaction.commit();
//            optionTextView.setCompoundDrawablesWithIntrinsicBounds(
//                    0, 0, R.mipmap.ic_arrow_drop_up, 0);
//            dropDown = DropDown.SHOW_OPTION;
//
//        } else {
//            getFragmentManager().popBackStack();
//            optionTextView.setCompoundDrawablesWithIntrinsicBounds(
//                    0, 0, R.mipmap.ic_arrow_drop_down, 0);
//            dropDown = DropDown.HIDE_OPTION;
//        }
//    }

    @Override
    public void onPause() {
        super.onPause();
        PreferenceManager
                .getDefaultSharedPreferences(getContext())
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
