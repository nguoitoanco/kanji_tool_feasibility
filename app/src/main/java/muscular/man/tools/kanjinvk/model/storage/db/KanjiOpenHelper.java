package muscular.man.tools.kanjinvk.model.storage.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import muscular.man.tools.kanjinvk.model.storage.columns.KanjiColumn;
import muscular.man.tools.kanjinvk.model.storage.columns.KanjiTestColumn;
import muscular.man.tools.kanjinvk.util.TableUtils;

/**
 * Created by KhanhNV10 on 2015/12/25.
 */
public class KanjiOpenHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "kanji.db";

    private Context mContext;

    public KanjiOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Table(name as Shop) that mapping to ShopColumns Model
        TableUtils.createTable(db, KanjiColumn.class);
        TableUtils.createTable(db, KanjiTestColumn.class);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        if (newVersion > oldVersion) {
//            try {
//                TableUtils.deleteTable(db, KanjiColumn.class);
//                onCreate(db);
//            } catch (Exception e) {
//                CommonSharedPreferencesManager.saveBooleanPreference(mContext, "isDataError", true);
//            }
//            CommonSharedPreferencesManager.saveBooleanPreference(mContext, "isDataReset", true);
//        }
    }
}
