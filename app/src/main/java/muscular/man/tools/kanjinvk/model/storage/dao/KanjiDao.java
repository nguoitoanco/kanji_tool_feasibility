package muscular.man.tools.kanjinvk.model.storage.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.List;

import muscular.man.tools.kanjinvk.common.CommonActionListener.CallBackListener;
import muscular.man.tools.kanjinvk.common.handle.DaoAsyncQueryHandler;
import muscular.man.tools.kanjinvk.model.dto.KanjiDto;
import muscular.man.tools.kanjinvk.model.enums.BookmarkEnum;
import muscular.man.tools.kanjinvk.model.storage.columns.KanjiColumn;
import muscular.man.tools.kanjinvk.model.storage.provider.KanjiContentProvider;
import muscular.man.tools.kanjinvk.util.TableUtils;


/**
 * Created by KhanhNV10 on 2015/12/25.
 */
public class KanjiDao {
    private static final String TAG = KanjiDao.class.getSimpleName();
    private Context mContext;
    DaoAsyncQueryHandler<KanjiDto> queryHandler;

    public KanjiDao(Context context) {
        this.mContext = context;

    }

    /**
     * Get kanji list list from DB.
     * After finish query, callback to update UI
     */
    public void getKanjiInfoList(String key, boolean isOnlyBookMarked, final CallBackListener<List<KanjiDto>> callBack) {
        close();
        ContentResolver cr = mContext.getContentResolver();
        queryHandler = new DaoAsyncQueryHandler<>(cr);

        queryHandler.setLoaderTaskHandler(new DaoAsyncQueryHandler.LoaderTaskHandler<KanjiDto>() {
            @Override
            public KanjiDto createEntity(int i, Cursor cursor) {
                KanjiColumn kc = new KanjiColumn();
                TableUtils.set(kc, cursor);
//                KanjiDto dto = new KanjiDto(kc);
                return new KanjiDto(kc);
            }
        });

        queryHandler.setOnQueryCompleteListener(
                new DaoAsyncQueryHandler.OnQueryCompleteListener<List<KanjiDto>>() {
                    @Override
                    public void onQueryComplete(int i, List<KanjiDto> dtos) {
                        callBack.onSuccess(dtos);
                    }
                });

        String selection = "id LIKE ?";
        String[] selectionArgs = new String[]{key};
        if (isOnlyBookMarked) {
            selection = "is_bookmarked='1'";
            selectionArgs = null;
        }
        String[] columns = new String[] {"id", "word", "onyomi", "kuniomi", "en_onyomi", "en_kuniomi", "en_mean", "vn_mean"};
        queryHandler.loadContents(0, null, KanjiContentProvider.KANJI_URI,
                columns, selection, selectionArgs, "id");
    }


    /**
     * Get kanji list list from DB.
     * After finish query, callback to update UI
     */
    public void getKanjiInfoList(String key, int start, int amount, boolean isOnlyBookMarked, final CallBackListener<List<KanjiDto>> callBack) {
        ContentResolver cr = mContext.getContentResolver();
        queryHandler = new DaoAsyncQueryHandler<>(cr);

        queryHandler.setLoaderTaskHandler(new DaoAsyncQueryHandler.LoaderTaskHandler<KanjiDto>() {
            @Override
            public KanjiDto createEntity(int i, Cursor cursor) {
                KanjiColumn kc = new KanjiColumn();
                TableUtils.set(kc, cursor);
//                KanjiDto dto = new KanjiDto(kc);
                return new KanjiDto(kc);
            }
        });

        queryHandler.setOnQueryCompleteListener(
                new DaoAsyncQueryHandler.OnQueryCompleteListener<List<KanjiDto>>() {
            @Override
            public void onQueryComplete(int i, List<KanjiDto> dtos) {
                callBack.onSuccess(dtos);
            }
        });

        String selection = "id LIKE ?";
        String[] selectionArgs = new String[]{key};
        if (isOnlyBookMarked) {
            selection = "is_bookmarked='1'";
            selectionArgs = null;
        }
        String[] columns = new String[] {"id", "word", "onyomi", "kuniomi", "en_onyomi", "en_kuniomi", "en_mean", "vn_mean"};
        queryHandler.loadContents(0, null, KanjiContentProvider.KANJI_URI,
                columns, selection, selectionArgs, "id LIMIT  + " + amount + " OFFSET + " + start);
    }

    /**
     * Get kanji list list from DB.
     * After finish query, callback to update UI
     */
    public void getKanjiDetailList(List<String> kanjiIds, final CallBackListener<List<KanjiDto>> callBack) {
        close();
        ContentResolver cr = mContext.getContentResolver();
        queryHandler = new DaoAsyncQueryHandler<>(cr);

        queryHandler.setLoaderTaskHandler(new DaoAsyncQueryHandler.LoaderTaskHandler<KanjiDto>() {
            @Override
            public KanjiDto createEntity(int i, Cursor cursor) {
                KanjiColumn kc = new KanjiColumn();
                TableUtils.set(kc, cursor);
//                KanjiDto dto = new KanjiDto(kc);
                return new KanjiDto(kc);
            }
        });

        queryHandler.setOnQueryCompleteListener(
                new DaoAsyncQueryHandler.OnQueryCompleteListener<List<KanjiDto>>() {
                    @Override
                    public void onQueryComplete(int i, List<KanjiDto> dtos) {
                        callBack.onSuccess(dtos);
                    }
                });

        String[] keys = new String[kanjiIds.size()];
        keys = kanjiIds.toArray(keys);

        StringBuilder sb = new StringBuilder("id IN (");
        for (String key : keys) {
            sb.append("'").append(key).append("'").append(",");
        }
        sb.append(")");
        String selection =sb.toString().replace(",)", ")");

        String[] columns = TableUtils.getColumns(KanjiColumn.class);
        queryHandler.loadContents(0, null, KanjiContentProvider.KANJI_URI,
                columns, selection, null, "id");
    }

    /**
     * This method insert a shop info into database.
     *
     * @param dto kanjiDto
     */
    public void updateKanjiBookmark(KanjiDto dto, boolean bookmarkFlg, final CallBackListener<Boolean> callBack) {
        ContentResolver cr = mContext.getContentResolver();
        DaoAsyncQueryHandler queryHandler = new DaoAsyncQueryHandler(cr);
        ContentValues contentValues = new ContentValues();
        if (bookmarkFlg) {
            contentValues.put("is_bookmarked", BookmarkEnum.IS_BOOKMARKED.toString());
        } else {
            contentValues.put("is_bookmarked", BookmarkEnum.IS_NOT_BOOKMARKED.toString());
        }

        queryHandler.setOnUpdateCompleteListener(new DaoAsyncQueryHandler.OnUpdateCompleteListener() {
            @Override
            public void onUpdateComplete(int var1, int var2) {
                if (var2 > 0) {
                    callBack.onSuccess(true);
                } else {
                    callBack.onSuccess(false);
                }
            }
        });

        String selection ="id='" + dto.kid + "'";
        queryHandler.startUpdate(0, null, KanjiContentProvider.KANJI_URI, contentValues, selection, null);
    }

    /**
     * This method insert a shop info into database.
     *
     * @param dtos List<kanjiDto>
     */
    public void removeKanjiBookmarkList(List<KanjiDto> dtos, final CallBackListener<Boolean> callBack) {
        close();
        ContentResolver cr = mContext.getContentResolver();
        DaoAsyncQueryHandler queryHandler = new DaoAsyncQueryHandler(cr);


        queryHandler.setOnUpdateCompleteListener(new DaoAsyncQueryHandler.OnUpdateCompleteListener() {
            @Override
            public void onUpdateComplete(int var1, int var2) {
                if (var2 > 0) {
                    callBack.onSuccess(true);
                } else {
                    callBack.onSuccess(false);
                }
            }
        });

        StringBuilder sb = new StringBuilder("id IN (");
        for (KanjiDto dto : dtos) {
            sb.append("'").append(dto.kid).append("'").append(",");
        }
        sb.append(")");
        String selection =sb.toString().replace(",)", ")");

        ContentValues contentValues = new ContentValues();
        contentValues.put("is_bookmarked", BookmarkEnum.IS_NOT_BOOKMARKED.toString());

        queryHandler.startUpdate(0, null, KanjiContentProvider.KANJI_URI, contentValues, selection, null);

    }

    /**
     * This method delete all shop in Shop table.
     *
     */
    public void deleteAll(final CallBackListener<Boolean> callBack) {
        close();
        ContentResolver cr = mContext.getContentResolver();
        DaoAsyncQueryHandler queryHandler = new DaoAsyncQueryHandler(cr);
        queryHandler.setOnDeleteCompleteListener(new DaoAsyncQueryHandler.OnDeleteCompleteListener() {
            @Override
            public void onDeleteComplete(int i, int i1) {
                callBack.onSuccess(null);
            }
        });
        queryHandler.startDelete(0, null, KanjiContentProvider.KANJI_URI, null, null);
    }

    /**
     * This method delete all shop in Shop table.
     *
     */
    public void delete(KanjiDto dto, final CallBackListener<Boolean> callBack) {
        ContentResolver cr = mContext.getContentResolver();
        DaoAsyncQueryHandler queryHandler = new DaoAsyncQueryHandler(cr);

        String selection ="id='" + dto.kid + "'";
        queryHandler.setOnDeleteCompleteListener(new DaoAsyncQueryHandler.OnDeleteCompleteListener() {
            @Override
            public void onDeleteComplete(int i, int i1) {
                callBack.onSuccess(null);
            }
        });

        queryHandler.startDelete(0, null, KanjiContentProvider.KANJI_URI, selection, null);
    }

    public void insertKanjiList(List<KanjiDto> dtos, final CallBackListener<Boolean> callBack) {
        ContentResolver cr = mContext.getContentResolver();
        close();
        DaoAsyncQueryHandler queryHandler = new DaoAsyncQueryHandler(cr);
        List<KanjiColumn> columns = KanjiColumn.getKanjiColums(dtos);
        List<ContentValues> contentValueList = null;
        try {
            contentValueList = TableUtils.getContentValueList(columns);
        } catch (IllegalAccessException e) {
            callBack.onSuccess(false);
        }

        if (contentValueList != null) {
            final int size = contentValueList.size();
            if (size > 0) {
                queryHandler.setOnInsertCompleteListener(
                        new DaoAsyncQueryHandler.OnInsertCompleteListener() {
                    @Override
                    public void onInsertComplete(int i, Uri uri) {
                        Log.d(TAG,"complete:" + i);
                        if (uri != null && i == (size - 1)) {
                            callBack.onSuccess(true);
                        }
                        else if (uri == null){
                            callBack.onSuccess(false);
                        }
                    }
                });

                for (int i = 0; i < contentValueList.size(); i++) {
                    queryHandler.startInsert(i, null, KanjiContentProvider.KANJI_URI,
                            contentValueList.get(i));
                }
            }
        }
    }

    public void getKanjiNotificationInfo(String strJlpt, final CallBackListener<KanjiDto> callBack) {
        ContentResolver cr = mContext.getContentResolver();
        close();
        queryHandler = new DaoAsyncQueryHandler<>(cr);

        queryHandler.setLoaderTaskHandler(new DaoAsyncQueryHandler.LoaderTaskHandler<KanjiDto>() {
            @Override
            public KanjiDto createEntity(int i, Cursor cursor) {
                KanjiColumn kc = new KanjiColumn();
                TableUtils.set(kc, cursor);
//                KanjiDto dto = new KanjiDto(kc);
                return new KanjiDto(kc);
            }
        });

        queryHandler.setOnQueryCompleteListener(new DaoAsyncQueryHandler.OnQueryCompleteListener<List<KanjiDto>>() {
            @Override
            public void onQueryComplete(int i, List<KanjiDto> dtos) {
                callBack.onSuccess(dtos.get(0));
            }
        });

        String whereClause = "id LIKE ?";
        String[] columns = new String[] {
                "id", "word", "onyomi", "kuniomi", "en_onyomi", "en_kuniomi", "en_mean", "vn_mean"
        };

        queryHandler.loadContents(0, null, KanjiContentProvider.KANJI_URI,
                columns, whereClause, new String[]{strJlpt}, "RANDOM() LIMIT 1");

    }

    public void getKanjiById(String id, final CallBackListener<KanjiDto> callBack) {
        ContentResolver cr = mContext.getContentResolver();
        close();
        queryHandler = new DaoAsyncQueryHandler<>(cr);

        queryHandler.setLoaderTaskHandler(new DaoAsyncQueryHandler.LoaderTaskHandler<KanjiDto>() {
            @Override
            public KanjiDto createEntity(int i, Cursor cursor) {
                KanjiColumn kc = new KanjiColumn();
                TableUtils.set(kc, cursor);
//                KanjiDto dto = new KanjiDto(kc);
                return new KanjiDto(kc);
            }
        });

        queryHandler.setOnQueryCompleteListener(new DaoAsyncQueryHandler.OnQueryCompleteListener<List<KanjiDto>>() {
            @Override
            public void onQueryComplete(int i, List<KanjiDto> dtos) {
                callBack.onSuccess(dtos.get(0));
            }
        });

        String whereClause = "id = ?";
        String[] columns = TableUtils.getColumns(KanjiColumn.class);

        queryHandler.loadContents(0, null, KanjiContentProvider.KANJI_URI,
                columns, whereClause, new String[]{id}, "RANDOM() LIMIT 1");

    }

    public void close() {
        if (queryHandler != null) {
            Log.d(TAG, "close query handler");
            queryHandler.cancelOperation(0);
        }
    }
}
