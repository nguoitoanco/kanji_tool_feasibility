package muscular.man.tools.kanjinvk.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import muscular.man.tools.kanjinvk.R;
import muscular.man.tools.kanjinvk.common.CommonActionListener.OnclickItemListener;
import muscular.man.tools.kanjinvk.model.dto.KanjiTestBlockDto;


/**
 * Created by KhanhNV10 on 2015/11/23.
 */
public class KanjiTestBlockResultAdapter extends RecyclerView.Adapter<KanjiTestBlockResultAdapter.ViewHolder> {

    private List<KanjiTestBlockDto> mBlockDtoList;
    private Context mContext;
    private OnclickItemListener<KanjiTestBlockDto> mCallBack;

    private boolean contentIsEnglish = true;


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView wordView;
        public ImageView moreView;
        public LinearLayout infoLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            wordView = (TextView) itemView.findViewById(R.id.kanji_test_block_text_view);
            moreView = (ImageView) itemView.findViewById(R.id.kanji_test_block_more_view);
        }
    }



    public KanjiTestBlockResultAdapter(
            List<KanjiTestBlockDto> dtos, Context context,
            OnclickItemListener<KanjiTestBlockDto> callBack
            ,View.OnCreateContextMenuListener moreCallBack ) {
        mBlockDtoList = dtos;
        mContext = context;
        mCallBack = callBack;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.fragment_kanji_test_block_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder vh, final int position) {
        setViewContent(vh, position);
    }

    private void setViewContent(final ViewHolder vh, final int pos) {
        final KanjiTestBlockDto blockDto = mBlockDtoList.get(pos);
        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallBack.onSuccess(blockDto, pos);
            }
        });

//        vh.moreView.setOnCreateContextMenuListener(onCreateContextMenuListener);
        vh.itemView.setLongClickable(true);
//        vh.moreView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showMoreCallBack.onShowMore(v, blockDto, pos);
//            }
//        });

        String title = mContext.getString(R.string.jlpt_test_block_title) +
                String.format(Locale.getDefault(), "%03d", (pos + 1));
        vh.wordView.setText(title);

        vh.itemView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                vh.itemView.getViewTreeObserver().removeOnScrollChangedListener(this);
            }
        });
    }
    @Override
    public int getItemCount() {
        return mBlockDtoList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

//    public interface ShowMoreItemListener<T, Integer> {
//        public void onShowMore(View view, T t, int pos);
//    }
}
