package muscular.man.tools.kanjinvk.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import muscular.man.tools.kanjinvk.R;
import muscular.man.tools.kanjinvk.common.CommonActionListener;
import muscular.man.tools.kanjinvk.model.dto.KanjiDto;
import muscular.man.tools.kanjinvk.model.enums.SizeEnum;
import muscular.man.tools.kanjinvk.model.enums.ViewListType;
import muscular.man.tools.kanjinvk.util.ViewUtils;


/**
 * Created by KhanhNV10 on 2015/11/23.
 */
public class BasicSetSearchResultAdapter extends RecyclerView.Adapter<BasicSetSearchResultAdapter.ViewHolder> {

    private ViewListType mViewListType;
    private List<KanjiDto> mWordList;
    private Context mContext;
    private CommonActionListener.OnclickItemListener<List<KanjiDto>> mCallBack;

    private int mNumColumn = SizeEnum.RATE_FOUR.rate;
//    private boolean contentIsEnglish = true;


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView wordView;
        public TextView meanView;
        public View basicSetItemLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            wordView = (TextView) itemView.findViewById(R.id.basic_set_grid_text_view);
            meanView = (TextView) itemView.findViewById(R.id.basic_set_mean_text_view);
            basicSetItemLayout = itemView.findViewById(R.id.basic_set_item_layout);
        }
    }

    public BasicSetSearchResultAdapter(
            List<KanjiDto> words, Context context,
            int numColumn, CommonActionListener.OnclickItemListener<List<KanjiDto>> callBack) {
        mWordList = words;
        mContext = context;
        mCallBack = callBack;
        mNumColumn = numColumn;
//        contentIsEnglish = CommonSharedPreferencesManager.loadBooleanPreference(context, "ContentIsEnglish", true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.fragment_basic_set_search_result_grid_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder vh, final int position) {
        setViewContent(vh, position);
    }

    private void setViewContent(final ViewHolder vh, final int pos) {
        final KanjiDto dto = mWordList.get(pos);
        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallBack.onSuccess(mWordList, pos);
            }
        });

        vh.wordView.setText(dto.word);
        vh.meanView.setText(dto.vnMean);
        ViewUtils.setSquareSizeOnWidthDevice(vh.itemView, mContext, mNumColumn);
//        if (!contentIsEnglish) {
//            vh.meanView.setVisibility(View.VISIBLE);
//            vh.meanView.setText(dto.vnMean.trim());
//        } else {
//            vh.meanView.setVisibility(View.GONE);
//        }
    }

    @Override
    public int getItemCount() {
        if (mWordList == null) return 0;
        return mWordList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
