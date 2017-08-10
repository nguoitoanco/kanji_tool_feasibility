package muscular.man.tools.kanjinvk.util;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Created by KhanhNV10 on 01/08/2016.
 */
public class KanjiUtils {

    public static boolean isAppInstalled(Context ctx, String uri) {
        PackageManager pm = ctx.getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
