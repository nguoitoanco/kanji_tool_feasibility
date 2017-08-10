package muscular.man.tools.kanjinvk.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import muscular.man.tools.kanjinvk.R;
import muscular.man.tools.kanjinvk.common.CommonActionListener;
import muscular.man.tools.kanjinvk.common.CommonSharedPreferencesManager;
import muscular.man.tools.kanjinvk.model.constant.Constant;
import muscular.man.tools.kanjinvk.model.dto.KanjiBlockDto;
import muscular.man.tools.kanjinvk.model.dto.KanjiDto;
import muscular.man.tools.kanjinvk.model.enums.SizeEnum;
import muscular.man.tools.kanjinvk.model.enums.ViewListType;
import muscular.man.tools.kanjinvk.util.ViewUtils;


/**
 * Created by KhanhNV10 on 2015/11/23.
 */
public class KanjiSearchResultAdapter extends RecyclerView.Adapter<KanjiSearchResultAdapter.ViewHolder>
                                    implements Filterable {

    private ViewListType mViewListType;
    private List<KanjiDto> mWordList;
    private List<KanjiDto> mWordFilterList;
    private List<KanjiBlockDto> mBlockDtoList;
    private List<KanjiBlockDto> mBlockFilterList;
    private Context mContext;
    private CommonActionListener.OnclickItemListener<List<KanjiDto>> mCallBack;

    private boolean contentIsEnglish = true;
    private int mNumColumn = SizeEnum.RATE_FOUR.rate;


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView indexView;
        public TextView wordView;
        public TextView onView;
        public TextView kunView;
        public TextView meanView;
        public TextView storyView;
        public LinearLayout infoLayout;

        public ViewHolder(View itemView, ViewListType type) {
            super(itemView);
            if (type.isBlockType()) {
                wordView = (TextView) itemView.findViewById(R.id.kanji_block_text_view);
                indexView = (TextView) itemView.findViewById(R.id.kanji_block_index);
            } else if (type.isGridType()) {
                wordView = (TextView) itemView.findViewById(R.id.kanji_grid_text_view);
            } else {
                infoLayout = (LinearLayout)itemView.findViewById(R.id.kanji_info_layout);
                wordView = (TextView) itemView.findViewById(R.id.kanji_detail_text_view);
                onView = (TextView) itemView.findViewById(R.id.kanji_on_text_view);
                kunView = (TextView) itemView.findViewById(R.id.kanji_kun_text_view);
                meanView = (TextView) itemView.findViewById(R.id.kanji_mean_text_view);
            }
        }
    }


    public KanjiSearchResultAdapter(List<KanjiDto> words, ViewListType viewListType, Context context,
                                    int numColumn, CommonActionListener.OnclickItemListener<List<KanjiDto>> callBack) {
        mViewListType = viewListType;
        mWordList = words;
        mContext = context;
        mCallBack = callBack;
        mWordFilterList = new ArrayList<>(mWordList);
        contentIsEnglish = CommonSharedPreferencesManager
                .loadBooleanPreference(context, Constant.CONTENT_LANGUAGE_KEY, true);

        mNumColumn = numColumn;
    }

    public KanjiSearchResultAdapter(List<KanjiBlockDto> dtos, ViewListType viewListType, Context context,
                                    CommonActionListener.OnclickItemListener<List<KanjiDto>> callBack, int index) {
        mViewListType = viewListType;
        mBlockDtoList = dtos;
        mContext = context;
        mCallBack = callBack;
        mBlockFilterList = new ArrayList<>(dtos);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (mViewListType.isBlockType()) {
            view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.fragment_kanji_search_result_block_item, parent, false);
        } else if (mViewListType.isGridType()) {
            view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.fragment_kanji_search_result_grid_item, parent, false);
        } else {
            view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.fragment_kanji_search_result_detail_item, parent, false);
        }

        return new ViewHolder(view, mViewListType);
    }

    @Override
    public void onBindViewHolder(final ViewHolder vh, final int position) {
        setViewContent(vh, position);
    }

    private void setViewContent(final ViewHolder vh, final int pos) {
        if (mViewListType.isBlockType()) {
            final KanjiBlockDto blockDto = mBlockDtoList.get(pos);
            vh.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallBack.onSuccess(blockDto.getKanjiDtos(), 0);
                }
            });

            StringBuilder builder = new StringBuilder();
            for (KanjiDto dto : blockDto.getKanjiDtos()) {
                builder.append(dto.word).append("   ");
            }

            String index = String.format(Locale.getDefault(), "%02d", (pos + 1));
            vh.indexView.setText(index);
            vh.wordView.setText(builder.toString());
            Log.d("KanjiSearchResult", builder.toString());
//            vh.itemView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
////                    ViewUtils.setSquareSizeOnDevice(vh.indexView, mContext, SizeEnum.RATE_TEN.rate);
//                    vh.indexView.setHeight(vh.wordView.getHeight());
//                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
//                        vh.itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                    } else {
//                        vh.itemView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                    }
//                }
//            });

            vh.itemView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    vh.indexView.setHeight(vh.wordView.getHeight());
                    vh.itemView.getViewTreeObserver().removeOnScrollChangedListener(this);
                }
            });
        } else {
            final KanjiDto dto = mWordList.get(pos);
            vh.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallBack.onSuccess(mWordList, pos);
                }
            });

            if (mViewListType.isGridType()) {
                vh.wordView.setText(dto.word);
                ViewUtils.setSquareSizeOnWidthDevice(vh.itemView, mContext, mNumColumn);
//                ViewUtils.setSquareSizeOnDevice(vh.wordView, mContext, mNumColumn);

            } else {
                vh.wordView.setText(dto.word.trim());
                vh.onView.setText(dto.onyomi.trim());
                vh.kunView.setText(dto.kuniomi.trim());
                if (contentIsEnglish) {
                    vh.meanView.setText(dto.enMean.trim());
                } else {
                    vh.meanView.setText(dto.vnMean.trim());
                }

//                if (isFirstStart) {
//                    vh.itemView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
//                        @Override
//                        public void onScrollChanged() {
//                            if (vh.infoLayout.getHeight() >= vh.wordView.getHeight()) {
//                                vh.wordView.setHeight(vh.infoLayout.getHeight());
//                            } else {
//                                vh.infoLayout.setMinimumHeight(vh.wordView.getHeight());
//                            }
//                            vh.wordView.setGravity(Gravity.CENTER);
//                            vh.itemView.getViewTreeObserver().removeOnScrollChangedListener(this);
//                        }
//                    });
//                }
//
//                if (pos == mWordList.size() - 1 && isFirstStart) {
//                    isFirstStart = false;
//                }
            }
        }
    }
    @Override
    public int getItemCount() {
        if (mViewListType.isBlockType()) {
            return mBlockDtoList.size();
        }

        if (mWordList == null) return 0;

        return mWordList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                constraint = constraint.toString().toLowerCase();
                FilterResults result = new FilterResults();

                if (constraint.toString().length() > 0) {
                    List<KanjiDto> founded = new ArrayList<>();
                    for (KanjiDto item : mWordFilterList) {
                        if (item.onyomi.trim().toLowerCase().contains(constraint)
                                || item.enOnyomi.trim().toLowerCase().contains(constraint)
                                || item.kuniomi.trim().toLowerCase().contains(constraint)
                                || item.enKuniomi.trim().toLowerCase().contains(constraint)
                                || item.enMean.trim().toLowerCase().contains(constraint)
                                || item.vnMean.trim().toLowerCase().contains(constraint)
                                || item.word.trim().toLowerCase().contains(constraint)
                                ) {
                            founded.add(item);
                        }
                    }

                    result.values = founded;
                    result.count = founded.size();
                } else {
                    result.values = mWordFilterList;
                    result.count = mWordFilterList.size();
                }
                return result;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mWordList = (ArrayList<KanjiDto>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
