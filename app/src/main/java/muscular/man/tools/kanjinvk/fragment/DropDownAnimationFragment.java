package muscular.man.tools.kanjinvk.fragment;


import static muscular.man.tools.kanjinvk.common.CommonActionListener.CallBackListener;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import muscular.man.tools.kanjinvk.R;
import muscular.man.tools.kanjinvk.model.enums.Categories;

/**
 * A simple {@link Fragment} subclass.
 */
public class DropDownAnimationFragment extends Fragment {

    private CallBackListener<Integer> callBackListener;

    public enum DropDown {
        SHOW_OPTION(1),
        HIDE_OPTION(0);

        private int type;

        DropDown( int type) {
            this.type = type;
        }

        private int toInt() {
            return type;
        }
        public boolean isShown() {
            return type == DropDown.SHOW_OPTION.toInt();
        }
    }

    public enum OptionView {
        JLPT_N1(R.id.jlpt_n1_option, R.string.jlpt_n1_text, "N1%"),
        JLPT_N2(R.id.jlpt_n2_option, R.string.jlpt_n2_text, "N2%"),
        JLPT_N3(R.id.jlpt_n3_option, R.string.jlpt_n3_text, "N3%"),
        JLPT_N4(R.id.jlpt_n4_option, R.string.jlpt_n4_text, "N4%"),
        JLPT_N5(R.id.jlpt_n5_option, R.string.jlpt_n5_text, "N5%");

        private int id;
        private int idTextDisplay;
        private String keySearch;

        OptionView(int id, int textDisplay, String keySearch) {
            this.id = id;
            this.idTextDisplay = textDisplay;
            this.keySearch = keySearch;
        }

        public int getId() {
            return id;
        }

        public int getTextDisplay() {
            return idTextDisplay;
        }

        public String getKeySearch() {
            return keySearch;
        }
    }

    private  View view;


    public DropDownAnimationFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_drop_down_animation, container, false);
        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.main_slid_down);
        view.findViewById(R.id.main_anim).startAnimation(anim);
        view.findViewById(R.id.redundant).setVisibility(View.GONE);

        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.findViewById(R.id.redundant).setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        registerViewOnClickListener();
        return view;
    }

    private void registerViewOnClickListener() {
        for (int i = 0; i < OptionView.values().length; i++) {
            final TextView textView = (TextView) view
                    .findViewById(OptionView.values()[i].getId());
            final int index = i;
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callBackListener.onSuccess(index);
                    getFragmentManager().popBackStack();
                }
            });
        }
    }

    public void setServiceCallBackListener(CallBackListener<Integer> callBack) {
        callBackListener = callBack;
    }

    @Override
    public void onPause() {
        view.findViewById(R.id.redundant).setVisibility(View.GONE);
        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.main_slid_up);
        view.startAnimation(anim);
        super.onPause();
    }
}
