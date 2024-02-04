package com.view.activity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lingling.R;
import com.utils.FullScreen;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setpage);


        //配置全面屏
        FullScreen.Fullscreen(this);


        //设置页左上方返回按钮
        //这条代码你以后能用来做返回动画 overridePendingTransition(R.anim.leftin, R.anim.rightout);
        ImageButton back_to_main = findViewById(R.id.setpage_back_to_main);
        back_to_main.setOnClickListener(v -> finish());



        //switch的点击事件，同时点击整行都能控制switch
        //这是设置1的控制整行
        RelativeLayout setting1 = findViewById(R.id.setting1);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch setting1_switch = findViewById(R.id.setting1_switch);
        setting1.setOnClickListener(v -> setting1_switch.setChecked(!setting1_switch.isChecked()));


        //设置1的状态保存和读取
        SharedPreferences switch1_check = getSharedPreferences("switch1_check",MODE_PRIVATE);
        setting1_switch.setChecked(switch1_check.getBoolean("switch1_on_off",setting1_switch.isChecked()));
        setting1_switch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor1 = switch1_check.edit();
            editor1.putBoolean("switch1_on_off",isChecked);
            editor1.apply();
        });


    }
}