package muscular.man.tools.kanjinvk.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import muscular.man.tools.kanjinvk.util.StringUtils;

/**
 * Created by KhanhNV10 on 2015/12/01.
 */
public class CommonSharedPreferencesManager {

    public static final String FAV_NUMBER = "fav_number";

    public static void savePreference(Context context,String name, String value)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        if (name != null && value != null)
        {
            editor.putString(name, value);
        }
        editor.apply();

    }

    public static String loadPreference(Context context, String name, String valueDefault) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(name, valueDefault);
    }

    public static void saveIntPreference(Context context, String name, int value)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        if (name != null)
        {
            editor.putInt(name, value);
        }
        editor.apply();

    }

    public static int loadIntPreference(Context context, String name, int defaultValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(name, defaultValue);
    }

    public static void saveBooleanPreference(Context context,String name, Boolean value)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        if (name != null && value != null)
        {
            editor.putBoolean(name, value);
        }
        editor.apply();

    }

//    public static int loadIntPreference(Context context, String name) {
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
//        return Integer.parseInt(prefs.getString(name, StringUtils.DEFAULT));
//    }

    public static Boolean loadBooleanPreference(Context context, String name, boolean bDefault) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(name, bDefault);
    }

    public static String updatePreference(Context context, String name, boolean isIncrease) {
        String reBefore = loadPreference(context, name, StringUtils.DEFAULT);
        int reBeforeNum = Integer.parseInt(reBefore);
        if (isIncrease) {
            reBeforeNum++;
        } else if (reBeforeNum > 0) {
            reBeforeNum--;
        }

        savePreference(context, name, String.valueOf(reBeforeNum));
        return String.valueOf(reBeforeNum);
    }

}
