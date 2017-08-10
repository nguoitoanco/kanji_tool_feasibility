package muscular.man.tools.kanjinvk.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Created by nguoitoanco on 12/24/2015.
 */
public class ViewUtils {
    public static void setSquareSizeOnWidthDevice(View view, Context context, int rate) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float mWidth = displayMetrics.widthPixels;
        view.getLayoutParams().width = (int)(mWidth / rate);
        view.getLayoutParams().height = (int)(mWidth / rate);
    }

    public static void setSquareSizeOnWidthDevice(View view, Context context, int rate, int delta) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float mWidth = displayMetrics.widthPixels;
        view.getLayoutParams().width = (int)((mWidth + delta) / rate);
        view.getLayoutParams().height = (int)((mWidth + delta) / rate);
    }

    public static void setHeightByWidth(View view, Context context, int rate, int delta) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float mWidth = displayMetrics.widthPixels;
        view.getLayoutParams().height = (int)((mWidth + delta) / rate);
    }
}
