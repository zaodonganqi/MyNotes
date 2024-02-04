package com.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bean.TaskBean;
import com.example.lingling.R;
import com.helper.TaskOpenHelper;
import com.view.activity.ShowPageActivity;

import java.util.ArrayList;
import java.util.List;

// 在Adapter中设置数据列表
public class MainActivityItemAdapter extends RecyclerView.Adapter<MainActivityItemAdapter.MyViewHolder> {
    //Holder开始
    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewtitle;
        TextView textViewwords;
        CheckBox recycler_check_box;
        public MyViewHolder(View view) {
            super(view);
            textViewtitle = view.findViewById(R.id.title_text_view);
            textViewwords = view.findViewById(R.id.words_text_view);
            recycler_check_box = view.findViewById(R.id.recycler_check_box);
        }
    }
    //Holder结束
    private List<TaskBean> dataList;
    private boolean isLongPress = false;
    private boolean isAllChose = false;
    private List<TaskBean> selectedList = new ArrayList<>();//被选中的item列表
    public MainActivityItemAdapter(List<TaskBean> dataList) {

        this.dataList = dataList;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 创建ViewHolder的代码
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task,parent,false);
        return new MyViewHolder(view);
    }
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, @SuppressLint("RecyclerView") int position) {

        // 绑定数据到ViewHolder的代码
        TaskBean tasks = dataList.get(position);
        myViewHolder.textViewtitle.setText(tasks.getTitle());
        myViewHolder.textViewwords.setText(tasks.getWords());

        //获取根布局
        LinearLayout task_long_click_bar = ((Activity) myViewHolder.itemView.getContext()).findViewById(R.id.task_long_click_bar);
        ImageButton close_long_click = ((Activity) myViewHolder.itemView.getContext()).findViewById(R.id.close_long_click);
        ImageButton delete_these_tasks = ((Activity) myViewHolder.itemView.getContext()).findViewById(R.id.delete_these_tasks);
        ImageButton new_task = ((Activity) myViewHolder.itemView.getContext()).findViewById(R.id.new_task);
        ImageButton choose_all = ((Activity) myViewHolder.itemView.getContext()).findViewById(R.id.choose_all);

        //新建按钮淡入淡出动画
        Animation new_task_in = AnimationUtils.loadAnimation(myViewHolder.itemView.getContext(), R.anim.new_task_in);
        Animation new_task_out = AnimationUtils.loadAnimation(myViewHolder.itemView.getContext(), R.anim.new_task_out);

        //获取屏幕底部导航栏
        @SuppressLint("InternalInsetResource")
        RelativeLayout.LayoutParams task_long_click_barLayoutParams = (RelativeLayout.LayoutParams) task_long_click_bar.getLayoutParams();
        Resources resources = myViewHolder.itemView.getContext().getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        int navigationBarHeight = resources.getDimensionPixelSize(resourceId);

        //长按显示删除多选框
        if (isLongPress) {
            myViewHolder.recycler_check_box.setVisibility(View.VISIBLE);
        } else {
            myViewHolder.recycler_check_box.setVisibility(View.GONE);
            task_long_click_bar.setVisibility(View.GONE);
            if (new_task.getVisibility() == View.GONE) {
                new_task.startAnimation(new_task_in);
                new_task.setVisibility(View.VISIBLE);
            }
        }

        // 选中时更新选中列表
        myViewHolder.itemView.setOnLongClickListener(v -> {
            new CountDownTimer(250, 100) { // 长按计时器
                public void onTick(long millisUntilFinished) {
                    // 倒计时过程中执行的操作（当前为空）
                }
                public void onFinish() {
                    // 长按时间到达后执行的操作
                    if(!isLongPress) {
                        isLongPress = true;
                        task_long_click_bar.setVisibility(View.VISIBLE);
                        selectedList.clear();
                        selectedList.add(dataList.get(position));
                        new_task.startAnimation(new_task_out);
                        new_task.setVisibility(View.GONE);
                        task_long_click_barLayoutParams.bottomMargin += navigationBarHeight;
                        notifyDataSetChanged();
                    }
                }
            }.start();
            return true;
        });

        //点击取消时退出选中状态
        close_long_click.setOnClickListener(v -> {
            isLongPress = false;
            task_long_click_bar.setVisibility(View.GONE);
            task_long_click_barLayoutParams.bottomMargin -= navigationBarHeight;
            notifyDataSetChanged();
        });

        //列表全选
        choose_all.setOnClickListener(v -> {
            if(!isAllChose) {
                selectedList.addAll(dataList);
                isAllChose = true;
                notifyDataSetChanged();
            } else {
                selectedList.clear();
                isAllChose = false;
                notifyDataSetChanged();
            }
        });

        //item点击事件
        myViewHolder.itemView.setOnClickListener(v -> {
            if (isLongPress) {
                if (selectedList.contains(dataList.get(position))) {
                    selectedList.remove(dataList.get(position));
                } else {
                    selectedList.add(dataList.get(position));
                }
                notifyDataSetChanged();
            } else {
                //设置点击进入单独页面查看功能
                Intent intent = new Intent(myViewHolder.itemView.getContext(), ShowPageActivity.class);
                intent.putExtra("id",dataList.get(position).getID());
                intent.putExtra("title",myViewHolder.textViewtitle.getText().toString());
                intent.putExtra("words",myViewHolder.textViewwords.getText().toString());
                intent.putExtra("color",dataList.get(position).getColor());
                intent.putExtra("time",dataList.get(position).getTime());
                //此处用于补充图片等后续功能
                intent.putExtra("backgrounduri",dataList.get(position).getBackgroundUri());
                intent.putExtra("alpha",dataList.get(position).getAlpha());


                myViewHolder.itemView.getContext().startActivity(intent);
            }

        });

        // 重置多选框状态
        myViewHolder.recycler_check_box.setOnCheckedChangeListener(null);
        myViewHolder.recycler_check_box.setChecked(selectedList.contains(dataList.get(position)));
        myViewHolder.recycler_check_box.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedList.add(dataList.get(position));
            } else {
                selectedList.remove(dataList.get(position));
            }
        });

        //确认删除这些任务
        delete_these_tasks.setOnClickListener(v -> {
            TaskOpenHelper myOpenHelper = TaskOpenHelper.getInstance(myViewHolder.itemView.getContext());
            int result = myOpenHelper.deleteTasks(selectedList);
            if (result != 0) {
                // 从dataList中删除选中项
                dataList.removeAll(selectedList);
                // 清空选中列表和id列表
                selectedList.clear();
                new_task.startAnimation(new_task_in);
                new_task.setVisibility(View.VISIBLE);
                task_long_click_barLayoutParams.bottomMargin -= navigationBarHeight;

                // 隐藏多选框
                isLongPress = false;
                task_long_click_bar.setVisibility(View.GONE);
                // 更新RecyclerView的显示
                notifyDataSetChanged();
            } else {
                isLongPress = false;
                task_long_click_bar.setVisibility(View.GONE);
                new_task.startAnimation(new_task_in);
                new_task.setVisibility(View.VISIBLE);
                task_long_click_barLayoutParams.bottomMargin -= navigationBarHeight;
                notifyDataSetChanged();
            }

        });
    }
    @Override
    public int getItemCount() {
        return dataList.size();
    }
}