package muscular.man.tools.kanjinvk.task;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import muscular.man.tools.kanjinvk.model.dto.KanjiTestDto;
import muscular.man.tools.kanjinvk.util.FileUtils;
import muscular.man.tools.kanjinvk.util.StringUtils;

import static muscular.man.tools.kanjinvk.common.CommonActionListener.CallBackAfterFinishTaskListener;

/**
 * Created by KhanhNV10 on 08/03/2016.
 */
public class GetKanjiTestListFromFileTask extends AsyncTask<Integer, Void, List<KanjiTestDto>> {
    private static final String TAG = GetKanjiTestListFromFileTask.class.getSimpleName();

    private CallBackAfterFinishTaskListener<List<KanjiTestDto>> mCallBack;
    private String mUrl;
    private Context mContext;

    public GetKanjiTestListFromFileTask(String url, Context ctx, CallBackAfterFinishTaskListener<List<KanjiTestDto>> mCallBack) {
        mUrl = url;
        mContext = ctx;
        this.mCallBack = mCallBack;
    }

    @Override
    protected List<KanjiTestDto> doInBackground(Integer... params) {
        List<KanjiTestDto> kanjiTestDtos = new ArrayList<>();
        try {
            if (!StringUtils.isEmpty(mUrl)) {
                List<String> dataList = FileUtils.readFile(mContext, mUrl);
                for (int i = 0; i < dataList.size(); i++) {
                    kanjiTestDtos.add(new KanjiTestDto(dataList.get(i)));
                }
            }
        } catch (IOException e) {
            mCallBack.onError("Can not load data!");
        }
        return kanjiTestDtos;
    }

    @Override
    protected void onPostExecute(List<KanjiTestDto> kanjiTestDtos) {
        mCallBack.onSuccess(kanjiTestDtos);
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
