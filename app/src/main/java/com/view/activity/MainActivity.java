package com.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.adapters.MainActivityItemAdapter;
import com.bean.TaskBean;
import com.example.lingling.R;
import com.helper.TaskOpenHelper;
import com.utils.FullScreen;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Boolean up = false;
    MainActivityItemAdapter madapter;
    RecyclerView newrecyclerView;

    @SuppressLint({"Range", "NotifyDataSetChanged"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newrecyclerView = findViewById(R.id.all_tasks_view);

        //配置全面屏
        FullScreen.Fullscreen(this);

        //主页右上角设置按钮链接
        ImageButton settings1 = findViewById(R.id.settings1);
        settings1.setOnClickListener(v -> {
            Intent intent1 = new Intent();
            intent1.setClass(MainActivity.this, SettingActivity.class);
            startActivity(intent1);

        });

        //设置主页背景
        ImageButton personal_background = findViewById(R.id.personal_background);
        personal_background.setOnClickListener(v -> {

        });

        //显示已有数据
        updateView();

        //新建任务按钮以及其功能
        ImageButton new_task = findViewById(R.id.new_task);
        new_task.setOnClickListener(v -> {
            Intent addtask = new Intent(this,NewTaskActivity.class);
            startActivity(addtask);
        });

    }

    @Override

    public void onPause() {
        super.onPause();
        up = true;
    }

    @SuppressLint({"NotifyDataSetChanged", "Range"})
    @Override
    public void onResume() {
        super.onResume();
        if (up) {
            // 查询数据库或获取最新数据
            updateView();
            up = false;
        }
    }

    @SuppressLint("Range")
    private void updateView() {
        //显示已有数据
        List<TaskBean> tasklist = new ArrayList<>();
        TaskOpenHelper myOpenHelper = new TaskOpenHelper(this);
        SQLiteDatabase db = myOpenHelper.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * FROM tasks", null);
        if (cursor.moveToFirst()) {
            do {
                TaskBean alltask = new TaskBean();
                alltask.setID(cursor.getInt(cursor.getColumnIndex("id")));
                alltask.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                alltask.setWords(cursor.getString(cursor.getColumnIndex("words")));
                alltask.setColor(cursor.getString(cursor.getColumnIndex("color")));
                alltask.setTime(cursor.getString(cursor.getColumnIndex("time")));
                alltask.setBackgroundUri(cursor.getString(cursor.getColumnIndex("backgrounduri")));
                alltask.setAlpha(cursor.getInt(cursor.getColumnIndex("alpha")));
                alltask.setImageUri(cursor.getString(cursor.getColumnIndex("imageuri")));
                alltask.setMP3Uri(cursor.getString(cursor.getColumnIndex("mp3uri")));
                alltask.setVideoUri(cursor.getString(cursor.getColumnIndex("videouri")));
                // 获取其他列的数据并设置到data对象中
                tasklist.add(alltask);
            } while (cursor.moveToNext());

        }
        cursor.close();
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        if (newrecyclerView != null) {
            newrecyclerView.setLayoutManager(layoutManager);
            madapter = new MainActivityItemAdapter(tasklist);
            newrecyclerView.setAdapter(madapter);
        }
    }
}
