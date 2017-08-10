package muscular.man.tools.kanjinvk.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import muscular.man.tools.kanjinvk.R;

/**
 * Created by KhanhNV10 on 2015/11/23.
 */
public class CompoundListAdapter extends
        RecyclerView.Adapter<CompoundListAdapter.OptionItemViewHolder> {

    private String[] mCompounds;

    public class OptionItemViewHolder extends RecyclerView.ViewHolder {
        private TextView compoundView;

        public OptionItemViewHolder(View itemView) {
            super(itemView);
            compoundView = (TextView) itemView.findViewById(R.id.kanji_compound_text_view);
        }
    }

    public CompoundListAdapter(String[] compounds) {
        mCompounds = compounds;
    }

    @Override
    public OptionItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.kanji_compound_item, parent, false);
        return new OptionItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final OptionItemViewHolder vh, final int position) {
        String cp = mCompounds[position];
        vh.compoundView.setText(Html.fromHtml(cp));
    }

    @Override
    public int getItemCount() {
        return mCompounds.length;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
