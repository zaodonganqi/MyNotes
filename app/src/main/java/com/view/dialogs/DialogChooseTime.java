package com.view.dialogs;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.adapters.RemindFragmentAdapter;
import com.example.lingling.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.utils.ScreenSize;

import java.util.Arrays;
import java.util.List;

public class DialogChooseTime {
    private ViewPager2 remindViewPage;
    private TabLayout remindTabTitle;
    private TextView remind_music;
    private boolean isFirst = true;
    private int lastHeight;
    private final List<String> tabTextList = Arrays.asList("循环提醒", "一次性提醒", "已设提醒");

    @SuppressLint("NotifyDataSetChanged")
    public void show(Context context, int id, AppCompatActivity appCompatActivity) {
        Dialog dialog = new Dialog(context);
        View mView = View.inflate(context, R.layout.remind_dialog, null);

        // 配置翻页视图
        remindViewPage = mView.findViewById(R.id.remind_viewpage);
        RemindFragmentAdapter mAdapter = new RemindFragmentAdapter(appCompatActivity);
        mAdapter.setRFDatas(id, context, dialog);
        remindViewPage.setAdapter(mAdapter);

        // 为翻页视图配置标题
        remindTabTitle = mView.findViewById(R.id.remind_tab_title);
        new TabLayoutMediator(remindTabTitle, remindViewPage, (tab, position) -> tab.setText(tabTextList.get(position))).attach();

        // 禁用标签的长按和工具提示
        for (int i = 0; i < remindTabTitle.getTabCount(); i++) {
            View tabView = remindTabTitle.getTabAt(i).view;
            tabView.setLongClickable(false);
            tabView.setTooltipText("");
            tabView.setOnLongClickListener(null);
        }

        //选择音频
        remind_music = mView.findViewById(R.id.remind_music);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setWindowAnimations(R.style.DialogAnimation);
            dialog.setContentView(mView);
            dialog.setCanceledOnTouchOutside(true);

            // 设置对话框的大小和颜色
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            int miniHeight = (int) (ScreenSize.getInstance(context).getScreenHeight() * 0.70f);
            lastHeight = miniHeight;
            mView.setMinimumHeight(miniHeight);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = (int) (ScreenSize.getInstance(context).getScreenWidth() * 0.90f);
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.CENTER;
            window.setAttributes(lp);

            // 动态变化dialog高度
            remindViewPage.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    if (position == 0 && !isFirst) {
                        smoothResize(dialog, mView, (int) (ScreenSize.getInstance(context).getScreenHeight() * 0.70f));
                    } else if (position == 0 && isFirst) {
                        isFirst = false;
                    } else if (position == 1) {
                        smoothResize(dialog, mView, (int) (ScreenSize.getInstance(context).getScreenHeight() * 0.60f));
                    }
                }
            });

            dialog.show();
        }
    }

    private void smoothResize(@NonNull Dialog dialog, View view, int targetHeight) {
        final Window window = dialog.getWindow();
        if (window == null) return;

        ValueAnimator anim = ValueAnimator.ofInt(lastHeight, targetHeight);
        anim.addUpdateListener(valueAnimator -> {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.gravity = Gravity.CENTER;
            lp.height = (int) valueAnimator.getAnimatedValue();
            window.setAttributes(lp);
        });
        lastHeight = targetHeight;


        anim.setDuration(300); // 设置动画时长
        anim.start();
        view.setMinimumHeight((int) (targetHeight * 0.7));
    }
}
