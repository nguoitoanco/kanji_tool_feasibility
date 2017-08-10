package muscular.man.tools.kanjinvk.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import static android.support.v7.widget.RecyclerView.*;

/**
 * Created by nguoitoanco on 2/24/2016.
 */
public class BaseAdapter implements Filterable {

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public Filter getFilter() {
        return null;
    }

}
