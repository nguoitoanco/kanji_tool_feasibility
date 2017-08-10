package muscular.man.tools.kanjinvk.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import muscular.man.tools.kanjinvk.R;
import muscular.man.tools.kanjinvk.activity.KanjiTestHomeActivity;
import muscular.man.tools.kanjinvk.common.CommonSharedPreferencesManager;
import muscular.man.tools.kanjinvk.model.constant.Constant;
import muscular.man.tools.kanjinvk.model.enums.FragmentEnum;
import muscular.man.tools.kanjinvk.view.BaseView;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseFragment implements BaseView {
    private static final String TAG = HomeFragment.class.getSimpleName();
//    private EditText mKeywordText;
//    private Button mBtnSearch;

    public static HomeFragment newInstance() {
        // Required empty public constructor
        return new HomeFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_home, container, false);
        initView();
        return mView;
    }


    public void openSearchResult(int category, String content) {
        FragmentManager fragmentManager = getChildFragmentManager();
        Fragment fragment = fragmentManager
                .findFragmentByTag(FragmentEnum.SEARCH_RESULT.tag);
        KanjiSearchResultFragment sf = KanjiSearchResultFragment.newInstance(category, content);

        FragmentTransaction ft = fragmentManager.beginTransaction();
        if (fragment != null) {
            ft.replace(R.id.fragment_home_container, sf, FragmentEnum.SEARCH_RESULT.tag);
        } else {
            ft.add(R.id.fragment_home_container, sf, FragmentEnum.SEARCH_RESULT.tag);
        }
        ft.addToBackStack(null);
        ft.commit();
    }

    public void openBasicSetResult() {
        FragmentManager fragmentManager = getChildFragmentManager();
        Fragment fragment = fragmentManager
                .findFragmentByTag(FragmentEnum.BASIC_SET.tag);
        BasicSetFragment bsf = BasicSetFragment.newInstance();

        FragmentTransaction ft = fragmentManager.beginTransaction();
        if (fragment != null) {
            ft.replace(R.id.fragment_home_container, bsf, FragmentEnum.BASIC_SET.tag);
        } else {
            ft.add(R.id.fragment_home_container, bsf, FragmentEnum.BASIC_SET.tag);
        }
        ft.addToBackStack(null);
        ft.commit();
    }


    @Override
    public void initView() {
//        mKeywordText = (EditText) mView.findViewById(R.id.home_keyword_edit_text);
//        mBtnSearch = (Button) mView.findViewById(R.id.home_search_button);
//        String keyword = mKeywordText.getText().toString();

        TextView[] btnCategories = new TextView[] {
//                (TextView) mView.findViewById(R.id.home_search_button),
                (TextView) mView.findViewById(R.id.home_jlpt_n1_button),
                (TextView) mView.findViewById(R.id.home_jlpt_n2_button),
                (TextView) mView.findViewById(R.id.home_jlpt_n3_button),
                (TextView) mView.findViewById(R.id.home_jlpt_n4_button),
                (TextView) mView.findViewById(R.id.home_jlpt_n5_button),
                (TextView) mView.findViewById(R.id.home_radical_button),
//                (TextView) mView.findViewById(R.id.home_vocabulary_jlpt_button),
//                (TextView) mView.findViewById(R.id.home_image_button),
//                (TextView) mView.findViewById(R.id.home_test_button),
        };

        View[] layoutCategories = new View[]{
//                (TextView) mView.findViewById(R.id.home_search_button),
                mView.findViewById(R.id.home_jlpt_n1_layout),
                mView.findViewById(R.id.home_jlpt_n2_layout),
                mView.findViewById(R.id.home_jlpt_n3_layout),
                mView.findViewById(R.id.home_jlpt_n4_layout),
                mView.findViewById(R.id.home_jlpt_n5_layout),
                mView.findViewById(R.id.home_radical_layout)
        };

        // Register click listener for all buttons
        for (int i = 0; i < btnCategories.length; i++) {
            final int index = i + 1;
            final String title = btnCategories[i].getText().toString();
            layoutCategories[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().setTitle(title);
                    CommonSharedPreferencesManager.savePreference(getContext(), "backUpTitle", title);
                    if (index > 0 && index <= 5) {
                        updateSelectedJlptTyes(index);
                        openSearchResult(index, null);
                    } else {
                        if (index == 6) {
                            openBasicSetResult();
                        }
                    }
                }
            });
        }

        View homeTestButton = mView.findViewById(R.id.home_jlpt_test_layout);
        homeTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), KanjiTestHomeActivity.class);
                startActivity(intent);
            }
        });
    }

    private void updateSelectedJlptTyes(int category) {
        String jlptTypes = CommonSharedPreferencesManager.loadPreference(
                getContext(), Constant.SELECTED_JLPT_TYPE_LIST_KEYS, "N5");

        Log.d(TAG, Constant.SELECTED_JLPT_TYPE_LIST_KEYS + ":" + jlptTypes);

        String newJlptType= "N" + category;
        if (!jlptTypes.contains(newJlptType) && !newJlptType.equals("N1")) {
            CommonSharedPreferencesManager.savePreference(
                    getContext(), Constant.SELECTED_JLPT_TYPE_LIST_KEYS,
                    jlptTypes + Constant.SEMI_COLON + newJlptType);
        }
    }
}
