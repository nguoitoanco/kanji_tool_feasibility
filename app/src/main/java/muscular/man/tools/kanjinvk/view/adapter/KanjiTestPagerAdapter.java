package muscular.man.tools.kanjinvk.view.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import muscular.man.tools.kanjinvk.fragment.KanjiBlockListFragment;
import muscular.man.tools.kanjinvk.model.enums.JlptTabs;

/**
 * Created by KhanhNV10 on 2015/11/21.
 */
public class KanjiTestPagerAdapter extends FragmentStatePagerAdapter {


    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

    public KanjiTestPagerAdapter(FragmentManager fm) {
        super(fm);
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

        return KanjiBlockListFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return 5;
    }
}
