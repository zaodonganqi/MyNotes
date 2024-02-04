package com.view.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bean.TaskBean
import com.example.lingling.R
import com.helper.SoftKeyboardStateHelper
import com.helper.SoftKeyboardStateHelper.SoftKeyboardStateListener
import com.helper.TaskOpenHelper
import com.utils.FullScreen
import com.utils.ScreenSize
import com.view.menu.NewTaskColor
import com.view.menu.NewTaskMenu
import com.yalantis.ucrop.UCrop
import java.io.File
import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import java.util.*

class NewTaskActivity : NewTaskColor, AppCompatActivity() {
    var context: Context? = null
    private lateinit var mSelectedImageUri: Uri
    private lateinit var destinationUri: Uri
    private val REQUEST_CODE_SELECT_IMAGE = 123
    private val REQUEST_CODE_CROP = 321
    private val REQUEST_CODE_ALPHA = 114514
    private lateinit var title: String
    private lateinit var words: String
    private var color: String = "2"
    private lateinit var time: String
    private var backgrounduri: String = "-1"
    private var alpha = 0
    private lateinit var taskBean:TaskBean
    private lateinit var new_task_page: RelativeLayout
    private lateinit var new_task_back_to_main: ImageButton
    private lateinit var new_task_up_to_top: ImageButton
    private lateinit var new_task_setting: ImageButton
    private lateinit var new_task_scrollview: ScrollView
    private lateinit var new_task_edit_title: EditText
    private lateinit var new_task_words: EditText
    private lateinit var new_task_title: TextView
    private lateinit var new_task_time: TextView
    private lateinit var new_task_ok: ImageButton

    @SuppressLint("ClickableViewAccessibility", "CutPasteId", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_task)
        FullScreen.Fullscreen(this)
        context = applicationContext
        new_task_page = findViewById<RelativeLayout>(R.id.new_task_page)
        new_task_back_to_main = findViewById<ImageButton>(R.id.new_task_back_to_main)
        new_task_setting = findViewById<ImageButton>(R.id.new_task_setting)
        new_task_edit_title = findViewById<EditText>(R.id.new_task_edit_title)
        new_task_words = findViewById<EditText>(R.id.new_task_words)
        new_task_title = findViewById<TextView>(R.id.new_task_title)
        new_task_time = findViewById<TextView>(R.id.new_task_time)
        new_task_ok = findViewById<ImageButton>(R.id.new_task_ok)
        new_task_up_to_top = findViewById(R.id.new_task_up_to_top)

        //返回主页
        new_task_back_to_main.setOnClickListener{
            if (backgrounduri != "-1") {
                val file = Uri.parse(backgrounduri).path?.let { File(it) }
                if (file != null) {
                    if (file.exists()) {
                        file.delete()
                    }
                }
            }
            finish()
        }

        //设置默认标题
        title = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        new_task_title.text = title
        time = SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.getDefault()).format(Date())
        new_task_time.text = time


        //设置各项内容
        new_task_edit_title.setText(title)
        words = ""
        new_task_words.setText(words)
        this.taskBean = TaskBean(title,words,time)
        if (backgrounduri != "-1") {
            // 异步加载裁剪后的图片
            AsyncTask.execute {
                val croppedBitmap = getCroppedBitmap(Uri.parse(backgrounduri))
                // 在主线程中更新UI
                runOnUiThread {
                    if (croppedBitmap != null) {
                        val drawable: Drawable = BitmapDrawable(resources, croppedBitmap)
                        drawable.alpha = alpha
                        new_task_page.background = drawable
                    }
                }
            }
        }
        new_task_time.text = time
        new_task_title.text = title
        new_task_title.setTextColor(new_task_title.textColors.withAlpha(0))

        //右上角展开框设置
        new_task_setting.setOnClickListener {
            val menu = NewTaskMenu()
            menu.setNewTaskColor(this)
            val layoutX = new_task_setting.x.toInt()
            val layoutY = new_task_setting.y.toInt()
            menu.customDialog(
                this,
                this,
                layoutX,
                layoutY,
                color,
                new_task_edit_title,
                new_task_words,
                new_task_title,
            )
        }

        //确认添加任务
        new_task_ok.setOnClickListener {
            this.taskBean.title = this.title
            this.taskBean.words = this.words
            this.taskBean.time = this.time
            this.taskBean.color = this.color
            this.taskBean.backgroundUri = this.backgrounduri
            this.taskBean.alpha = this.alpha
            val myOpenHelper = TaskOpenHelper.getInstance(context)
            myOpenHelper.insert(this.taskBean)
            myOpenHelper.close()
            finish()
        }

        // 设置标题的文本变化监听
        new_task_edit_title.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                title = new_task_edit_title.text.toString()
                new_task_title.text = title
            }
        })

        // 设置文字的文本变化监听
        new_task_words.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                title = new_task_edit_title.text.toString()
                new_task_title.text = title
                words = new_task_words.text.toString()
            }
        })
        new_task_scrollview = findViewById<ScrollView>(R.id.new_task_scrollview)
        val titleLocation = IntArray(2)
        new_task_edit_title.getLocationOnScreen(titleLocation)

        //设置滚动监听
        new_task_scrollview.setOnScrollChangeListener{ v: View?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            val height = new_task_edit_title.height
            if (scrollY >= 50 && scrollY <= 115 + (new_task_edit_title.lineCount - 1) * new_task_edit_title.lineHeight) {
                val Y = scrollY.toDouble()
                val newalpha1 = 255 * (1.0 - (Y - 50) / (65 + (new_task_edit_title.lineCount - 1) * new_task_edit_title.lineHeight))
                val newalpha10 = newalpha1.toInt()
                new_task_edit_title.setTextColor(new_task_edit_title.textColors.withAlpha(newalpha10))
            } else if (scrollY > 115) {
                new_task_edit_title.setTextColor(new_task_edit_title.textColors.withAlpha(0))
            } else if (scrollY < 50) {
                new_task_edit_title.setTextColor(new_task_edit_title.textColors.withAlpha(255))
            }
            if (scrollY >= 90 && scrollY <= 170 + (new_task_edit_title.lineCount - 1) * new_task_edit_title.lineHeight) {
                val Y = scrollY.toDouble()
                val newalpha2 = 255 * ((Y - 90) / (80 + (new_task_edit_title.lineCount - 1) * new_task_edit_title.lineHeight))
                val newalpha20 = newalpha2.toInt()
                new_task_title.setTextColor(new_task_title.textColors.withAlpha(newalpha20))
            } else if (scrollY > 170) {
                new_task_title.setTextColor(new_task_title.textColors.withAlpha(255))
            } else if (scrollY < 90) {
                new_task_title.setTextColor(new_task_title.textColors.withAlpha(0))
            }
            if (scrollY <= 160) {
                new_task_up_to_top.visibility = View.GONE
            } else {
                new_task_up_to_top.visibility = View.VISIBLE
            }
        }

        //设置标题的点击回到顶部功能
        new_task_title.setOnClickListener{
            new_task_scrollview.smoothScrollTo(0, 0)
            new_task_scrollview.viewTreeObserver.addOnScrollChangedListener {
                val scrollY = new_task_scrollview.scrollY
                if (scrollY == 0) {
                    // 在顶部，设置焦点到 EditText
                    new_task_edit_title.requestFocus()
                    new_task_edit_title.setSelection(new_task_edit_title.text.length)
                }
            }
        }

        //设置回到顶部按钮的回顶功能

        //设置回到顶部按钮的回顶功能
        new_task_up_to_top.setOnClickListener{
            new_task_scrollview.smoothScrollTo(0, 0)
        }

        //监听软键盘
        val softKeyboardStateHelper = SoftKeyboardStateHelper(findViewById<View>(R.id.new_task_page))
        softKeyboardStateHelper.addSoftKeyboardStateListener(object :
            SoftKeyboardStateListener {
            override fun onSoftKeyboardOpened(keyboardHeightInPx: Int) {
                //键盘打开
            }

            override fun onSoftKeyboardClosed() {
                //键盘关闭
                new_task_edit_title.clearFocus()
                new_task_words.clearFocus()
            }
        })
    }

    // 获取文字颜色接口
    override fun setColor(color: String) {
        this.color = color
    }

    // 处理选择图片后的裁剪事件
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //图片选择好了，进入裁剪
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            mSelectedImageUri = data.data!!
            startCropActivity(mSelectedImageUri)
            //裁剪好了，进入透明度调节
        } else if (requestCode == REQUEST_CODE_CROP && resultCode == RESULT_OK && data != null) {
            startSetAlpha(destinationUri)
            //直接关闭裁剪，删除被保存的图片
        } else if (requestCode == REQUEST_CODE_CROP && resultCode == 0) {
            val file = destinationUri!!.path?.let { File(it) }
            if (file != null) {
                if (file.exists()) {
                    file.delete()
                }
            }
            //透明度调节成功，保存图片并设置
        } else if (requestCode == REQUEST_CODE_ALPHA && resultCode == RESULT_OK && data != null) {
            if (backgrounduri != destinationUri.toString()) {
                val file = Uri.parse(backgrounduri).path?.let { File(it) }
                if (file != null) {
                    if (file.exists()) {
                        file.delete()
                    }
                }
            }
            val alpha = data.getIntExtra("alpha", 255)
            this.backgrounduri = destinationUri.toString()
            val croppedBitmap = getCroppedBitmap(destinationUri)
            val drawable: Drawable = BitmapDrawable(resources, croppedBitmap)
            drawable.alpha = alpha
            this.alpha = alpha
            new_task_page.background = drawable
            //没调透明度，删除图片
        } else if (requestCode == REQUEST_CODE_ALPHA && resultCode == 0) {
            val file = destinationUri.path?.let { File(it) }
            if (file != null) {
                if (file.exists()) {
                    file.delete()
                }
            }
        }
    }

    //进入裁剪
    private fun startCropActivity(sourceUri: Uri?) {
        val file = File(context?.getExternalFilesDir(null), "pictures") // 在内部存储根目录下创建名为"pictures"的文件夹
        if (!file.exists()) {
            file.mkdirs()
        }
        val outFile = File(file, System.currentTimeMillis().toString() + ".jpg")
        destinationUri = Uri.fromFile(outFile)
        val uCrop = UCrop.of(sourceUri!!, destinationUri)
        val options = UCrop.Options()
        options.setToolbarTitle("裁剪")
        options.setHideBottomControls(true)
        options.setFreeStyleCropEnabled(false)
        uCrop.withOptions(options)
        var height = ScreenSize.getInstance(context).screenHeight.toFloat()
        var width = ScreenSize.getInstance(context).screenWidth.toFloat()
        val resources = context!!.resources
        @SuppressLint("DiscouragedApi", "InternalInsetResource") val statusBarHeightId =
            resources.getIdentifier("status_bar_height", "dimen", "android")
        if (statusBarHeightId > 0) {
            height += resources.getDimensionPixelSize(statusBarHeightId).toFloat()
        }
        height /= 2.0f
        width /= 2.0f
        uCrop.withAspectRatio(width, height)
        uCrop.start(this, REQUEST_CODE_CROP)
    }

    //获取被裁剪后的bitmap
    private fun getCroppedBitmap(croppedUri: Uri?): Bitmap? {
        return try {
            BitmapFactory.decodeStream(contentResolver.openInputStream(croppedUri!!))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            // 处理异常情况
            null
        }
    }

    //进入透明度调节
    private fun startSetAlpha(croppedUri: Uri?) {
        val intent = Intent(this, SetAlphaActivity::class.java)
        val bundle = Bundle()
        bundle.putParcelable("destinationUri", destinationUri)
        intent.putExtras(bundle)
        startActivityForResult(intent, REQUEST_CODE_ALPHA)
    }

}