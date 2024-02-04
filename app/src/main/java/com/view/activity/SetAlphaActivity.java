package com.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lingling.R;
import com.utils.FullScreen;
import com.utils.ScreenSize;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public class SetAlphaActivity extends AppCompatActivity {

    private int REQUEST_CODE_ALPHA = 114514;
    private Uri destinationUri;
    private ImageButton alpha_cancel;
    private ImageButton alpha_sure;
    private ImageView alpha_image;
    private SeekBar alpha_seekbar;
    private int alpha = 255;
    private int lastprogress = 255;
    private int flag = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alpha);

        FullScreen.Fullscreen(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        destinationUri =bundle.getParcelable("destinationUri");

        alpha_cancel = findViewById(R.id.alpha_cancel);
        alpha_sure = findViewById(R.id.alpha_sure);
        alpha_image = findViewById(R.id.alpha_image);
        alpha_seekbar = findViewById(R.id.alpha_seekbar);

        //设置图像框大小
        float height = ScreenSize.getInstance(this).getScreenHeight();
        float width = ScreenSize.getInstance(this).getScreenWidth();
        Resources resources = this.getResources();
        @SuppressLint({"DiscouragedApi", "InternalInsetResource"}) int statusBarHeightId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (statusBarHeightId > 0) {
            height += resources.getDimensionPixelSize(statusBarHeightId);
        }
        height /= 1.5f;
        width /= 1.5f;
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) alpha_image.getLayoutParams();
        layoutParams.height = (int) height;
        layoutParams.width = (int) width;
        alpha_image.setLayoutParams(layoutParams);

        //将图片配置到框框里
        Bitmap croppedBitmap = getCroppedBitmap(Uri.parse(String.valueOf(destinationUri)));
        alpha_image.setImageBitmap(croppedBitmap);

        //透明度拖动条设置
        alpha_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            Drawable forward = getResources().getDrawable(R.mipmap.cat_thumb);
            @SuppressLint("UseCompatLoadingForDrawables")
            Drawable back = getResources().getDrawable(R.mipmap.cat_thumb2);

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress < lastprogress && flag == 1) {
                    flag = 0;
                    alpha_seekbar.setThumb(back);
                } else if (progress > lastprogress && flag == 0) {
                    flag = 1;
                    alpha_seekbar.setThumb(forward);
                }
                lastprogress = progress;
                alpha = progress;
                alpha_image.setImageAlpha(alpha);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //取消调节透明度
        alpha_cancel.setOnClickListener(v -> {
            finish();
        });

        //确定调节透明度
        alpha_sure.setOnClickListener(v -> {
            File outFile = new File(destinationUri.getPath());
            Bitmap bitmap = ((BitmapDrawable) alpha_image.getDrawable()).getBitmap();
            try (OutputStream outputStream = Files.newOutputStream(outFile.toPath())) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Intent resultintent = new Intent();
            resultintent.putExtra("alpha",alpha);
            setResult(RESULT_OK,resultintent);
            finish();
        });

    }


    //获取被裁剪后的bitmap
    private Bitmap getCroppedBitmap(Uri croppedUri) {
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(croppedUri));
            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            // 处理异常情况
            return null;
        }
    }

}