package muscular.man.tools.kanjinvk.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.View;

import muscular.man.tools.kanjinvk.view.BaseView;

/**
 * Created by khanhnv10 on 2016/02/25.
 */
public abstract class BaseFragment extends Fragment implements BaseView, SearchView.OnQueryTextListener {
    public View mView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
//        favouriteMovieAdapter.getFilter().filter(newText);
        return false;
    }
}
