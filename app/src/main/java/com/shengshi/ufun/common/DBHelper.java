package com.shengshi.ufun.common;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 这个类不要使用字段格式化
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String TAG = "DBHelper";
    // 数据库默认版本号
    private final static int sDBVersion = 1;
    private static final Object mLock = new Object();
    // 数据库名称
    private static final String DATABASE_NAME = "keyword.db";
    /* 搜索关键字表 */
    public static final String TABLE_SEARCH = "search";
    public static final String KEYWORD_ID = "id";// INTEGER
    public static final String KEYWORD_STRING = "keyword";// 搜索关键词
    private final String CREATE_KEYWORDS = "CREATE TABLE " + TABLE_SEARCH + " ("
            + KEYWORD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEYWORD_STRING + " TEXT"
            + " );";

    private static DBHelper mInstance;

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, sDBVersion);
    }

    /**
     * 获得一个DBHelper的实例
     *
     * @param context
     * @return
     */
    public synchronized static DBHelper getInstance(Context context) {
        synchronized (mLock) {
            if (mInstance == null) {
                mInstance = new DBHelper(context);
            }
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /* 草稿箱表 */
        db.execSQL(CREATE_KEYWORDS);
    }

    public boolean deleteDatabase(Context context) {
        return context.deleteDatabase(DATABASE_NAME);
    }

    /**
     * 释放操作数据库的资源
     *
     * @param db database obj
     */
    public void release(SQLiteDatabase db) {
        /*
         * if (db != null) { try { db.close(); db = null; } catch (Exception e)
         * { db.close(); db = null; } finally { db = null; } }
         */
    }

    /**
     * 是否游标
     */
    public void release(Cursor c) {
        if (c != null) {
            try {
                c.close();
                c = null;
            } catch (Exception e) {
                c.close();
                c = null;
            } finally {
                c = null;
            }
        }
    }

    public void deleteAllTable(SQLiteDatabase db) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(
                    "select name from sqlite_master where type='table'", null);
            if (!cursor.isClosed()) {
                while (cursor.moveToNext()) {
                    String name = cursor.getString(cursor
                            .getColumnIndex("name"));
                    if (WHITE_LIST[0].equalsIgnoreCase(name)) {
                        continue;
                    }
                    if (WHITE_LIST[1].equalsIgnoreCase(name)) {
                        continue;
                    }
                    // DROP TABLE IF EXISTS
                    db.execSQL("DROP TABLE IF EXISTS " + name);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.release(cursor);
        }
    }

    public static String[] WHITE_LIST = new String[]{"android_metadata",
            "sqlite_sequence"};

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        deleteAllTable(db);
        this.onCreate(db);
    }
}
