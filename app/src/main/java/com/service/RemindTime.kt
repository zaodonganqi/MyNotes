package com.service

import android.app.AlarmManager
import android.app.PendingIntent

interface RemindTime {

    fun getTime(time: Long)

    fun getPendingIntent(pendingIntent: PendingIntent)

}