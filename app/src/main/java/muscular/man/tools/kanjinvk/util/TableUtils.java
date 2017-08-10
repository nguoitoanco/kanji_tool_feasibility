package muscular.man.tools.kanjinvk.util;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import muscular.man.tools.kanjinvk.common.annotation.ColumnAnnotation;
import muscular.man.tools.kanjinvk.common.annotation.TableAnnotation;

/**
 * Created by KhanhNV10 on 2015/12/25.
 */
public class TableUtils {

    /** Get table name declared from annotation table in model class */
    public static String tableName(Class<?> tClass) {
        TableAnnotation tableAnnotation = tClass.getAnnotation(TableAnnotation.class);
        String tableName = tClass.getSimpleName();
        if (tableAnnotation != null) {
            if (!StringUtils.isEmpty(tableAnnotation.name())) {
                tableName = tableAnnotation.name();
            }
        }

        return tableName;
    }

    /** Get list of columns declared from annotation column in model class */
    public static String[] getColumns(Class<?> tClass) {
        List<String> columns = new ArrayList<>();
        for (Field field : tClass.getDeclaredFields()) {
            ColumnAnnotation fieldAnnotation = field.getAnnotation(ColumnAnnotation.class);
            if (fieldAnnotation != null) {
                String columnName = fieldAnnotation.name();
                if (columnName != null)
                    columns.add(columnName);
            }
        }

        String[] columnsArray = new String[columns.size()];
        columns.toArray(columnsArray);
        return columnsArray;
    }

    /** Create table */
    public static void createTable(SQLiteDatabase db, Class<?> tClass) {
        if (tableName(tClass) == null) {
            return;
        }
        StringBuffer sql = new StringBuffer("CREATE TABLE ");
        sql.append(tableName(tClass));
        sql.append(" (");
//        sql.append(" (_id INTEGER PRIMARY KEY AUTOINCREMENT");
        for (Field field : tClass.getDeclaredFields()) {
            ColumnAnnotation fieldAnnotation = field.getAnnotation(ColumnAnnotation.class);
            if (fieldAnnotation != null) {
                sql.append(", ");
                sql.append(fieldAnnotation.name());
                sql.append(" ");
                sql.append(fieldAnnotation.type());
            }
        }
        sql.append(");");

        String strSql = sql.toString().replace("(, ", "(");
        db.execSQL(strSql);
    }

    /** Delete table */
    public static void deleteTable(SQLiteDatabase db, Class<?> tClass) throws Exception {
        db.execSQL(tableName(tClass));
    }

    /** Get content values of all properties in an object*/
    public static ContentValues getFilledContentValues(Object object)
            throws IllegalAccessException {
        ContentValues contentValues = new ContentValues();
        for (Field field : object.getClass().getDeclaredFields()) {
            ColumnAnnotation fieldAnnotation = field.getAnnotation(ColumnAnnotation.class);
            if (fieldAnnotation != null) {
                putInContentValues(contentValues, field, object);
            }
        }
        return contentValues;
    }

    /** Get content values of all properties in an object*/
    public static List<ContentValues> getContentValueList(List<?> objects)
            throws IllegalAccessException {
        List<ContentValues> cvList = new ArrayList<>();

        for (Object o : objects) {
            cvList.add(getFilledContentValues(o));
        }

        return cvList;
    }

    /** Put each property's key and value into content values */
    private static void putInContentValues(ContentValues contentValues, Field field,
                                   Object object) throws IllegalAccessException {
        if (!field.isAccessible()) {
            field.setAccessible(true); // for private variables
        }

        Object fieldValue = field.get(object);
        String key = field.getAnnotation(ColumnAnnotation.class).name();
        if (fieldValue instanceof Long) {
            contentValues.put(key, Long.valueOf(fieldValue.toString()));
        } else if (fieldValue instanceof String) {
            contentValues.put(key, fieldValue.toString());
        } else if (fieldValue instanceof Integer) {
            contentValues.put(key, Integer.valueOf(fieldValue.toString()));
        } else if (fieldValue instanceof Float) {
            contentValues.put(key, Float.valueOf(fieldValue.toString()));
        } else if (fieldValue instanceof Byte) {
            contentValues.put(key, Byte.valueOf(fieldValue.toString()));
        } else if (fieldValue instanceof Short) {
            contentValues.put(key, Short.valueOf(fieldValue.toString()));
        } else if (fieldValue instanceof Boolean) {
            contentValues.put(key, Boolean.parseBoolean(fieldValue.toString()));
        } else if (fieldValue instanceof Double) {
            contentValues.put(key, Double.valueOf(fieldValue.toString()));
        } else if (fieldValue instanceof Byte[] || fieldValue instanceof byte[]) {
            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                        outputStream);
                objectOutputStream.writeObject(fieldValue);
                contentValues.put(key, outputStream.toByteArray());
                objectOutputStream.flush();
                objectOutputStream.close();
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
            }
        }
    }

    public static void set(Object object, Cursor c){
        for (Field field : object.getClass().getDeclaredFields()) {
            try {
                ColumnAnnotation fieldAnnotation = field.getAnnotation(ColumnAnnotation.class);
                if (fieldAnnotation != null) {
                    String columnName = field.getAnnotation(ColumnAnnotation.class).name();
                    field.setAccessible(true);
                    int indexColumn = c.getColumnIndex(columnName);
                    if (indexColumn >= 0) {
                        field.set(object, c.getString(indexColumn));
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
