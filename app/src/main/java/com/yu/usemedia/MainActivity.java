package com.yu.usemedia;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.view.View;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

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
                .setSound(Uri.fromFile(new File("/system/media/audio/ringtones/Luna.ogg"))) // 设置发生通知的时间
                //        .setVibrate(new long[]{0, 1000, 1000, 1000})   // 设置振动，需要权限 <uses-permission android:name="android.permission.VIBRATE" />
                //        .setLights(Color.GREEN, 1000, 1000)   // 设置灯光闪烁
                .build();

        manager.notify(1, notification);

        // manager.cancel(id);  // 在某处取消通知的一种方式
    }
}
