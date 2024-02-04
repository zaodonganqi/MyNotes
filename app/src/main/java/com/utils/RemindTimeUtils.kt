package com.utils

import android.util.Log
import cn.carbswang.android.numberpickerview.library.NumberPickerView
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class RemindTimeUtils {
    var currentTime = 0
    var now_date = 0
    var now_hour = 0
    var now_minute = 0
    var date = arrayOf("00")

    var hours = arrayOf("00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13",
        "14", "15", "16", "17", "18", "19", "20", "21", "22", "23")

    var minutes = arrayOf("00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13",
        "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27",
        "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41",
        "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55",
        "56", "57", "58", "59")
    private val CHINESE_TO_DAY_OF_WEEK_MAP = mapOf(
        "星期一" to DayOfWeek.MONDAY,
        "星期二" to DayOfWeek.TUESDAY,
        "星期三" to DayOfWeek.WEDNESDAY,
        "星期四" to DayOfWeek.THURSDAY,
        "星期五" to DayOfWeek.FRIDAY,
        "星期六" to DayOfWeek.SATURDAY,
        "星期日" to DayOfWeek.SUNDAY
    )


    init {
        val today = LocalDate.now()
        val nextYear = today.plusYears(1)
        val datesList = ArrayList<String>()
        var formatter = DateTimeFormatter.ofPattern("M月d日EEEE")

        var currentDate = today

        while (!currentDate.isAfter(nextYear)) {
            val formattedDate = currentDate.format(formatter)
            datesList.add(formattedDate)
            currentDate = currentDate.plusDays(1)
        }
        this.date = datesList.toTypedArray()
        this.now_date = datesList.size

        formatter = DateTimeFormatter.ofPattern("HH")
        this.now_hour = LocalTime.now().format(formatter).toInt()
        formatter = DateTimeFormatter.ofPattern("mm")
        this.now_minute = LocalTime.now().format(formatter).toInt()
        currentTime = SimpleDateFormat("MMddHHmm", Locale.getDefault()).format(Date()).toInt()

    }

    fun timeToString(mdate: String, mhour: String, mminute: String): String {
        // 获取当前年份
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        // 从原始日期字符串中提取月、日和星期
        val dateParts = mdate.split("月", "日", "星期")
        val month = dateParts[0].padStart(2, '0')
        val day = dateParts[1].padStart(2, '0')
        // 解析小时和分钟
        val hour = mhour.padStart(2, '0')
        val minute = mminute.padStart(2, '0')

        // 获取精确时间
        val chooseTime = (month + day + hour + minute).toInt()

        // 确定日期的年份
        val year = if (chooseTime <= currentTime) {
            currentYear + 1 // 如果日期在当前日期前，使用下一年
        } else {
            currentYear // 否则使用当前年
        }
        // 构建日期字符串
        val resultTime = String.format("%04d-%02d-%02d-%02d-%02d", year, month.toInt(), day.toInt(), hour.toInt(), minute.toInt())
        return resultTime
    }

    fun timeToString(weekdays: List<String>, mhour: String, mminute: String): MutableList<String> {
        var weekStrings: MutableList<String> = mutableListOf("1")
        // 解析小时和分钟
        val hour = mhour.padStart(2, '0')
        val minute = mminute.padStart(2, '0')
        // 计算日期
        weekStrings.remove("1")
        for (item in weekdays){
            weekStrings.add(findNearestDayOfWeek(item, hour, minute))
        }
        return weekStrings
    }

    fun setChoosePicker(choose_date: NumberPickerView, choose_hour: NumberPickerView, choose_minute: NumberPickerView) {
        val times = RemindTimeUtils()
        //日期
        val mdates = times.date
        choose_date.displayedValues = mdates
        choose_date.minValue = 0
        choose_date.maxValue = times.now_date - 1

        //小时
        choose_hour.displayedValues = times.hours
        choose_hour.minValue = 0
        choose_hour.maxValue = 23
        choose_hour.value = times.now_hour

        //分钟
        choose_minute.displayedValues = times.minutes
        choose_minute.minValue = 0
        choose_minute.maxValue = 59
        choose_minute.value = times.now_minute
    }

    fun setChoosePicker(choose_hour: NumberPickerView, choose_minute: NumberPickerView) {
        val times = RemindTimeUtils()

        //小时
        choose_hour.displayedValues = times.hours
        choose_hour.minValue = 0
        choose_hour.maxValue = 23
        choose_hour.value = times.now_hour

        //分钟
        choose_minute.displayedValues = times.minutes
        choose_minute.minValue = 0
        choose_minute.maxValue = 59
        choose_minute.value = times.now_minute
    }

    private fun findNearestDayOfWeek(dayOfWeek: String, mhour: String, mminute: String): String {
        val currentDayOfWeek = LocalDate.now().dayOfWeek
        val targetDay = CHINESE_TO_DAY_OF_WEEK_MAP[dayOfWeek] ?: throw IllegalArgumentException("Invalid Chinese day of week: $dayOfWeek")

        // 计算距离最近的目标星期几的日期
        if (currentDayOfWeek == targetDay) {
            // 如果目标星期和今天相同，返回今天的日期
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            var newdate = LocalDate.now()
            if (mhour.toInt() <= now_hour && mminute.toInt() <= now_minute) {
                newdate = newdate.plusDays(7)
            }
            val result = newdate.format(formatter) + "-" + mhour + "-" + mminute
            return result
        }

        // 计算距离最近的目标星期几的日期
        val daysToAdd = (targetDay.value - currentDayOfWeek.value + 7) % 7
        val nearestDate = LocalDate.now().plusDays(daysToAdd.toLong())
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val result = nearestDate.format(formatter) + "-" + mhour + "-" + mminute
        return result
    }

}
