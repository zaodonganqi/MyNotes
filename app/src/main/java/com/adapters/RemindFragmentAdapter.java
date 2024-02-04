package com.adapters;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.view.fragment.RemindFragment1;
import com.view.fragment.RemindFragment2;
import com.view.fragment.RemindFragment3;

public class RemindFragmentAdapter extends FragmentStateAdapter {

    RemindFragment1 remindFragment1 = new RemindFragment1();
    RemindFragment2 remindFragment2 = new RemindFragment2();
    int rfid;
    Context rfcontext;
    Dialog rfdialog;
    RemindFragment3 remindFragment3 = new RemindFragment3();

    public RemindFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return RemindFragment1.newInstance(rfid, rfcontext, rfdialog);
            case 1:
                return RemindFragment2.newInstance(rfid, rfcontext, rfdialog);
            case 2:
                return remindFragment3;
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public void setRFDatas(int id, Context context, Dialog dialog) {
        this.rfid = id;
        this.rfcontext = context;
        this.rfdialog = dialog;
    }
}
