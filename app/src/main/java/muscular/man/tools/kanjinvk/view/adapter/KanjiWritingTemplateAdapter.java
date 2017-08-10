package muscular.man.tools.kanjinvk.view.adapter;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import muscular.man.tools.kanjinvk.R;
import muscular.man.tools.kanjinvk.model.dto.KanjiDto;
import muscular.man.tools.kanjinvk.model.enums.SizeEnum;
import muscular.man.tools.kanjinvk.util.StringUtils;
import muscular.man.tools.kanjinvk.util.ViewUtils;

/**
 * Created by KhanhNV10 on 2015/11/23.
 */
public class KanjiWritingTemplateAdapter extends
        RecyclerView.Adapter<KanjiWritingTemplateAdapter.OptionItemViewHolder> {

    private KanjiDto mKanjiDto;
    private Context mContext;
    private int config = Configuration.ORIENTATION_PORTRAIT;

    private boolean mIsTemplate = false;
//    private SelectItemListener mListener;

    public class OptionItemViewHolder extends RecyclerView.ViewHolder {
        private TextView wordView;

        public OptionItemViewHolder(View itemView) {
            super(itemView);
            wordView = (TextView) itemView.findViewById(R.id.kanji_template_view);
        }
    }

    public KanjiWritingTemplateAdapter(KanjiDto dto, Context context, boolean isTemplate) {
        mKanjiDto = dto;
        mContext = context;
        config = mContext.getResources().getConfiguration().orientation;
        mIsTemplate = isTemplate;
    }

    @Override
    public OptionItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.kanji_writing_template_item, parent, false);
        return new OptionItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final OptionItemViewHolder vh, final int position) {
        if (mIsTemplate) {
            vh.wordView.setText(mKanjiDto.word.trim());
        } else {
            vh.wordView.setText(StringUtils.EMPTY);
        }

        if (config == Configuration.ORIENTATION_LANDSCAPE) {
            ViewUtils.setSquareSizeOnWidthDevice(vh.itemView, mContext, SizeEnum.RATE_FIVE.rate, -40);
        } else {
            ViewUtils.setSquareSizeOnWidthDevice(vh.itemView, mContext, SizeEnum.RATE_TWO.rate, -30);
        }

//        vh.indexLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mListener.onSelectItem(position);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        int itemCount = 2;
        if (config == Configuration.ORIENTATION_LANDSCAPE) {
            itemCount = 4;
        }
        return itemCount;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
