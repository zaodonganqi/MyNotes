package com.view.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bean.TimeBean;
import com.example.lingling.R;
import com.helper.TimeOpenHelper;
import com.utils.RemindTimeUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.carbswang.android.numberpickerview.library.NumberPickerView;

public class RemindFragment1 extends Fragment {

    protected View mview;
    private int id;
    private Context context;
    private Dialog dialog;
    private Button remind1_cancel_button;
    private Button remind1_confirm_button;
    private NumberPickerView remind1_choose_hour;
    private NumberPickerView remind1_choose_minute;
    private CheckBox remind1_monday;
    private CheckBox remind1_tuesday;
    private CheckBox remind1_wednesday;
    private CheckBox remind1_thursday;
    private CheckBox remind1_friday;
    private CheckBox remind1_saturday;
    private CheckBox remind1_sunday;
    private List<String> weekdays = Arrays.asList("星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日");
    private String chooseDate;

    public static RemindFragment1 newInstance(int id, Context context, Dialog dialog) {
        RemindFragment1 fragment1 = new RemindFragment1();
        fragment1.id = id;
        fragment1.context = context;
        fragment1.dialog = dialog;
        return fragment1;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mview = inflater.inflate(R.layout.remind_dialog_viewpage1, container, false);
        remind1_cancel_button = mview.findViewById(R.id.remind1_cancel_button);
        remind1_confirm_button = mview.findViewById(R.id.remind1_confirm_button);
        remind1_choose_hour = mview.findViewById(R.id.remind1_choose_hour);
        remind1_choose_minute = mview.findViewById(R.id.remind1_choose_minute);
        remind1_monday = mview.findViewById(R.id.remind1_monday);
        remind1_tuesday = mview.findViewById(R.id.remind1_tuesday);
        remind1_wednesday = mview.findViewById(R.id.remind1_wednesday);
        remind1_thursday = mview.findViewById(R.id.remind1_thursday);
        remind1_friday = mview.findViewById(R.id.remind1_friday);
        remind1_saturday = mview.findViewById(R.id.remind1_saturday);
        remind1_sunday = mview.findViewById(R.id.remind1_sunday);
        //设置日期显示
        RemindTimeUtils mremindutil = new RemindTimeUtils();
        mremindutil.setChoosePicker(remind1_choose_hour, remind1_choose_minute);
        //确定按钮
        remind1_confirm_button.setOnClickListener(v -> {
            weekCount();
            RemindTimeUtils times = new RemindTimeUtils();
            String mHour = times.getHours()[remind1_choose_hour.getValue()];
            String mMinute = times.getMinutes()[remind1_choose_minute.getValue()];
            List<String> mRemindTimes = times.timeToString(weekdays, mHour, mMinute);
            int flag = 1;
            for (int i = 0; i < mRemindTimes.size(); i++) {
                TimeBean timeBean = new TimeBean(id);
                timeBean.setTaskID(id);
                timeBean.setRemindTime(mRemindTimes.get(i));
                timeBean.setRepeat(chooseDate);
                TimeOpenHelper timeOpenHelper = TimeOpenHelper.getInstance(context);
                if (timeOpenHelper.insert(timeBean) <= 0) {
                    flag = 0;
                }

            }
            if (mRemindTimes.size() == 0) {
                Toast.makeText(context, "请选择日期", Toast.LENGTH_SHORT).show();
            } else if (flag == 1) {
                Toast.makeText(context, "提醒设置成功", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(context, "设置失败，再试试吧~", Toast.LENGTH_SHORT).show();
            }
        });
        //取消按钮
        remind1_cancel_button.setOnClickListener(v -> {
            dialog.dismiss();
        });
        return mview;
    }

    //判断哪几周被选中了
    private void weekCount() {
        List<String> selectedWeekdays = new ArrayList<>(weekdays);
        chooseDate = "1";
        if (!remind1_monday.isChecked()) {
            selectedWeekdays.remove("星期一");
        } else {
            chooseDate += "1";
        }
        if (!remind1_tuesday.isChecked()) {
            selectedWeekdays.remove("星期二");
        } else {
            chooseDate += "2";
        }
        if (!remind1_wednesday.isChecked()) {
            selectedWeekdays.remove("星期三");
        } else {
            chooseDate += "3";
        }
        if (!remind1_thursday.isChecked()) {
            selectedWeekdays.remove("星期四");
        } else {
            chooseDate += "4";
        }
        if (!remind1_friday.isChecked()) {
            selectedWeekdays.remove("星期五");
        } else {
            chooseDate += "5";
        }
        if (!remind1_saturday.isChecked()) {
            selectedWeekdays.remove("星期六");
        } else {
            chooseDate += "6";
        }
        if (!remind1_sunday.isChecked()) {
            selectedWeekdays.remove("星期日");
        } else {
            chooseDate += "7";
        }

        // 将新的列表赋值给 weekdays
        weekdays = selectedWeekdays;
    }
}
