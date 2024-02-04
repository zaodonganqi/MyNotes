package com.view.menu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.lingling.R;
import com.service.RemindService;
import com.service.RemindTime;
import com.utils.ScreenSize;

import java.util.Objects;

public class NewTaskMenu {

    public static final int REQUEST_CODE_SELECT_IMAGE = 123;
    private NewTaskColor newTaskColor = null;
    private RemindTime remindTime = null;
    private String mcolor = "2";
    private long time = 0;
    private PendingIntent pendingIntent;

    @SuppressLint({"NotifyDataSetChanged", "RtlHardcoded", "IntentReset"})
    public void customDialog(Context context, Activity activity, int layoutX, int layoutY,String color, EditText item_title, EditText item_words
            , TextView new_title) {
        @SuppressLint("ResourceType") final Dialog dialog = new Dialog(context);
        View view = View.inflate(context, R.layout.new_task_page_menu, null);
        dialog.setContentView(view);

        this.mcolor = color;

        //Button准备
        Button new_task_menu_remind = view.findViewById(R.id.new_task_menu_remind);
        Button new_task_menu_color = view.findViewById(R.id.new_task_menu_color);
        Button new_task_menu_setbackground = view.findViewById(R.id.new_task_menu_setbackground);

        //使得点击对话框外部对话框消失
        dialog.setCanceledOnTouchOutside(true);

        //设置对话框的大小和颜色
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        LinearLayout dialogContainer = dialog.findViewById(R.id.new_task_menu_container);
        dialogContainer.setBackground(ContextCompat.getDrawable(context, R.drawable.dialog_background));
        view.setMinimumHeight((int) (ScreenSize.getInstance(context).getScreenHeight() * 0.18f));
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setDimAmount(0.2f);
        dialogWindow.setWindowAnimations(R.style.MenuAnimation);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSize.getInstance(context).getScreenWidth() * 0.40f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.x = (int) (layoutX - (ScreenSize.getInstance(context).getScreenWidth() * 0.56f));
        lp.y = (int) (layoutY - (ScreenSize.getInstance(context).getScreenHeight() * 0.40f));
        dialogWindow.setAttributes(lp);

        //修改背景
        new_task_menu_setbackground.setOnClickListener(v -> {
            @SuppressLint("IntentReset") Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            activity.startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
        });

        //修改文字颜色
        new_task_menu_color.setOnClickListener(v -> {
            if (Objects.equals(mcolor, "2")) {
                mcolor = "-2";
            } else {
                mcolor = "2";
            }
            int alpha = Color.alpha(new_title.getCurrentTextColor()); // 保存当前文字透明度
            // 保存当前文字透明度
            if (Objects.equals(mcolor, "2")) {
                item_title.setTextColor(Color.parseColor("#CC000000"));
                item_words.setTextColor(Color.parseColor("#CC000000"));
                new_title.setTextColor(Color.parseColor("#CC000000"));
                new_title.setTextColor(new_title.getTextColors().withAlpha(alpha)); // 应用保存的透明度
                if (newTaskColor != null) {
                    newTaskColor.setColor("2");
                }
            } else {
                item_title.setTextColor(Color.parseColor("#F4F4F3"));
                item_words.setTextColor(Color.parseColor("#F4F4F3"));
                new_title.setTextColor(Color.parseColor("#F4F4F3"));
                new_title.setTextColor(new_title.getTextColors().withAlpha(alpha)); // 应用保存的透明度
                if (newTaskColor != null) {
                    newTaskColor.setColor("-2");
                }
            }

        });

        new_task_menu_remind.setOnClickListener(v -> {
            Intent intent = new Intent(activity, RemindService.class);
//            remindTime.getTime(1);
//            remindTime.getPendingIntent(this.pendingIntent);
            activity.startService(intent);
        });

        dialog.show();
    }

    public void setNewTaskColor(NewTaskColor newTaskColor) {
        this.newTaskColor = newTaskColor;
    }

}
