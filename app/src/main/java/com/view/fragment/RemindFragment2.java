package com.view.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.bean.TimeBean;
import com.example.lingling.R;
import com.helper.TimeOpenHelper;
import com.utils.RemindTimeUtils;

import cn.carbswang.android.numberpickerview.library.NumberPickerView;

public class RemindFragment2 extends Fragment {

    protected View mview;
    private int id;
    private Context context;
    private Dialog dialog;
    Button cancel;
    Button confirm;
    NumberPickerView remind2_choose_date;
    NumberPickerView remind2_choose_hour;
    NumberPickerView remind2_choose_minute;
    public static RemindFragment2 newInstance(int id, Context context, Dialog dialog) {
        RemindFragment2 fragment2 = new RemindFragment2();
        fragment2.id = id;
        fragment2.context = context;
        fragment2.dialog = dialog;
        return fragment2;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mview = inflater.inflate(R.layout.remind_dialog_viewpage2, container, false);
        cancel = mview.findViewById(R.id.remind2_cancel_button);
        confirm = mview.findViewById(R.id.remind2_confirm_button);
        remind2_choose_date = mview.findViewById(R.id.remind2_choose_date);
        remind2_choose_hour = mview.findViewById(R.id.remind2_choose_hour);
        remind2_choose_minute = mview.findViewById(R.id.remind2_choose_minute);
        //设置日期显示
        RemindTimeUtils mremindutil = new RemindTimeUtils();
        mremindutil.setChoosePicker(remind2_choose_date, remind2_choose_hour, remind2_choose_minute);
        //确定按钮
        confirm.setOnClickListener(v -> {
            RemindTimeUtils times = new RemindTimeUtils();
            String mDate = times.getDate()[remind2_choose_date.getValue()];
            String mHour = times.getHours()[remind2_choose_hour.getValue()];
            String mMinute = times.getMinutes()[remind2_choose_minute.getValue()];
            String mRemindTime = times.timeToString(mDate, mHour, mMinute);
            TimeBean timeBean = new TimeBean(id);
            timeBean.setTaskID(id);
            timeBean.setRemindTime(mRemindTime);
            timeBean.setRepeat("0");
            TimeOpenHelper timeOpenHelper = TimeOpenHelper.getInstance(context);
            long result = timeOpenHelper.insert(timeBean);
            if (result > 0) {
                Toast.makeText(context, "提醒设置成功", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(context, "设置失败，再试试吧~", Toast.LENGTH_SHORT).show();
            }
        });
        //取消按钮
        cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
        return mview;
    }

}
