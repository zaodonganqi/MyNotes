package com.view.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bean.RemindBean
import com.example.lingling.R
import com.utils.ScreenSize

class DialogReminder {

    fun customDialog(context: Context, remindBean: RemindBean) {
        var dialog = Dialog(context)
        var view = View.inflate(context, R.layout.activity_remind_dialog,null)
        var title = view.findViewById<TextView>(R.id.remind_text)
        var image = view.findViewById<ImageView>(R.id.remind_image)
        var remind_ok = view.findViewById<TextView>(R.id.remind_ok)
        dialog.setContentView(view)
        dialog.setCanceledOnTouchOutside(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dialogContainer = dialog.findViewById<FrameLayout>(R.id.remind_dialog)
        dialogContainer.background = ContextCompat.getDrawable(context, R.drawable.dialog_background)
        view.minimumHeight = WindowManager.LayoutParams.WRAP_CONTENT
        val dialogWindow = dialog.window
        val lp = dialogWindow!!.attributes
        lp.width = (ScreenSize.getInstance(context).screenWidth * 0.80f).toInt()
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        dialogWindow!!.attributes = lp
        //设置标题
        title.text = remindBean.getTitle()

        dialog.show()
    }
}