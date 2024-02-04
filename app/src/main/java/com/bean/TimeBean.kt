package com.bean


class TimeBean(taskID: Int) {
    private var id = -1
    private var taskID = -1
    private var remindTime = ""
    private var repeat = "0" //循环类型：0为一次性，1134表示周一周三周四，并且是循环
    constructor(taskID: Int, remindTime: String, isRepeat: String) : this(taskID) {
        this.taskID = taskID
        this.remindTime = remindTime
        this.repeat = isRepeat
    }

    fun getID(): Int { return this.id }
    fun setTaskID(mtaskID: Int) { this.taskID = mtaskID }
    fun setRemindTime(mremindTime: String) { this.remindTime = mremindTime }
    fun setRepeat(mrepeat: String) { this.repeat = mrepeat }
    fun getTaskID(): Int { return this.taskID }
    fun getRemindTime(): String { return this.remindTime }
    fun getRepeat(): String { return this.repeat }
}