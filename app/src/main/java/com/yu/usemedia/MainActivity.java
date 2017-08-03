package com.yu.usemedia;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    // 拍照
    private static final int REQUEST_CODE_TAKE_PIC = 1;
    // 选择图片
    private static final int REQUEST_CODE_CHOOSE_PHOTO = 2;

    private Uri imgUri;
    private ImageView mImgIv;
    File mOutputImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImgIv = (ImageView) findViewById(R.id.imageView_reault);
    }

    /**
     * 发生通知
     *
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
     *
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
        startActivityForResult(intent, REQUEST_CODE_TAKE_PIC);
    }


    /**
     * 打开相册
     *
     * @param view
     */
    public void openAlbum(View view) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            Intent intent = new Intent("android.intent.action.GET_CONTENT");
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_CODE_CHOOSE_PHOTO);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent("android.intent.action.GET_CONTENT");
                    intent.setType("image/*");
                    startActivityForResult(intent, REQUEST_CODE_CHOOSE_PHOTO);
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_TAKE_PIC:
                if (resultCode == RESULT_OK) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imgUri));
                        //                    Bitmap bitmap = BitmapFactory.decodeFile(mOutputImage.getPath(), new BitmapFactory.Options());
                        if (bitmap == null) {
                            Toast.makeText(this, "btimap is null", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(this, "btimap is not null", Toast.LENGTH_SHORT).show();
                        mImgIv.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }

                break;
            case REQUEST_CODE_CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    // 在kitkat及以上版本，>=19
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        handImageOnKitkat(data);
                    } else {
                        handImageBeforeKitkat(data);

                    }
                }
                break;
        }
    }

    /**
     * 处理图片，api<19
     *
     * @param data
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void handImageBeforeKitkat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    /**
     * 处理图片请求，api>=19
     *
     * @param data
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void handImageOnKitkat(Intent data) {
        String imgPath = null;
        Uri uri = data.getData();
        Uri treeUri = data.getData();
        //Log.d("TAG", "handleImageOnKitKat: uri is " + uri);
        if (DocumentsContract.isDocumentUri(this, uri)) {
            //Log.e("TAG", "isDocumentUri");
            // 如果是document类型的Uri，则通过document id处理
            String documentId = DocumentsContract.getDocumentId(uri);
           // Log.e("TAG", "documentId="+documentId);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = documentId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imgPath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(documentId));
                imgPath = getImagePath(contentUri, null);
            } else if ("com.android.externalstorage.documents".equals(uri.getAuthority())) { // 外置sd卡
                final String[] split = documentId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type))
                    imgPath = Environment.getExternalStorageDirectory() + "/" + split[1];
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imgPath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imgPath = uri.getPath();
        }
        displayImage(imgPath); // 根据图片路径显示图片
    }

    /**
     * 显示图片
     *
     * @param imgPath
     */
    private void displayImage(String imgPath) {
        if (imgPath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
            mImgIv.setImageBitmap(bitmap);
            Log.e("TAG", "imgPath=" + imgPath);
        } else {
            Toast.makeText(this, "imgPath is null!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 获取图片路径
     * @param uri
     * @param selection
     * @return
     */
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
}
