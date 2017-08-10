package muscular.man.tools.kanjinvk.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import muscular.man.tools.kanjinvk.R;
import muscular.man.tools.kanjinvk.activity.KanjiDetailActivity;
import muscular.man.tools.kanjinvk.common.CommonSharedPreferencesManager;
import muscular.man.tools.kanjinvk.model.dto.KanjiDto;
import muscular.man.tools.kanjinvk.model.storage.dao.KanjiDao;
import muscular.man.tools.kanjinvk.view.ContextMenuRecyclerView;
import muscular.man.tools.kanjinvk.view.adapter.BookmarkAdapter;

import static muscular.man.tools.kanjinvk.common.CommonActionListener.CallBackListener;
import static muscular.man.tools.kanjinvk.common.CommonActionListener.OnclickItemListener;

/**
 * A simple {@link BaseFragment} subclass.
 */
public class BookmarkFragment extends BaseFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener, View.OnClickListener {
    private static final String TAG = BookmarkFragment.class.getSimpleName();

    private ImageView mDeleteAllView;
    private ContextMenuRecyclerView mRecyclerView;
    private TextView mChangeLanguageView;

    private BookmarkAdapter mAdapter;
    private List<KanjiDto> mBookmarkList;

    public static BookmarkFragment newInstance() {
        Bundle bundle = new Bundle();
        BookmarkFragment fragment = new BookmarkFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_kanji_bookmark, container, false);

        initView();
        return mView;
    }


    @Override
    public void initView() {
        super.initView();

        mDeleteAllView = (ImageView) mView.findViewById(R.id.delete_all_image_view);
        mRecyclerView = (ContextMenuRecyclerView) mView.findViewById(R.id.kanji_list_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager lineLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(lineLayoutManager);
        registerForContextMenu(mRecyclerView);
        mBookmarkList = new ArrayList<>();


        mChangeLanguageView = (TextView)mView.findViewById(R.id.change_language_view);
        mChangeLanguageView.setOnClickListener(this);

        getKanjiList();
        setAdapter();
    }

    private void getKanjiList() {
        KanjiDao kanjiDao = new KanjiDao(getContext());
        kanjiDao.getKanjiInfoList(null, true, new CallBackListener<List<KanjiDto>>() {
            @Override
            public boolean onSuccess(List<KanjiDto> kanjiDtos) {
                if (isVisible()) {
                    mView.findViewById(R.id.loading_panel).setVisibility(View.GONE);
                    mBookmarkList = new ArrayList<>(kanjiDtos);
                    setAdapter();
                    setHeader();
                }

                return true;
            }
        });
    }

    private void setAdapter() {
        mAdapter = new BookmarkAdapter(mBookmarkList, getContext(), itemOnclickListener);
        mAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mAdapter);
    }

    private void setHeader() {
        // Set text header
        String headerText = String.format(getContext().getString(R.string.kanji_search_result_header_text),mBookmarkList.size());
        TextView headerView = (TextView) mView.findViewById(R.id.search_result_header_view);
        headerView.setVisibility(View.VISIBLE);
        headerView.setText(headerText);
        if (mBookmarkList.size() > 0) {
            mDeleteAllView.setVisibility(View.VISIBLE);
            mDeleteAllView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeleteAllConfirmDialog(getContext());
                }
            });
            mChangeLanguageView.setVisibility(View.VISIBLE);
        } else {
            mDeleteAllView.setVisibility(View.GONE);
            mChangeLanguageView.setVisibility(View.GONE);
        }
    }

    OnclickItemListener<List<KanjiDto>> itemOnclickListener = new OnclickItemListener<List<KanjiDto>>() {
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
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        PreferenceManager
                .getDefaultSharedPreferences(getContext())
                .registerOnSharedPreferenceChangeListener(this);

        setLanguageText();

        boolean isBookmarkChanged = CommonSharedPreferencesManager.loadBooleanPreference(
                getContext(), "isBookmarkChanged", false);
        if (isBookmarkChanged) {
            getKanjiList();
            CommonSharedPreferencesManager.saveBooleanPreference(getContext(),
                    "isBookmarkChanged", false);
        } else {
            setAdapter();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        // Inflate Menu from xml resource
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.context_menu_bookmark, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        super.onContextItemSelected(item);
        if (item.getItemId() == R.id.delete_action) {
            final ContextMenuRecyclerView.RecyclerContextMenuInfo info = (ContextMenuRecyclerView.RecyclerContextMenuInfo) item.getMenuInfo();
            KanjiDao kanjiDao = new KanjiDao(getContext());
            kanjiDao.updateKanjiBookmark(mBookmarkList.get(info.position), false, new CallBackListener<Boolean>() {
                @Override
                public boolean onSuccess(Boolean aBoolean) {
                    mBookmarkList.remove(info.position);
                    setAdapter();
                    setHeader();
                    return true;
                }
            });
        }
        return false;
    }

    private void showDeleteAllConfirmDialog(Context ctx) {
        final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();

        // Setting Dialog Title
        alertDialog.setTitle(ctx.getString(R.string.confirm_dialog_title));

        alertDialog.setIcon(R.mipmap.ic_launcher);

        // Setting Dialog Message
        alertDialog.setMessage(ctx.getString(R.string.delete_all_confirm_message));

        // Setting OK Button
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, ctx.getString(R.string.ok_button_confirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                deleteAllBookmark();
            }
        });

        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, ctx.getString(R.string.cancel_button_confirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    private void deleteAllBookmark() {
        KanjiDao kanjiDao = new KanjiDao(getContext());
        kanjiDao.removeKanjiBookmarkList(mBookmarkList, new CallBackListener<Boolean>() {
            @Override
            public boolean onSuccess(Boolean isSuccess) {
                if (isSuccess) {
                    mBookmarkList.clear();
                    setAdapter();
                    setHeader();
                }
                return false;
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("ContentIsEnglish")) {
            setAdapter();
        }
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
    public void onPause() {
        super.onPause();
        PreferenceManager
                .getDefaultSharedPreferences(getContext())
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
