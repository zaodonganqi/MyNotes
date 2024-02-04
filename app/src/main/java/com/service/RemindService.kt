package com.service

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import com.receiver.RemindReceiver
import java.util.Calendar

class RemindService : Service() {
    private var triggerTime: Long = 0
    private var calender = Calendar.getInstance()
    private lateinit var pendingIntent: PendingIntent
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

    }

    override fun onDestroy() {
        super.onDestroy()

    }

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        val mintent = Intent(this, RemindReceiver::class.java)

        val pendingIntent: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // 如果运行在Android 12或更高版本，使用FLAG_IMMUTABLE标志
            PendingIntent.getBroadcast(
                this, 0, mintent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            // 在较低版本上使用旧的标志
            PendingIntent.getBroadcast(
                this, 0, mintent, PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        // 获取AlarmManager实例
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        // 设置定时器，在3秒后触发
        val triggerTime = SystemClock.elapsedRealtime() + 3000 // 3秒后
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerTime,pendingIntent)
        return START_STICKY
    }

}
