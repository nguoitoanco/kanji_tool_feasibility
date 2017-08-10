package muscular.man.tools.kanjinvk.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import muscular.man.tools.kanjinvk.R;
import muscular.man.tools.kanjinvk.activity.KanjiTestMainActivity;
import muscular.man.tools.kanjinvk.common.CommonActionListener;
import muscular.man.tools.kanjinvk.common.CommonSharedPreferencesManager;
import muscular.man.tools.kanjinvk.model.dto.KanjiTestBlockDto;
import muscular.man.tools.kanjinvk.model.dto.KanjiTestDto;
import muscular.man.tools.kanjinvk.model.enums.Blocks;
import muscular.man.tools.kanjinvk.model.storage.dao.KanjiTestDao;
import muscular.man.tools.kanjinvk.view.ContextMenuRecyclerView;
import muscular.man.tools.kanjinvk.view.adapter.KanjiTestBlockResultAdapter;

/**
 * A simple {@link BaseFragment} subclass.
 */
public class KanjiBlockListFragment extends BaseFragment
        implements CommonActionListener.OnclickItemListener<KanjiTestBlockDto>
        , OnCreateContextMenuListener {

    private static final String TAG = KanjiBlockListFragment.class.getSimpleName();

    private static final String JLPT_CATES_PARAM = "JLPT_CATEGORY";

    private ContextMenuRecyclerView mRecyclerView;

    private KanjiTestBlockResultAdapter mAdapter;
    private List<KanjiTestBlockDto> mKanjiTestBlockDtoList;

    private int selectedPos = -1;

    public static KanjiBlockListFragment newInstance(int param) {
        // Required empty public constructor
        Bundle bundle = new Bundle();
        bundle.putInt(JLPT_CATES_PARAM, param);
        KanjiBlockListFragment fragment = new KanjiBlockListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_kanji_block_list, container, false);
        initView();
        return mView;
    }


    @Override
    public void initView() {
        mRecyclerView = (ContextMenuRecyclerView) mView.findViewById(R.id.kanji_test_list_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        registerForContextMenu(mRecyclerView);

        LinearLayoutManager lineLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(lineLayoutManager);
        mKanjiTestBlockDtoList = new ArrayList<>();

        getKanjiTestDtoList();
        setAdapter();
    }

    private void getKanjiTestDtoList() {
        KanjiTestDao kanjiTestDao = new KanjiTestDao(getContext());
        kanjiTestDao.getKanjiTestInfoList(getSearchKey(), false, new CommonActionListener.CallBackListener<List<KanjiTestDto>>() {
            @Override
            public boolean onSuccess(List<KanjiTestDto> kanjiTestDtos) {
                if (isVisible()) {
                    mView.findViewById(R.id.loading_panel).setVisibility(View.GONE);
                    mKanjiTestBlockDtoList = getBlock(kanjiTestDtos);
                    setAdapter();
                }

                return true;
            }
        });
    }

    private String getSearchKey() {
        int category = getArguments().getInt(JLPT_CATES_PARAM);
        String key = "category";
        String dataUrl = "TES" + key + "%";
        switch (category) {
            case 0:
                dataUrl = dataUrl.replace(key, "5");
                break;
            case 1:
                dataUrl = dataUrl.replace(key, "4");
                break;
            case 2:
                dataUrl = dataUrl.replace(key, "3");
                break;
            case 3:
                dataUrl = dataUrl.replace(key, "2");
                break;
            case 4:
                dataUrl = dataUrl.replace(key, "1");
                break;
        }

        return dataUrl;
    }

    private void setAdapter() {
        mAdapter = new KanjiTestBlockResultAdapter(mKanjiTestBlockDtoList, getContext(), this, this);
        mAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mAdapter);
    }

    private List<KanjiTestBlockDto> getBlock(List<KanjiTestDto> dtos) {
        List<KanjiTestBlockDto> blockDtos = new ArrayList<>();
        int numberBlock = dtos.size() / Blocks.BLOCK_10.elementOfLine;
        int start = 0;
        int end = 0;
        for (int i = 0; i < numberBlock; i++) {
            start = i * Blocks.BLOCK_10.elementOfLine;
            end = start + Blocks.BLOCK_10.elementOfLine;
            KanjiTestBlockDto dto = new KanjiTestBlockDto();
            dto.setKanjiTestDtos(dtos.subList(start, end));
            blockDtos.add(dto);
        }

        if (end < dtos.size()) {
            KanjiTestBlockDto dto = new KanjiTestBlockDto();
            dto.setKanjiTestDtos(dtos.subList(end, dtos.size()));
            blockDtos.add(dto);
        }
        return blockDtos;
    }

    @Override
    public boolean onSuccess(KanjiTestBlockDto kanjiTestBlockDto, int i) {
        boolean isTestRandom = CommonSharedPreferencesManager
                .loadBooleanPreference(getContext(), "isRandomTest", false);
        startMainTest(i, isTestRandom);
        return false;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        // Inflate Menu from xml resource
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.context_menu_kanji_test_block, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        super.onContextItemSelected(item);
        final ContextMenuRecyclerView.RecyclerContextMenuInfo
                info = (ContextMenuRecyclerView.RecyclerContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.default_test_action:
                startMainTest(info.position, false);
                break;
            case R.id.random_test_action:
                startMainTest(info.position, true);
                break;
        }

        return true;
    }

    private void startMainTest(int blockIndex, boolean isRandom) {
        List<KanjiTestDto> kanjiTestDtos =
                mKanjiTestBlockDtoList.get(blockIndex).getKanjiTestDtos();
        if (isRandom) {
            Collections.shuffle(kanjiTestDtos);
        }

        CommonSharedPreferencesManager.saveBooleanPreference(getContext(), "isRandomTest", isRandom);

        Intent intent = new Intent(getContext(), KanjiTestMainActivity.class);
        intent.putParcelableArrayListExtra("kanjiTestDtos",
                (ArrayList<? extends Parcelable>) kanjiTestDtos);
        startActivity(intent);
    }


//    @Override
//    public void onShowMore(View view, KanjiTestBlockDto kanjiTestBlockDto, int pos) {
////        ViewParent parent = view.getParent();
//        selectedPos = pos;
//        view.setOnCreateContextMenuListener(this);
//        view.showContextMenu();
////        mRecyclerView.showContextMenuForChild(view);
////        parent.showContextMenuForChild(view);
//    }
}
