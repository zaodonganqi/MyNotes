package com.view.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bean.TaskBean;
import com.example.lingling.R;
import com.helper.SoftKeyboardStateHelper;
import com.helper.TaskOpenHelper;
import com.utils.FullScreen;
import com.utils.ScreenSize;
import com.view.menu.ShowPageMenu;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;

public class ShowPageActivity extends AppCompatActivity {
    Context context;
    private Uri mSelectedImageUri;
    private Uri destinationUri;
    private int REQUEST_CODE_SELECT_IMAGE = 123;
    private int REQUEST_CODE_CROP = 321;
    private int REQUEST_CODE_ALPHA = 114514;
    private int id;
    private String title;
    private String words;
    private String color;
    private String time;
    private String backgrounduri;
    private int alpha;
    private RelativeLayout showpage;
    private ImageButton itemshowpage_back_to_main;
    private ImageButton up_to_top;
    private ImageButton showpage_setting;
    private ScrollView item_scrollview;
    private EditText item_title;
    private EditText item_words;
    private TextView new_title;
    private TextView item_time;

    @SuppressLint({"ClickableViewAccessibility", "CutPasteId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_show_page);

        FullScreen.Fullscreen(this);

        context = getApplicationContext();

        showpage = findViewById(R.id.showpage);
        itemshowpage_back_to_main = findViewById(R.id.itemshowpage_back_to_main);
        up_to_top = findViewById(R.id.up_to_top);
        showpage_setting = findViewById(R.id.showpage_setting);
        item_title = findViewById(R.id.item_title);
        item_words = findViewById(R.id.item_words);
        new_title = findViewById(R.id.new_title);
        item_time = findViewById(R.id.item_time);

        //返回主页
        itemshowpage_back_to_main.setOnClickListener(v -> finish());

        //获取各项内容
        Intent intent = getIntent();
        id = intent.getIntExtra("id",-1);
        title = intent.getStringExtra("title");
        words = intent.getStringExtra("words");
        color = intent.getStringExtra("color");
        time = intent.getStringExtra("time");
        backgrounduri = intent.getStringExtra("backgrounduri");
        alpha = intent.getIntExtra("alpha",255);

        //设置各项内容
        item_title.setText(title);
        item_words.setText(words);
        if (backgrounduri != null) {
            // 异步加载裁剪后的图片
            AsyncTask.execute(() -> {
                Bitmap croppedBitmap = getCroppedBitmap(Uri.parse(backgrounduri));
                // 在主线程中更新UI
                runOnUiThread(() -> {
                    if (croppedBitmap != null) {
                        Drawable drawable = new BitmapDrawable(getResources(), croppedBitmap);
                        drawable.setAlpha(alpha);
                        showpage.setBackground(drawable);
                    }
                });
            });
        }
        item_time.setText(time);
        new_title.setText(title);
        if (Objects.equals(color, "2")) {
            int alpha = Color.alpha(new_title.getCurrentTextColor()); // 保存当前文字透明度
            item_title.setTextColor(Color.parseColor("#CC000000"));
            item_words.setTextColor(Color.parseColor("#CC000000"));
            new_title.setTextColor(Color.parseColor("#CC000000"));
            new_title.setTextColor(new_title.getTextColors().withAlpha(alpha)); // 应用保存的透明度
        } else if (Objects.equals(color, "-2")) {
            int alpha = Color.alpha(new_title.getCurrentTextColor()); // 保存当前文字透明度
            item_title.setTextColor(Color.parseColor("#F4F4F3"));
            item_words.setTextColor(Color.parseColor("#F4F4F3"));
            new_title.setTextColor(Color.parseColor("#F4F4F3"));
            new_title.setTextColor(new_title.getTextColors().withAlpha(alpha)); // 应用保存的透明度
        }
        new_title.setTextColor(new_title.getTextColors().withAlpha(0));

        //右上角展开框设置
        showpage_setting.setOnClickListener(v -> {

            ShowPageMenu menu = new ShowPageMenu();
            int layoutX = (int) showpage_setting.getX();
            int layoutY = (int) showpage_setting.getY();
            TaskBean menutask = new TaskBean(id,title,words,time);
            menu.showMenu(this,this,layoutX,layoutY,id,item_title,item_words,new_title,menutask);

        });

        // 设置标题的文本变化监听
        item_title.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                TaskOpenHelper myOpenHelper = TaskOpenHelper.getInstance(context);
                new_title.setText(item_title.getText().toString());
                TaskBean tasks = new TaskBean(id, item_title.getText().toString(), item_words.getText().toString(), item_time.getText().toString());
                myOpenHelper.update(tasks);
            }

        });

        // 设置文字的文本变化监听
        item_words.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                TaskOpenHelper myOpenHelper = TaskOpenHelper.getInstance(context);
                TaskBean tasks = new TaskBean(id, item_title.getText().toString(), item_words.getText().toString(), item_time.getText().toString());
                new_title.setText(item_title.getText().toString());
                myOpenHelper.update(tasks);
            }
        });


        item_scrollview = findViewById(R.id.item_scrollview);
        int[] titleLocation = new int[2];
        item_title.getLocationOnScreen(titleLocation);

        //设置滚动监听
        item_scrollview.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            int height = item_title.getHeight();
            if(scrollY >=50 && scrollY <= 115 + (item_title.getLineCount() - 1) * item_title.getLineHeight()) {
                double Y = (double) scrollY;
                double newalpha1 = 255 * (1.0 - (Y - 50) / (65 + (item_title.getLineCount() - 1) * item_title.getLineHeight()));
                int newalpha10 = (int) newalpha1;
                item_title.setTextColor(item_title.getTextColors().withAlpha(newalpha10));
            } else if (scrollY > 115) {
                item_title.setTextColor(item_title.getTextColors().withAlpha(0));
            } else if (scrollY < 50) {
                item_title.setTextColor(item_title.getTextColors().withAlpha(255));
            }

            if(scrollY >=90 && scrollY <= 170 + (item_title.getLineCount() - 1) * item_title.getLineHeight()) {
                double Y = (double) scrollY;
                double newalpha2 = 255 * ((Y - 90) / (80 + (item_title.getLineCount() - 1) * item_title.getLineHeight()));
                int newalpha20 = (int) newalpha2;
                new_title.setTextColor(new_title.getTextColors().withAlpha(newalpha20));
            } else if (scrollY > 170) {
                new_title.setTextColor(new_title.getTextColors().withAlpha(255));
            } else if (scrollY < 90) {
                new_title.setTextColor(new_title.getTextColors().withAlpha(0));
            }

            if(scrollY <=160) {
                up_to_top.setVisibility(View.GONE);
            }else {
                up_to_top.setVisibility(View.VISIBLE);
            }

        });

        //设置标题的点击回到顶部功能
        new_title.setOnClickListener(v -> {
            item_scrollview.smoothScrollTo(0,0);
            item_scrollview.getViewTreeObserver().addOnScrollChangedListener(() -> {
                int scrollY = item_scrollview.getScrollY();
                if (scrollY == 0) {
                    // 在顶部，设置焦点到 EditText
                    item_title.requestFocus();
                    item_title.setSelection(item_title.getText().length());
                }
            });
        });

        //设置回到顶部按钮的回顶功能
        up_to_top.setOnClickListener(v -> {
            item_scrollview.smoothScrollTo(0,0);
        });

        //监听软键盘
        SoftKeyboardStateHelper softKeyboardStateHelper = new SoftKeyboardStateHelper(findViewById(R.id.showpage));
        softKeyboardStateHelper.addSoftKeyboardStateListener(new SoftKeyboardStateHelper.SoftKeyboardStateListener() {
            @Override
            public void onSoftKeyboardOpened(int keyboardHeightInPx) {
                //键盘打开
            }
            @Override
            public void onSoftKeyboardClosed() {
                //键盘关闭
                item_title.clearFocus();
                item_words.clearFocus();
            }
        });

    }


    // 处理选择图片后的裁剪事件
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //图片选择好了，进入裁剪
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            mSelectedImageUri = data.getData();
            startCropActivity(mSelectedImageUri);
            //裁剪好了，进入透明度调节
        } else if (requestCode == REQUEST_CODE_CROP && resultCode == RESULT_OK && data != null) {
            startSetAlpha(destinationUri);
            //直接关闭裁剪，删除被保存的图片
        } else if (requestCode == REQUEST_CODE_CROP && resultCode == 0) {
            File file = new File(destinationUri.getPath());
            if (file.exists()) {
                file.delete();
            }
            //透明度调节成功，保存图片并设置
        } else if (requestCode == REQUEST_CODE_ALPHA && resultCode == RESULT_OK && data != null) {
            if(!Objects.equals(backgrounduri, String.valueOf(destinationUri)) && backgrounduri != null) {
                File file = new File(Uri.parse(backgrounduri).getPath());
                if (file.exists()) {
                    file.delete();
                }
            }
            int alpha = data.getIntExtra("alpha",255);
            TaskOpenHelper myOpenHelper = TaskOpenHelper.getInstance(context);
            int result = myOpenHelper.updatebackground(id,destinationUri);
            result = myOpenHelper.updatealpha(id,alpha);
            backgrounduri = String.valueOf(destinationUri);
            Bitmap croppedBitmap = getCroppedBitmap(destinationUri);
            Drawable drawable = new BitmapDrawable(getResources(), croppedBitmap);
            drawable.setAlpha(alpha);
            showpage.setBackground(drawable);
            //没调透明度，删除图片
        } else if (requestCode == REQUEST_CODE_ALPHA && resultCode == 0) {
            File file = new File(destinationUri.getPath());
            if (file.exists()) {
                file.delete();
            }
        }
    }

    //进入裁剪
    private void startCropActivity(Uri sourceUri) {
        File file = new File(getFilesDir(), "pictures"); // 在内部存储根目录下创建名为"pictures"的文件夹
        if (!file.exists()) {
            file.mkdirs();
        }
        File outFile = new File(file, System.currentTimeMillis() + ".jpg");
        destinationUri = Uri.fromFile(outFile);
        UCrop uCrop = UCrop.of(sourceUri, destinationUri);
        UCrop.Options options = new UCrop.Options();
        options.setToolbarTitle("裁剪");
        options.setHideBottomControls(true);
        options.setFreeStyleCropEnabled(false);
        options.setCompressionQuality(50);
        uCrop.withOptions(options);
        float height = ScreenSize.getInstance(context).getScreenHeight();
        float width = ScreenSize.getInstance(context).getScreenWidth();
        Resources resources = context.getResources();
        @SuppressLint({"DiscouragedApi", "InternalInsetResource"}) int statusBarHeightId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (statusBarHeightId > 0) {
            height += resources.getDimensionPixelSize(statusBarHeightId);
        }
        height /= 2.0f;
        width /= 2.0f;
        uCrop.withAspectRatio(width, height);
        uCrop.start(this, REQUEST_CODE_CROP);
    }

    //获取被裁剪后的bitmap
    private Bitmap getCroppedBitmap(Uri croppedUri) {
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(croppedUri));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos);
            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            // 处理异常情况
            return null;
        }
    }

    //进入透明度调节
    private void startSetAlpha(Uri croppedUri) {
        Intent intent = new Intent(this, SetAlphaActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("destinationUri",destinationUri);
        intent.putExtras(bundle);
        startActivityForResult(intent,REQUEST_CODE_ALPHA);
    }
}