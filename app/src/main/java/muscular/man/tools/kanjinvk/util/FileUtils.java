package muscular.man.tools.kanjinvk.util;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by khanhnv10 on 04/03/2016.
 */
public class FileUtils extends File {
    private static final String TAG = FileUtils.class.getSimpleName();

    public FileUtils(File dir, String name) {
        super(dir, name);
    }

    public FileUtils(String path) {
        super(path);
    }

    public FileUtils(String dirPath, String name) {
        super(dirPath, name);
    }

    public FileUtils(URI uri) {
        super(uri);
    }

    public static List<String> readFile(Context ctx, String name) throws IOException {
        List<String> dataList = new ArrayList<>();
        InputStream is = ctx.getAssets().open(name);
        BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        String line;
        while ((line = br.readLine()) != null) {
            dataList.add(line);
        }

        br.close();
        return dataList;
    }
}
