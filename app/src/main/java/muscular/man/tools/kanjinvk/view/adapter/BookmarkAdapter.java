package muscular.man.tools.kanjinvk.view.adapter;

import java.util.ArrayList;
import java.util.List;

import muscular.man.tools.kanjinvk.R;
import muscular.man.tools.kanjinvk.common.CommonActionListener;
import muscular.man.tools.kanjinvk.common.CommonSharedPreferencesManager;
import muscular.man.tools.kanjinvk.model.constant.Constant;
import muscular.man.tools.kanjinvk.model.dto.KanjiBlockDto;
import muscular.man.tools.kanjinvk.model.dto.KanjiDto;
import muscular.man.tools.kanjinvk.model.enums.SizeEnum;
import muscular.man.tools.kanjinvk.model.enums.ViewListType;
import muscular.man.tools.kanjinvk.util.ViewUtils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * Created by KhanhNV10 on 2015/11/23.
 */
public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ViewHolder> {

    private List<KanjiDto> mBookmarkList;
    private Context mContext;
    private CommonActionListener.OnclickItemListener<List<KanjiDto>> mCallBack;

    private boolean contentIsEnglish = true;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView indexView;
        public TextView wordView;
        public TextView onView;
        public TextView kunView;
        public TextView meanView;
        public LinearLayout infoLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            infoLayout = (LinearLayout)itemView.findViewById(R.id.kanji_info_layout);
            wordView = (TextView) itemView.findViewById(R.id.kanji_detail_text_view);
            onView = (TextView) itemView.findViewById(R.id.kanji_on_text_view);
            kunView = (TextView) itemView.findViewById(R.id.kanji_kun_text_view);
            meanView = (TextView) itemView.findViewById(R.id.kanji_mean_text_view);
        }
    }


    public BookmarkAdapter(List<KanjiDto> words, Context context,
                           CommonActionListener.OnclickItemListener<List<KanjiDto>> callBack) {
        mBookmarkList = words;
        mContext = context;
        mCallBack = callBack;
        contentIsEnglish = CommonSharedPreferencesManager.loadBooleanPreference(context, Constant.CONTENT_LANGUAGE_KEY, true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.fragment_kanji_search_result_detail_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder vh, final int position) {
        setViewContent(vh, position);
    }

    private void setViewContent(final ViewHolder vh, final int pos) {
        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallBack.onSuccess(mBookmarkList, pos);
            }
        });

        final KanjiDto dto = mBookmarkList.get(pos);
        vh.itemView.setLongClickable(true);
        vh.wordView.setText(dto.word);
        vh.onView.setText(dto.onyomi);
        vh.kunView.setText(dto.kuniomi);

        if (contentIsEnglish) {
            vh.meanView.setText(dto.enMean.trim());
        } else {
            vh.meanView.setText(dto.vnMean.trim());
        }

        vh.itemView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (vh.infoLayout.getHeight() >= vh.wordView.getHeight()) {
                    vh.wordView.setHeight(vh.infoLayout.getHeight());
                } else {
                    vh.infoLayout.setMinimumHeight(vh.wordView.getHeight());
                }
                vh.wordView.setGravity(Gravity.CENTER);
                vh.itemView.getViewTreeObserver().removeOnScrollChangedListener(this);
            }
        });
    }
    @Override
    public int getItemCount() {
        return mBookmarkList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
