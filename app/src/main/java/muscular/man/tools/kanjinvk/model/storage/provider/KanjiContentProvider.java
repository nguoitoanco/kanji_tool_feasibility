package muscular.man.tools.kanjinvk.model.storage.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import muscular.man.tools.kanjinvk.model.storage.columns.KanjiColumn;
import muscular.man.tools.kanjinvk.model.storage.columns.KanjiTestColumn;
import muscular.man.tools.kanjinvk.model.storage.db.KanjiOpenHelper;
import muscular.man.tools.kanjinvk.util.TableUtils;

/**
 * Created by KhanhNV10 on 2015/12/25.
 */
public class KanjiContentProvider extends ContentProvider {

    private static String TAG = KanjiContentProvider.class.getSimpleName();
    private static final String AUTHORITY = "muscular.man.tools.kanjinvk.model.storage.provider";

    private static final String TABLE_NAME_KANJI = "kanji";
    private static final String TABLE_NAME_KANJI_TEST = "kanji_test";

    public static final Uri KANJI_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME_KANJI);
    public static final Uri KANJI_TEST_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME_KANJI_TEST);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/kanjis";
    public static final String CONTENT_ITEM = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/kanji";

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Using for the Uri matcher
    private static final int KANJI_ID = 10;
    private static final int KANJI_TEST_ID = 11;

    static {
        sURIMatcher.addURI(AUTHORITY, TABLE_NAME_KANJI, KANJI_ID);
        sURIMatcher.addURI(AUTHORITY, TABLE_NAME_KANJI_TEST, KANJI_TEST_ID);
    }

    SQLiteDatabase db;
    // Database
    private KanjiOpenHelper openHelper;

    public KanjiContentProvider() {
    }


    @Override
    public boolean onCreate() {
        openHelper = new KanjiOpenHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@Nullable Uri uri, String[] projection, String selection, String[] selectionArgs,String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // Set the table
        queryBuilder.setTables(getTable(uri));


        db = openHelper.getReadableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        SimpleDateFormat sdf = new SimpleDateFormat("M月d日(E)", Locale.JAPAN);
        String date = sdf.format(new Date());
        return cursor;
    }


    @Nullable
    @Override
    public String getType(@Nullable Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        db = openHelper.getWritableDatabase();

        long rowId = db.insertWithOnConflict(getTable(uri), null, values, SQLiteDatabase.CONFLICT_REPLACE);
//        getContext().getContentResolver().notifyChange(uri, null);

        return Uri.parse(uri + "/" + rowId);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        db = openHelper.getWritableDatabase();
        if (selection == null) {
            return db.delete(getTable(uri), null , null);
        }
        return db.delete(getTable(uri), selection , null);
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        db = openHelper.getWritableDatabase();
        return db.update(getTable(uri),values, selection, null);
    }

    @Nullable
    @Override
    public Bundle call(@NonNull String method, String arg, Bundle extras) {
        return super.call(method, arg, extras);
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        return super.bulkInsert(uri, values);
    }

    private String getTable(Uri uri) {
        String table;
        switch (sURIMatcher.match(uri)) {
            case KANJI_ID:
                table = TableUtils.tableName(KanjiColumn.class);
                break;
            case KANJI_TEST_ID:
                table = TableUtils.tableName(KanjiTestColumn.class);
                break;
            default:
                table = TableUtils.tableName(KanjiColumn.class);
                break;
        }

        Log.d(TAG, table);
        return table;
    }

    public void close() {
        if (db != null) {
            db.close();
        }
    }
}
