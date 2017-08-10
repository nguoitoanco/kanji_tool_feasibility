package muscular.man.tools.kanjinvk.view.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import muscular.man.tools.kanjinvk.R;
import muscular.man.tools.kanjinvk.model.dto.KanjiTestDto;

/**
 * Created by KhanhNV10 on 2015/11/23.
 */
public class OptionItemListAdapter extends
        RecyclerView.Adapter<OptionItemListAdapter.OptionItemViewHolder> {

    private List<KanjiTestDto> mKanjiTestDtos;
    private Context mContext;
    private OnOptionItemOnClickListener mListener;

    public class OptionItemViewHolder extends RecyclerView.ViewHolder {
        private View indexLayout;
        private TextView indexView;

        public OptionItemViewHolder(View itemView) {
            super(itemView);
            indexLayout = itemView.findViewById(R.id.option_item_layout);
            indexView = (TextView) itemView.findViewById(R.id.option_item_text_view);
        }
    }

    public OptionItemListAdapter(List<KanjiTestDto> dtos, Context context, OnOptionItemOnClickListener callback) {
        mKanjiTestDtos = dtos;
        mContext = context;
        mListener = callback;
    }

    @Override
    public OptionItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.option_text_view_item, parent, false);
        return new OptionItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final OptionItemViewHolder vh, final int position) {
            final KanjiTestDto dto = mKanjiTestDtos.get(position);

        vh.indexView.setText(String.valueOf(position + 1));
        if (dto.selectedItem >= 0) {
            vh.indexView.setTextColor(
                    ContextCompat.getColor(mContext, R.color.colorAccent));
        } else {
            vh.indexView.setTextColor(
                    ContextCompat.getColor(mContext, R.color.colorPrimary));
        }

        vh.indexLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onOptionItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mKanjiTestDtos.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public interface OnOptionItemOnClickListener {
        public void onOptionItemClick(int itemIndex);
    }
}
