package muscular.man.tools.kanjinvk.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;
import java.util.List;

import muscular.man.tools.kanjinvk.R;
import muscular.man.tools.kanjinvk.activity.KanjiDetailActivity;
import muscular.man.tools.kanjinvk.common.CommonActionListener;
import muscular.man.tools.kanjinvk.model.dto.KanjiDto;
import muscular.man.tools.kanjinvk.model.enums.Categories;
import muscular.man.tools.kanjinvk.model.enums.SizeEnum;
import muscular.man.tools.kanjinvk.model.storage.dao.KanjiDao;
import muscular.man.tools.kanjinvk.view.ContextMenuRecyclerView;
import muscular.man.tools.kanjinvk.view.adapter.BasicSetSearchResultAdapter;

/**
 * A simple {@link BaseFragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link BasicSetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BasicSetFragment extends BaseFragment {

    private static final String TAG = BasicSetFragment.class.getSimpleName();

    private ContextMenuRecyclerView mRecyclerView;
    private BasicSetSearchResultAdapter mAdapter;
    private List<KanjiDto> mKanjiList;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BasicSetFragment.
     */
    public static BasicSetFragment newInstance() {
        return new BasicSetFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_basic_set, container, false);

        initView();
        return mView;
    }

    @Override
    public void initView() {
        super.initView();
        setHasOptionsMenu(true);

        mRecyclerView = (ContextMenuRecyclerView) mView.findViewById(R.id.basic_set_list_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), SizeEnum.RATE_FOUR.rate));

        mKanjiList = new ArrayList<>();

        getKanjiList();
        setAdapter();
    }

    private void getKanjiList() {
        KanjiDao kanjiDao = new KanjiDao(getContext());
        kanjiDao.getKanjiInfoList(Categories.BASIC_SET.dataUrl, false, new CommonActionListener.CallBackListener<List<KanjiDto>>() {
            @Override
            public boolean onSuccess(List<KanjiDto> kanjiDtos) {
                if (isVisible()) {
                    mView.findViewById(R.id.loading_panel).setVisibility(View.GONE);
                    mKanjiList = new ArrayList<>(kanjiDtos);
                    setAdapter();
                }
                return true;
            }
        });
    }

    private void setAdapter() {
        int orientation = getContext().getResources().getConfiguration().orientation;
        int numColumns = SizeEnum.RATE_FOUR.rate;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            numColumns = SizeEnum.RATE_SIX.rate;
        }
        mAdapter = new BasicSetSearchResultAdapter(mKanjiList, getContext(), numColumns, itemOnclickListener);
        mAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setAdapter();
    }

    CommonActionListener.OnclickItemListener<List<KanjiDto>> itemOnclickListener = new CommonActionListener.OnclickItemListener<List<KanjiDto>>() {
        @Override
        public boolean onSuccess(List<KanjiDto> dtos, int pos) {
            List<String> kanjiIds = new ArrayList<>();
            for (KanjiDto dto : dtos) {
                kanjiIds.add(dto.kid);
            }
            Intent intent = new Intent(getContext(), KanjiDetailActivity.class);
            intent.putStringArrayListExtra("kanjiIds", (ArrayList<String>) kanjiIds);
            intent.putExtra("currentPos", pos);
            startActivity(intent);
            InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mView.getWindowToken(), 0);
            return true;
        }
    };

    @Override
    public void onPause() {
        super.onPause();
    }
}
