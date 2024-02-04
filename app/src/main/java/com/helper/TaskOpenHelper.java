package com.helper;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.bean.TaskBean;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class TaskOpenHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "Taskdb.db";//数据库名
    private static TaskOpenHelper mhelper = null;//帮助器实例
    private SQLiteDatabase mdb = null;//数据库
    public static final String ITEM_TABLE = "tasks";//任务列表表名
    public static final String ENVIRONMENT_TABLE = "environment";//环境列表表名
    public static final int DB_VERSION = 1;//数据库版本

    //表内字段名
    public static final String KEY_ID = "id";//ID
    public static final String KEY_TITLE = "title";//标题
    public static final String KEY_WORDS = "words";//文本内容
    public static final String KEY_COLOR = "color";//文本颜色
    public static final String KEY_TIME = "time";//创建时间
    public static final String KEY_BACKGROUNDURI = "backgrounduri";//页面背景
    public static final String KEY_ALPHA = "alpha";//背景透明度
    public static final String KEY_IMAGEURI = "imageuri";//图片路径
    public static final String KEY_MP3URI = "mp3uri";//提醒音路径
    public static final String KEY_VIDEOURI = "videouri";//视频路径
    public static final String KEY_ENVIRONMENT = "environment";//环境

    //构造函数
    public TaskOpenHelper(Context context) {
        super(context,DB_NAME,null,1);
    }

    //单例模式获取帮助器唯一实例
    public static TaskOpenHelper getInstance(Context context) {
        if(mhelper == null) {
            mhelper = new TaskOpenHelper(context);
        }
        return mhelper;
    }

    //打开数据库读连接
    public SQLiteDatabase openReadLink() {
        if(mdb == null || !mdb.isOpen()) {
            mdb = mhelper.getReadableDatabase();
        }
        return mdb;
    }

    //打开数据库写连接
    public SQLiteDatabase openWriteLink() {
        if(mdb == null || !mdb.isOpen()) {
            mdb = mhelper.getWritableDatabase();
        }
        return mdb;
    }

    //关闭数据库连接
    public void closeLink() {
        if(mdb !=null && mdb.isOpen()) {
            mdb.close();
            mdb = null;
        }
    }

    //创建数据表处理
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTaskTable = "CREATE TABLE IF NOT EXISTS " + ITEM_TABLE + " ("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + KEY_TITLE + " VARCHAR NOT NULL," +  KEY_WORDS + " VARCHAR NOT NULL,"
                + KEY_COLOR + " VARCHAR NOT NULL," + KEY_TIME + " VARCHAR NOT NULL,"
                + KEY_BACKGROUNDURI + " VARCHAR NOT NULL," + KEY_ALPHA + " INTEGER NOT NULL,"
                + KEY_IMAGEURI + " VARCHAR NOT NULL," + KEY_MP3URI + " VARCHAR NOT NULL,"
                + KEY_VIDEOURI + " VARCHAR NOT NULL"
                + ");";
        db.execSQL(createTaskTable);
    }

    //升级数据库
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    //任务增
    public long insert(TaskBean tasks) {
        long result = -1;
        SQLiteDatabase db = openWriteLink();
        ContentValues cv = new ContentValues();
        cv.put("title", tasks.getTitle());
        cv.put("words", tasks.getWords());
        cv.put("color",tasks.getColor());
        cv.put("time", tasks.getTime());
        cv.put("backgrounduri",tasks.getBackgroundUri());
        cv.put("alpha",tasks.getAlpha());
        //下面三者只是填个1防止置空，还没实现
        cv.put("imageuri",1);
        cv.put("mp3uri",1);
        cv.put("videouri",1);
        if(mdb.insert(ITEM_TABLE,"",cv) != -1) {
            String sql = "SELECT last_insert_rowid() FROM " + ITEM_TABLE;
            Cursor cursor = mdb.rawQuery(sql,null);
            if (cursor.moveToFirst()) {
                result = cursor.getInt(0);
            }
            cursor.close();
        }
        return result;
    }

    //环境增
//    public long insert_environment(String name) {
//        long result = -1;
//        SQLiteDatabase db = openWriteLink();
//        ContentValues cv = new ContentValues();
//        cv.put("environment", name);
//        result = mdb.insert(ENVIRONMENT_TABLE,"",cv);
//        return result;
//    }

    //任务删
    public int delete(List<Integer> ids) {
        int result = 0;
        SQLiteDatabase db = openWriteLink();
        for (int id : ids) {
            queryAndDeleteFile(id);
            result = mdb.delete(ITEM_TABLE, "id = ?", new String[]{String.valueOf(id)});
        }
        return result;
    }

    //删除单独某一个
    public int deleteone(int id) {
        int result = 0;
        SQLiteDatabase db = openWriteLink();
        queryAndDeleteFile(id);
        result = mdb.delete(ITEM_TABLE, "id = ?", new String[]{String.valueOf(id)});
        return result;
    }

    //环境删
//    public int delete_environment(List<Integer> ids) {
//        int result = 0;
//        SQLiteDatabase db = openWriteLink();
//        for (int id : ids) {
//            result = mdb.delete(ITEM_TABLE, "id = ?", new String[]{String.valueOf(id)});
//        }
//        return result;
//    }

    //一次删除多个
    public int deleteTasks(List<TaskBean> tasks) {
        int result = 0;
        SQLiteDatabase db = openWriteLink();
        // 将id列表转换为逗号分隔的字符串
        StringBuilder idList = new StringBuilder();
        for (TaskBean tasks1 : tasks) {
            queryAndDeleteFile(tasks1.getID());
            idList.append(tasks1.getID()).append(",");
        }
        // 移除最后一个逗号
        if (idList.length() > 0) {
            idList.setLength(idList.length() - 1);
        }
        // 使用 IN 关键字进行批量删除
        result = db.delete(ITEM_TABLE, "id IN (" + idList.toString() + ")", null);
        return result;
    }


    //改大体
    public int update(TaskBean tasks) {
        int result = 0;
        SQLiteDatabase db = openWriteLink();
        ContentValues cv = new ContentValues();
        cv.put("title",tasks.getTitle());
        cv.put("words",tasks.getWords());
        cv.put("time",tasks.getTime());
        //cv.put("imageuri",1);
        //cv.put("mp3uri",1);
        //cv.put("videouri",1);
        result = mdb.update(ITEM_TABLE,cv,"id = ? ",new String[]{String.valueOf(tasks.getID())});
        return result;
    }

    //改背景
    public int updatebackground(int id, Uri uri) {
        int result = 0;
        SQLiteDatabase db = openWriteLink();
        ContentValues cv = new ContentValues();
        cv.put("backgrounduri", String.valueOf(uri));
        result = mdb.update(ITEM_TABLE,cv,"id = ? ",new String[]{String.valueOf(id)});
        return result;
    }

    //改背景透明度
    public int updatealpha(int id, int alpha) {
        int result = 0;
        SQLiteDatabase db = openWriteLink();
        ContentValues cv = new ContentValues();
        cv.put("alpha", alpha);
        result = mdb.update(ITEM_TABLE,cv,"id = ? ",new String[]{String.valueOf(id)});
        return result;
    }

    //改文字颜色
    public int updatecolor(int id) {
        int result = 0;
        SQLiteDatabase db = openWriteLink();
        ContentValues cv = new ContentValues();
        if(Objects.equals(mhelper.queryColor(id), String.valueOf(2))) {
            cv.put("color", "-2");
        }else {
            cv.put("color", "2");
        }
        result = mdb.update(ITEM_TABLE,cv,"id = ? ",new String[]{String.valueOf(id)});
        return result;
    }

    //查
    @SuppressLint("Range")
    public TaskBean query(TaskBean tasks) {
        TaskBean tasks1 = new TaskBean();
        SQLiteDatabase db = openReadLink();
        String sql = "SELECT " + KEY_TITLE + "," + KEY_WORDS + "," + KEY_TIME + "," + KEY_BACKGROUNDURI + "," + KEY_IMAGEURI + "," + KEY_MP3URI + "," + KEY_VIDEOURI + " FROM " + ITEM_TABLE + " WHERE " + KEY_ID + " = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(tasks.getID())});
        if(cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                tasks1.setTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
                tasks1.setWords(cursor.getString(cursor.getColumnIndex(KEY_WORDS)));
                tasks1.setTime(cursor.getString(cursor.getColumnIndex(KEY_TIME)));
                tasks1.setBackgroundUri(cursor.getString(cursor.getColumnIndex(KEY_BACKGROUNDURI)));
                tasks1.setImageUri(cursor.getString(cursor.getColumnIndex(KEY_IMAGEURI)));
                tasks1.setMP3Uri(cursor.getString(cursor.getColumnIndex(KEY_MP3URI)));
                tasks1.setVideoUri(cursor.getString(cursor.getColumnIndex(KEY_VIDEOURI)));
            }
        }
        cursor.close();
        return tasks1;
    }

    //查字体颜色
    @SuppressLint("Range")
    public String queryColor(int id) {
        String result = null;
        SQLiteDatabase db = openReadLink();
        String sql = "SELECT " + KEY_COLOR + " FROM " + ITEM_TABLE + " WHERE " + KEY_ID + " = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(id)});
        if(cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                result = cursor.getString(cursor.getColumnIndex(KEY_COLOR));
            }
        }
        cursor.close();
        return result;
    }

    @SuppressLint("Range")
    private void queryAndDeleteFile(int id) {
        String result = null;
        SQLiteDatabase db  = openReadLink();
        String sql = "SELECT " + KEY_BACKGROUNDURI + " FROM " + ITEM_TABLE + " WHERE " + KEY_ID + " = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(id)});
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                result = cursor.getString(cursor.getColumnIndex(KEY_BACKGROUNDURI));
            }
        }
        cursor.close();
        assert result != null;
        File file = new File(result);
        if (file.exists()) {
            file.delete();
        }
    }

}
