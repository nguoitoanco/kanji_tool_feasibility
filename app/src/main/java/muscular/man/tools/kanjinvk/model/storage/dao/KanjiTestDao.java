package muscular.man.tools.kanjinvk.model.storage.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.List;

import muscular.man.tools.kanjinvk.common.CommonActionListener;
import muscular.man.tools.kanjinvk.common.handle.DaoAsyncQueryHandler;
import muscular.man.tools.kanjinvk.model.dto.KanjiTestDto;
import muscular.man.tools.kanjinvk.model.enums.BookmarkEnum;
import muscular.man.tools.kanjinvk.model.storage.columns.KanjiColumn;
import muscular.man.tools.kanjinvk.model.storage.columns.KanjiTestColumn;
import muscular.man.tools.kanjinvk.model.storage.provider.KanjiContentProvider;
import muscular.man.tools.kanjinvk.util.TableUtils;


/**
 * Created by KhanhNV10 on 2015/12/25.
 */
public class KanjiTestDao {
    private static final String TAG = KanjiTestDao.class.getSimpleName();
    private Context mContext;
    public KanjiTestDao(Context context) {
        this.mContext = context;
    }

    /**
     * Get kanji list list from DB.
     * After finish query, callback to update UI
     */
    public void getKanjiTestInfoList(String key, boolean isOnlyBookMarked, final CommonActionListener.CallBackListener<List<KanjiTestDto>> callBack) {
        ContentResolver cr = mContext.getContentResolver();
        DaoAsyncQueryHandler<KanjiTestDto> queryHandler = new DaoAsyncQueryHandler<>(cr);

        queryHandler.setLoaderTaskHandler(new DaoAsyncQueryHandler.LoaderTaskHandler<KanjiTestDto>() {
            @Override
            public KanjiTestDto createEntity(int i, Cursor cursor) {
                KanjiTestColumn kc = new KanjiTestColumn();
                TableUtils.set(kc, cursor);
//                KanjiTestDto dto = new KanjiTestDto(kc);
                return new KanjiTestDto(kc);
            }
        });

        queryHandler.setOnQueryCompleteListener(
                new DaoAsyncQueryHandler.OnQueryCompleteListener<List<KanjiTestDto>>() {
            @Override
            public void onQueryComplete(int i, List<KanjiTestDto> dtos) {
                callBack.onSuccess(dtos);
            }
        });

        String selection = "id LIKE ?";
        String[] selectionArgs = new String[]{key};
//        if (isOnlyBookMarked) {
//            selection = "is_bookmarked='1'";
//            selectionArgs = null;
//        }
        String[] columns = new String[] {"id"};
                TableUtils.getColumns(KanjiColumn.class);
        queryHandler.loadContents(0, null, KanjiContentProvider.KANJI_TEST_URI,
                columns, selection, selectionArgs, "id");
    }

    /**
     * Get kanji list list from DB.
     * After finish query, callback to update UI
     */
    public void getKanjiTestDetailList(List<KanjiTestDto> dtos, final CommonActionListener.CallBackListener<List<KanjiTestDto>> callBack) {

        ContentResolver cr = mContext.getContentResolver();
        DaoAsyncQueryHandler<KanjiTestDto> queryHandler = new DaoAsyncQueryHandler<>(cr);

        queryHandler.setLoaderTaskHandler(new DaoAsyncQueryHandler.LoaderTaskHandler<KanjiTestDto>() {
            @Override
            public KanjiTestDto createEntity(int i, Cursor cursor) {
                KanjiTestColumn kc = new KanjiTestColumn();
                TableUtils.set(kc, cursor);
//                KanjiTestDto dto = new KanjiTestDto(kc);
                return new KanjiTestDto(kc);
            }
        });

        queryHandler.setOnQueryCompleteListener(
                new DaoAsyncQueryHandler.OnQueryCompleteListener<List<KanjiTestDto>>() {
                    @Override
                    public void onQueryComplete(int i, List<KanjiTestDto> dtos) {
                        callBack.onSuccess(dtos);
                    }
                });


        StringBuilder sb = new StringBuilder("id IN (");
        for (KanjiTestDto dto : dtos) {
            sb.append("'").append(dto.id).append("'").append(",");
        }
        sb.append(")");
        String selection =sb.toString().replace(",)", ")");


        String[] columns = TableUtils.getColumns(KanjiTestColumn.class);
        queryHandler.loadContents(0, null, KanjiContentProvider.KANJI_TEST_URI,
                columns, selection, null, "id");
    }


    /**
     * Get kanji list list from DB.
     * After finish query, callback to update UI
     */
    public void getCustomKanjiTestDetailList(int limit, String condition, boolean isRandom, final CommonActionListener.CallBackListener<List<KanjiTestDto>> callBack) {

        ContentResolver cr = mContext.getContentResolver();
        DaoAsyncQueryHandler<KanjiTestDto> queryHandler = new DaoAsyncQueryHandler<>(cr);

        queryHandler.setLoaderTaskHandler(new DaoAsyncQueryHandler.LoaderTaskHandler<KanjiTestDto>() {
            @Override
            public KanjiTestDto createEntity(int i, Cursor cursor) {
                KanjiTestColumn kc = new KanjiTestColumn();
                TableUtils.set(kc, cursor);
                return new KanjiTestDto(kc);
            }
        });

        queryHandler.setOnQueryCompleteListener(
                new DaoAsyncQueryHandler.OnQueryCompleteListener<List<KanjiTestDto>>() {
                    @Override
                    public void onQueryComplete(int i, List<KanjiTestDto> dtos) {
                        callBack.onSuccess(dtos);
                    }
                });

        String whereClause = "id LIKE ?";
        String[] columns = TableUtils.getColumns(KanjiTestColumn.class);
        queryHandler.loadContents(0, null, KanjiContentProvider.KANJI_TEST_URI,
                columns, whereClause, new String[]{condition}, "RANDOM() LIMIT " + limit);
    }

    /**
     * This method insert a shop info into database.
     *
     * @param dto KanjiTestDto
     */
    public void updateKanjiBookmark(KanjiTestDto dto, boolean bookmarkFlg, final CommonActionListener.CallBackListener<Boolean> callBack) {
        ContentResolver cr = mContext.getContentResolver();
        final DaoAsyncQueryHandler queryHandler = new DaoAsyncQueryHandler(cr);
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

        String selection ="id='" + dto.id + "'";
        queryHandler.startUpdate(0, null, KanjiContentProvider.KANJI_TEST_URI, contentValues, selection, null);
    }

    /**
     * This method insert a shop info into database.
     *
     * @param dtos List<KanjiTestDto>
     */
    public void removeKanjiBookmarkList(List<KanjiTestDto> dtos, final CommonActionListener.CallBackListener<Boolean> callBack) {
        ContentResolver cr = mContext.getContentResolver();
        final DaoAsyncQueryHandler queryHandler = new DaoAsyncQueryHandler(cr);


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
        for (KanjiTestDto dto : dtos) {
            sb.append("'").append(dto.id).append("'").append(",");
        }
        sb.append(")");
        String selection =sb.toString().replace(",)", ")");

        ContentValues contentValues = new ContentValues();
        contentValues.put("is_bookmarked", BookmarkEnum.IS_NOT_BOOKMARKED.toString());

        queryHandler.startUpdate(0, null, KanjiContentProvider.KANJI_TEST_URI, contentValues, selection, null);

    }


    /**
     * This method delete all kanji in Kanji table.
     *
     */
    public void deleteAll(final CommonActionListener.CallBackListener<Boolean> callBack) {
        ContentResolver cr = mContext.getContentResolver();
        DaoAsyncQueryHandler queryHandler = new DaoAsyncQueryHandler(cr);
        queryHandler.setOnDeleteCompleteListener(new DaoAsyncQueryHandler.OnDeleteCompleteListener() {
            @Override
            public void onDeleteComplete(int i, int i1) {
                callBack.onSuccess(null);
            }
        });
        queryHandler.startDelete(0, null, KanjiContentProvider.KANJI_TEST_URI, null, null);
    }

    /**
     * This method delete all shop in Shop table.
     *
     */
    public void delete(KanjiTestDto dto, final CommonActionListener.CallBackListener<Boolean> callBack) {
        ContentResolver cr = mContext.getContentResolver();
        DaoAsyncQueryHandler queryHandler = new DaoAsyncQueryHandler(cr);

        String selection ="id='" + dto.id + "'";
        queryHandler.setOnDeleteCompleteListener(new DaoAsyncQueryHandler.OnDeleteCompleteListener() {
            @Override
            public void onDeleteComplete(int i, int i1) {
                callBack.onSuccess(null);
            }
        });

        queryHandler.startDelete(0, null, KanjiContentProvider.KANJI_TEST_URI, selection, null);
    }

    public void insertKanjiList(List<KanjiTestDto> dtos, final CommonActionListener.CallBackListener<Boolean> callBack) {
        ContentResolver cr = mContext.getContentResolver();
        DaoAsyncQueryHandler queryHandler = new DaoAsyncQueryHandler(cr);
        List<KanjiTestColumn> columns = KanjiTestColumn.getKanjiTestColums(dtos);
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
                        if (uri != null && i == (size - 1)) {
                            callBack.onSuccess(true);
                        }
                        else if (uri == null){
                            callBack.onSuccess(false);
                        }
                    }
                });

                for (int i = 0; i < contentValueList.size(); i++) {
                    queryHandler.startInsert(i, null, KanjiContentProvider.KANJI_TEST_URI,
                            contentValueList.get(i));
                }
            }

        }
    }
}
