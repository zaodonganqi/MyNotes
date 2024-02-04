package com.helper;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.bean.TimeBean;

import java.util.List;

public class TimeOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "Timedb.db"; // 数据库名
    private static TimeOpenHelper mhelper = null; // 实例
    public static final String ITEM_TABLE = "remindtime"; // 表名
    public static final int DB_VERSION = 1; // 数据库版本

    // Table column names
    public static final String KEY_ID = "id"; // ID
    public static final String KEY_TASKID = "taskid"; // 对应任务的id
    public static final String KEY_REMINDTIME = "remindtime"; // 提醒时间
    public static final String KEY_REPEAT = "repeat"; // 循环类型

    private SQLiteDatabase mdb = null; // 数据库实例

    public TimeOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // 创建带有上下文的实例
    public static TimeOpenHelper getInstance(Context context) {
        if (mhelper == null) {
            mhelper = new TimeOpenHelper(context);
        }
        return mhelper;
    }

    // 打开数据库读连接
    private SQLiteDatabase openReadLink() {
        if (mdb == null || mdb.isOpen()) {
            mdb = mhelper.getReadableDatabase();
        }
        return mdb;
    }

    // 打开数据库写连接
    private SQLiteDatabase openWriteLink() {
        if (mdb == null || mdb.isOpen()) {
            mdb = mhelper.getWritableDatabase();
        }
        return mdb;
    }

    // 关闭连接
    public void closeLink() {
        if (mdb != null && mdb.isOpen()) {
            mdb.close();
            mdb = null;
        }
    }

    // 创建表
    @Override
    public void onCreate(SQLiteDatabase db) {
        String timeTable = "CREATE TABLE IF NOT EXISTS " + ITEM_TABLE + " (" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                KEY_TASKID + " INTEGER NOT NULL, " +
                KEY_REMINDTIME + " VARCHAR NOT NULL, " +
                KEY_REPEAT + " VARCHAR NOT NULL" + ");";
        db.execSQL(timeTable);
    }

    // 更新数据库版本
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // You can handle database upgrades here if needed
    }

    // 插入新提醒
    public long insert(TimeBean times) {
        long result = -1;
        SQLiteDatabase db = openWriteLink();
        ContentValues cv = new ContentValues();
        cv.put(KEY_TASKID, times.getTaskID());
        cv.put(KEY_REMINDTIME, times.getRemindTime());
        cv.put(KEY_REPEAT, times.getRepeat());
        if (mdb.insert(ITEM_TABLE, null, cv) != -1) {
            String sql = "SELECT last_insert_rowid() FROM " + ITEM_TABLE;
            android.database.Cursor cursor = mdb.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                result = cursor.getInt(0);
            }
            cursor.close();
        }
        return result;
    }

    // 删除单个提醒
    public int deleteOne(int id) {
        int result = 0;
        SQLiteDatabase db = openWriteLink();
        result = mdb.delete(ITEM_TABLE, "id = ?", new String[]{String.valueOf(id)});
        return result;
    }

    // 删除多个提醒
    public int deleteTimes(List<TimeBean> times) {
        int result = 0;
        openWriteLink();
        StringBuilder idList = new StringBuilder();
        for (TimeBean time : times) {
            idList.append(time.getID()).append(",");
        }
        if (idList.length() > 0) {
            idList.setLength(idList.length() - 1);
        }
        result = mdb.delete(ITEM_TABLE, "id IN (" + idList.toString() + ")", null);
        return result;
    }

    // 修改提醒
    public int update(TimeBean time) {
        int result = 0;
        openWriteLink();
        ContentValues cv = new ContentValues();
        cv.put(KEY_TASKID, time.getTaskID());
        cv.put(KEY_REMINDTIME, time.getRemindTime());
        cv.put(KEY_REPEAT, time.getRepeat());
        result = mdb.update(ITEM_TABLE, cv, "id = ?", new String[]{String.valueOf(time.getID())});
        return result;
    }

    // 根据id修改提醒
    public int updateRemindTime(int id, String remindTime) {
        int result = 0;
        openWriteLink();
        ContentValues cv = new ContentValues();
        cv.put(KEY_REMINDTIME, remindTime);
        result = mdb.update(ITEM_TABLE, cv, "id = ?", new String[]{String.valueOf(id)});
        return result;
    }

    // 查询单个提醒
    @SuppressLint("Range")
    public TimeBean query(TimeBean time) {
        TimeBean time1 = new TimeBean(-1);
        openReadLink();
        String sql = "SELECT " + KEY_TASKID + "," + KEY_REMINDTIME + "," + KEY_REPEAT + " FROM " + ITEM_TABLE + " WHERE " + KEY_ID + " = ?";
        android.database.Cursor cursor = mdb.rawQuery(sql, new String[]{String.valueOf(time.getID())});
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                time1.setTaskID(cursor.getInt(cursor.getColumnIndex(KEY_TASKID)));
                time1.setRemindTime(cursor.getString(cursor.getColumnIndex(KEY_REMINDTIME)));
                time1.setRepeat(cursor.getString(cursor.getColumnIndex(KEY_REPEAT)));
            }
        }
        cursor.close();
        return time1;
    }
}
