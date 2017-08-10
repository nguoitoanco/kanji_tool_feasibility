package muscular.man.tools.kanjinvk.view.adapter;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.List;

import muscular.man.tools.kanjinvk.R;
import muscular.man.tools.kanjinvk.common.CommonActionListener.CallBackListener;
import muscular.man.tools.kanjinvk.model.dto.KanjiTestDto;


/**
 * Created by KhanhNV10 on 2015/11/23.
 */
public class KanjiTestMainAdapter extends RecyclerView.Adapter<KanjiTestMainAdapter.ViewHolder> implements View.OnClickListener {

    private Activity mContext;
    private List<KanjiTestDto> mKanjiTestDtos;

    private CallBackListener<Integer> mCallBack;

    private boolean isReset = false;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mInput;
        public RadioGroup radioGroup;

        public ViewHolder(View itemView) {
            super(itemView);
            mInput = (TextView) itemView.findViewById(R.id.kanji_test_input);
            radioGroup = (RadioGroup) itemView.findViewById(R.id.kanji_test_radio_group);
        }
    }

    public KanjiTestMainAdapter(List<KanjiTestDto> kanjiTestDtos, Activity context, boolean isReset, CallBackListener<Integer> callBack) {
        mContext = context;
        mKanjiTestDtos = kanjiTestDtos;
        mCallBack = callBack;
        this.isReset = isReset;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.kanji_test_main_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder vh, final int position) {
        setViewContent(vh, position);
    }

    private void setViewContent(final ViewHolder vh, final int pos) {
        final KanjiTestDto dto = mKanjiTestDtos.get(pos);

        String input = "<b>" + (pos + 1) + "</b>. "
                + dto.input.replace(dto.question.trim(),
                "<u>" + dto.question.trim() + "</u>");
        vh.mInput.setText(Html.fromHtml(input.trim()));

        String[] optionText = new String[] {
                dto.a.trim()
                ,dto.b.trim()
                ,dto.c.trim()
                ,dto.d.trim()
        };

        RadioButton[] radioButtons = new RadioButton[optionText.length];
        vh.radioGroup.removeAllViews();

        for (int i = 0; i < optionText.length; i++) {
            radioButtons[i] = new RadioButton(mContext);
            radioButtons[i].setId(Integer.parseInt(pos + "" + i));
            radioButtons[i].setText(optionText[i]);
//            radioButtons[i].setTextSize(mContext.getResources().getDimension(R.dimen.text_size_12));
            radioButtons[i].setTag(pos + "," + i);
            radioButtons[i].setOnClickListener(this);
            radioButtons[i].setChecked(i == dto.selectedItem);
            if (!isReset && i == Integer.parseInt(dto.aws.trim())) {
                radioButtons[i].setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
            } else {
                radioButtons[i].setTextColor(ContextCompat.getColor(mContext, R.color.colorBlackWhite));
            }
            if (!isReset) {
                radioButtons[i].setEnabled(false);
            }
            vh.radioGroup.addView(radioButtons[i]);

        }

    }

    @Override
    public int getItemCount() {
        if (mKanjiTestDtos == null) return 0;
        return mKanjiTestDtos.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onClick(View v) {
        String tag = (String) v.getTag();
        String[] params = tag.split(",");
        int pos = Integer.parseInt(params[0]);
        int option = Integer.parseInt(params[1]);

        int currentSelectedItem = mKanjiTestDtos.get(pos).selectedItem;
        if (currentSelectedItem < 0) {
            mCallBack.onSuccess(pos);
        }

        if (currentSelectedItem != option) {
            mKanjiTestDtos.get(pos).selectedItem = option;
        }
    }
}
