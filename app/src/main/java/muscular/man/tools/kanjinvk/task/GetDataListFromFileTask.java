package muscular.man.tools.kanjinvk.task;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.view.ViewPager;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import muscular.man.tools.kanjinvk.common.CommonActionListener;
import muscular.man.tools.kanjinvk.model.dto.KanjiDto;
import muscular.man.tools.kanjinvk.model.storage.entity.Kanji;
import muscular.man.tools.kanjinvk.util.FileUtils;
import muscular.man.tools.kanjinvk.util.StringUtils;
import muscular.man.tools.kanjinvk.view.adapter.KanjiDetailPagerAdapter;

import static muscular.man.tools.kanjinvk.common.CommonActionListener.*;

/**
 * Created by KhanhNV10 on 08/03/2016.
 */
public class GetDataListFromFileTask extends AsyncTask<Integer, Void, List<KanjiDto>> {
    private static final String TAG = GetDataListFromFileTask.class.getSimpleName();

    private CallBackAfterFinishTaskListener<List<KanjiDto>> mCallBack;
    private String mUrl;
    private Context mContext;

    public GetDataListFromFileTask(String url, Context ctx, CallBackAfterFinishTaskListener<List<KanjiDto>> mCallBack) {
        mUrl = url;
        mContext = ctx;
        this.mCallBack = mCallBack;
    }

    @Override
    protected List<KanjiDto> doInBackground(Integer... params) {
        List<KanjiDto> kanjiDtos = new ArrayList<>();
        try {
            if (!StringUtils.isEmpty(mUrl)) {
                List<String> dataList = FileUtils.readFile(mContext, mUrl);
                for (int i = 0; i < dataList.size(); i++) {
                    kanjiDtos.add(new KanjiDto(dataList.get(i)));
                }
            }
        } catch (IOException e) {
            mCallBack.onError("Can not load data!");
        }
        return kanjiDtos;
    }

    @Override
    protected void onPostExecute(List<KanjiDto> kanjiDtos) {
        mCallBack.onSuccess(kanjiDtos);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
