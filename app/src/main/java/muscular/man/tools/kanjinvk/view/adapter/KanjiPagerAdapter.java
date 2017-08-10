package muscular.man.tools.kanjinvk.view.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;

import muscular.man.tools.kanjinvk.fragment.AboutFragment;
import muscular.man.tools.kanjinvk.fragment.BookmarkFragment;
import muscular.man.tools.kanjinvk.fragment.HomeFragment;
import muscular.man.tools.kanjinvk.model.enums.Tabs;

/**
 * Created by KhanhNV10 on 2015/11/21.
 */
public class KanjiPagerAdapter extends FragmentStatePagerAdapter {

    private Context mContext;

    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

    public KanjiPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }

    @Override
    public Fragment getItem(int position) {

        if (position == Tabs.HOME.getIndex()) {
            Log.d("KanjiPagerAdapter", "Home");
            return HomeFragment.newInstance();
        }

        if (position == Tabs.BOOKMARK.getIndex()) {
            Log.d("KanjiPagerAdapter", "BOOKMARK");
            return BookmarkFragment.newInstance();
        }

        if (position == Tabs.ABOUT.getIndex()) {
            Log.d("KanjiPagerAdapter", "ABOUT");
            return AboutFragment.newInstance();
        }

//        if (position == Tabs.MORE.getIndex()) {
//            return AboutFragment.newInstance();
//        }

        return HomeFragment.newInstance();
    }

    @Override
    public int getCount() {
        return Tabs.values().length;
    }


}
