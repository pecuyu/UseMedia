package com.yu.usemedia;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private Uri imgUri;
    private ImageView imgIv;
    File mOutputImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgIv = (ImageView) findViewById(R.id.imageView_reault);
    }

    /**
     * 发生通知
     * @param view
     */
    public void sendNotification(View view) {
        NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://www.sogou.com"));  // 点击打开网页
        // 即将发生的意图
        PendingIntent pi = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_ONE_SHOT);
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("this is title")
                .setContentText("this is text")
                .setSmallIcon(R.mipmap.ic_launcher)  // 设置显示到状态栏的小图标
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)) //  下拉后显示的大图标
                .setWhen(System.currentTimeMillis()) // 设置发生时间
                .setContentIntent(pi)    // 设置PendingIntent
                .setAutoCancel(true)    // 设置自动取消
                //.setSound(Uri.fromFile(new File("/system/media/audio/ringtones/Luna.ogg"))) // 设置发生通知的时间
                //.setVibrate(new long[]{0, 1000, 1000, 1000})   // 设置振动，需要权限 <uses-permission android:name="android.permission.VIBRATE" />
                // .setLights(Color.GREEN, 1000, 1000)   // 设置灯光闪烁,参数：颜色，灯亮时间，灯灭时间
                .setDefaults(NotificationCompat.DEFAULT_ALL)  // 使用默认效果
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)))
                .setPriority(NotificationCompat.PRIORITY_MAX)  // 设置优先级
                .build();

        manager.notify(1, notification);

        // manager.cancel(id);  // 在某处取消通知的一种方式
    }

    /**
     * 拍照
     * @param view
     */
    public void takePicture(View view) {
        // 保存拍照后的图片
        mOutputImage = new File(getExternalCacheDir(), "IMG_" + new Date().toString() + ".jpg");
        if (mOutputImage.exists()) {
            mOutputImage.delete();
        }
        try {
            mOutputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 判断版本号，小于24用旧的方式
        if (Build.VERSION.SDK_INT < 24) {
            imgUri = Uri.fromFile(mOutputImage);
        } else {
            imgUri = FileProvider.getUriForFile(this, "com.yu.usemedia.fileprovider", mOutputImage);
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        startActivityForResult(intent, 1);
    }


    /**
     * 打开相册
     * @param view
     */
    public void openAlbum(View view) {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivity(intent);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                Bitmap bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imgUri));
//                    Bitmap bitmap = BitmapFactory.decodeFile(mOutputImage.getPath(), new BitmapFactory.Options());
                    if (bitmap == null) {
                        Toast.makeText(this, "btimap is null", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(this, "btimap is not null", Toast.LENGTH_SHORT).show();
                    imgIv.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                break;
        }
    }
}
