package muscular.man.tools.kanjinvk.view.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import muscular.man.tools.kanjinvk.fragment.KanjiDetailFragment;
import muscular.man.tools.kanjinvk.model.dto.KanjiDto;

import static muscular.man.tools.kanjinvk.common.CommonActionListener.CallBackListener;

/**
 * Created by KhanhNV10 on 2015/11/23.
 */
public class KanjiDetailPagerAdapter extends FragmentStatePagerAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflate;
    private List<KanjiDto> mKanjiDtoList;
    private CallBackListener mCallBack;

    public KanjiDetailPagerAdapter(FragmentManager fm, List<KanjiDto> dtos, CallBackListener callBack) {
        super(fm);
        this.mKanjiDtoList = dtos;
        mCallBack = callBack;
    }

    SparseArray<Fragment> registeredFragments = new SparseArray<>();

    @Override
    public Fragment getItem(int position) {
        KanjiDetailFragment fragment = KanjiDetailFragment.newInstance(mCallBack);
        Bundle bundle = new Bundle();
        bundle.putParcelable("kanjiDto", mKanjiDtoList.get(position));
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }

    public SparseArray<Fragment> getCurrentFragments() {
        return registeredFragments;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
        Log.d("KanjiDetailPagerAdapter", "" + registeredFragments.size());
    }

    @Override
    public int getCount() {
        return mKanjiDtoList.size();
    }

}
