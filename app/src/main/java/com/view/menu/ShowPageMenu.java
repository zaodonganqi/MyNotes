package com.view.menu;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bean.TaskBean;
import com.example.lingling.R;
import com.helper.TaskOpenHelper;
import com.utils.ScreenSize;
import com.view.dialogs.DialogChooseTime;

import java.io.File;
import java.util.Objects;

public class ShowPageMenu {

    public static final int REQUEST_CODE_SELECT_IMAGE = 123;

    @SuppressLint({"NotifyDataSetChanged", "RtlHardcoded", "IntentReset"})
    public void showMenu(Context context, AppCompatActivity appCompatActivity, int layoutX, int layoutY, int id, EditText item_title, EditText item_words
            , TextView new_title, TaskBean menutask) {
        @SuppressLint("ResourceType") final Dialog dialog = new Dialog(context);
        View view = View.inflate(context, R.layout.showpage_menu, null);
        dialog.setContentView(view);

        //Button准备
        Button menu_remind = view.findViewById(R.id.menu_remind);
        Button menu_color = view.findViewById(R.id.menu_color);
        Button menu_setbackground = view.findViewById(R.id.menu_setbackground);
        Button menu_delete = view.findViewById(R.id.menu_delete);
        Button menu_share = view.findViewById(R.id.menu_share);

        //使得点击对话框外部对话框消失
        dialog.setCanceledOnTouchOutside(true);

        //设置对话框的大小和颜色
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        LinearLayout dialogContainer = dialog.findViewById(R.id.menu_container);
        dialogContainer.setBackground(ContextCompat.getDrawable(context, R.drawable.dialog_background));
        view.setMinimumHeight((int) (ScreenSize.getInstance(context).getScreenHeight() * 0.25f));
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setDimAmount(0.2f);
        dialogWindow.setWindowAnimations(R.style.MenuAnimation);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSize.getInstance(context).getScreenWidth() * 0.40f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.x = (int) (layoutX - (ScreenSize.getInstance(context).getScreenWidth() * 0.56f));
        lp.y = (int) (layoutY - (ScreenSize.getInstance(context).getScreenHeight() * 0.36f));
        dialogWindow.setAttributes(lp);

        //删除此页
        menu_delete.setOnClickListener(v -> {
            Dialog delete_dialog = new Dialog(context);
            View delete_view = View.inflate(context, R.layout.delete_dialog, null);
            Button cancel = delete_view.findViewById(R.id.remind2_cancel_button);
            Button confirm = delete_view.findViewById(R.id.remind2_confirm_button);
            Window window = delete_dialog.getWindow();
            window.setWindowAnimations(R.style.DialogAnimation);
            delete_dialog.setContentView(delete_view);
            //使得点击对话框外部对话框消失
            delete_dialog.setCanceledOnTouchOutside(true);
            //设置对话框的大小和颜色
            delete_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            FrameLayout dialogContainer1 = delete_dialog.findViewById(R.id.menu_delete_container);
            dialogContainer1.setBackground(ContextCompat.getDrawable(context, R.drawable.dialog_background));
            delete_view.setMinimumHeight((int) (ScreenSize.getInstance(context).getScreenHeight() * 0.15f));
            Window dialogWindow1 = delete_dialog.getWindow();
            WindowManager.LayoutParams lp1 = dialogWindow1.getAttributes();
            lp1.width = (int) (ScreenSize.getInstance(context).getScreenWidth() * 0.75f);
            lp1.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp1.gravity = Gravity.CENTER;
            dialogWindow1.setAttributes(lp1);


            //删除任务
            confirm.setOnClickListener(v1 -> {
                TaskOpenHelper myOpenHelper = TaskOpenHelper.getInstance(context);
                TaskBean dtask = myOpenHelper.query(menutask);
                File dfile = new File(dtask.getBackgroundUri());
                int result = myOpenHelper.deleteone(id);
                if (dfile.exists()) {
                    dfile.delete();
                }
                if(result == 1){
                    delete_dialog.dismiss();
                    dialog.dismiss();
                    appCompatActivity.finish();
                    Toast.makeText(context,"删除成功",Toast.LENGTH_SHORT).show();
                } else {
                    delete_dialog.dismiss();
                    dialog.dismiss();
                    Toast.makeText(context,"删除失败，要不重新试试？",Toast.LENGTH_SHORT).show();
                }
            });

            cancel.setOnClickListener(v12 -> {
                delete_dialog.dismiss();
            });
            delete_dialog.show();
        });

        //修改背景
        menu_setbackground.setOnClickListener(v -> {
            @SuppressLint("IntentReset") Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            appCompatActivity.startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
        });

        //设置提醒
        menu_remind.setOnClickListener(v1 -> {
            dialog.dismiss();
            DialogChooseTime dialogChooseTime = new DialogChooseTime();
            dialogChooseTime.show(context, id, appCompatActivity);
//            Intent intent = new Intent(activity, RemindService.class);
//            activity.startService(intent);
        });

        //修改文字颜色
        menu_color.setOnClickListener(v -> {
            TaskOpenHelper myOpenHelper = TaskOpenHelper.getInstance(context);
            int result = myOpenHelper.updatecolor(id);
            String color = myOpenHelper.queryColor(id);
            if (Objects.equals(color, "2")) {
                int alpha = Color.alpha(new_title.getCurrentTextColor()); // 保存当前文字透明度
                item_title.setTextColor(Color.parseColor("#CC000000"));
                item_words.setTextColor(Color.parseColor("#CC000000"));
                new_title.setTextColor(Color.parseColor("#CC000000"));
                new_title.setTextColor(new_title.getTextColors().withAlpha(alpha)); // 应用保存的透明度
            } else if (Objects.equals(color, "-2")) {
                int alpha = Color.alpha(new_title.getCurrentTextColor()); // 保存当前文字透明度
                item_title.setTextColor(Color.parseColor("#F4F4F3"));
                item_words.setTextColor(Color.parseColor("#F4F4F3"));
                new_title.setTextColor(Color.parseColor("#F4F4F3"));
                new_title.setTextColor(new_title.getTextColors().withAlpha(alpha)); // 应用保存的透明度
            }

        });

        //分享
        menu_share.setOnClickListener(v -> {

        });

        dialog.show();
    }

}
