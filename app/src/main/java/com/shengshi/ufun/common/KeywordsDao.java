package com.shengshi.ufun.common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * @author harry
 */
public class KeywordsDao {
    private Context mContext;

    public KeywordsDao(Context context) {
        this.mContext = context;
    }

    /**
     * @param info
     * @return 0:失败 else:索引值
     * @author harry
     */
    public int insert(String keyword) {
        int id = 0;
        try {
            SQLiteDatabase db = DBHelper.getInstance(mContext)
                    .getWritableDatabase();
            if (keyword == null || !db.isOpen() || db == null) {
                return id;
            }
            if (db.isOpen()) {
                db.beginTransaction();
                try {
                    if (!isExistKeyword(keyword)) {
                        ContentValues cv = insertKeyword(keyword);
                        id = (int) db.insert(DBHelper.TABLE_SEARCH, null, cv);
                        db.setTransactionSuccessful();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    db.endTransaction();
                    DBHelper.getInstance(mContext).release(db);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    /**
     * @return
     */
    private boolean isExistKeyword(String keyword) {
        ArrayList<String> keywordList = queryKeywordList();
        if (keywordList != null) {
            if (keywordList.contains(keyword)) {
                return true;
            }
            return false;
        } else {
            return false;
        }
    }

    private ContentValues insertKeyword(String keyword) {
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.KEYWORD_STRING, keyword);
        return cv;
    }

    /**
     * 获取搜索关键字
     *
     * @param page 分页
     * @return
     */
    public ArrayList<String> queryKeywordList() {
        Cursor c = null;
        ArrayList<String> data = null;
        SQLiteDatabase db = DBHelper.getInstance(mContext)
                .getReadableDatabase();
        try {
            if (db.isOpen()) {
                String sql = "SELECT * FROM " + DBHelper.TABLE_SEARCH
                        + " ORDER BY " + DBHelper.KEYWORD_ID + " DESC";
                c = db.rawQuery(sql, null);
                if (c != null && c.getCount() != 0) {
                    data = findKeywordList(c);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBHelper.getInstance(mContext).release(c);
            DBHelper.getInstance(mContext).release(db);
        }
        return data;
    }

    /*
     * 查找String集合
     */
    private ArrayList<String> findKeywordList(Cursor c) {
        ArrayList<String> keywordList = null;
        String info = null;
        if (c != null && !c.isClosed()) {
            keywordList = new ArrayList<String>();
            int ii = 1;
            while ((info = findKeyword(c)) != null && ii <= 10) {
                keywordList.add(info);
                ii++;
            }
        }
        return keywordList;
    }

    /*
     * 查找String
     */
    private String findKeyword(Cursor c) {
        String keyword = null;
        if (c != null && !c.isClosed()) {
            if (!c.moveToNext()) {
                return null;
            }
            keyword = c.getString(c.getColumnIndex(DBHelper.KEYWORD_STRING));
        }
        return keyword;
    }

    /**
     * 清除缓存
     */
    public void removeAllSearchKeywords() {
        SQLiteDatabase db = DBHelper.getInstance(mContext)
                .getReadableDatabase();
        if (db.isOpen()) {
            db.execSQL(String.format("DELETE FROM %s;", DBHelper.TABLE_SEARCH));
            db.close();
        }
    }
}
