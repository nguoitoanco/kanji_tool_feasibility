package muscular.man.tools.kanjinvk.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.widget.ImageView;

/**
 * Created by nguoitoanco on 12/24/2015.
 */
public class ImageUtils {
    public static void setSquareSizeOnWidth(ImageView view, Context context, int rate) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float mWidth = displayMetrics.widthPixels;
        view.getLayoutParams().width = (int)(mWidth / rate);
        view.getLayoutParams().height = (int)(mWidth / rate);
    }
}
