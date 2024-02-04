package com.receiver

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Button
import com.example.lingling.R
import com.service.RemindService

class RemindReceiver: BroadcastReceiver() {
    @SuppressLint("InflateParams")
    override fun onReceive(context: Context, intent: Intent) {

        val outMetrics = DisplayMetrics()
        val mwindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        mwindowManager.defaultDisplay.getMetrics(outMetrics)
        val layoutParam = WindowManager.LayoutParams().apply {
            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            format = PixelFormat.RGBA_8888
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
            //位置大小设置
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            //设置剧中屏幕显示
            x = 0
            y = 0
        }
        val floatingView = LayoutInflater.from(context).inflate(R.layout.activity_remind_dialog, null)
        val remind_ok = floatingView.findViewById<Button>(R.id.remind_ok)
        remind_ok.setOnClickListener {
            if (floatingView != null) {
                mwindowManager.removeView(floatingView)
                val serviceIntent  =Intent(context, RemindService::class.java)
                context.stopService(serviceIntent)
                abortBroadcast()
            }
        }
        mwindowManager.addView(floatingView,layoutParam)
    }

}