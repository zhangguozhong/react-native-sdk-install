package com.apk.install;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadAndInstall {
    public static int REQUEST_CODE_APP_INSTALL = 0x00002019;
    public static int CANCEL_APP_INSTALL = 0x00002020;
    private static final int DOWN_ERROR = 0x00003;
    private static Handler mHandler;

    @SuppressLint("HandlerLeak")
    public static void start(final String url, final boolean forceUpdate, final Activity activity) {
        if (TextUtils.isEmpty(url)) {
            return;
        }

        final ApkDownloadDialog apkDownloadDialog;
        apkDownloadDialog = new ApkDownloadDialog(activity);
        apkDownloadDialog.setCancelable(false);
        apkDownloadDialog.show();

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case DOWN_ERROR:
                        apkDownloadDialog.dismiss();
                        Toast.makeText(activity.getApplicationContext(), "获取新版本失败！", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        };

        new Thread(){
            @Override
            public void run() {
                try {
                    Looper.prepare();

                    File file = getFileFromServer(url,apkDownloadDialog,activity.getDir("tmp", Context.MODE_PRIVATE));
                    apkDownloadDialog.dismiss();
                    //安装APK
                    installApk(file,activity);

                    Looper.loop();
                }catch (Exception e) {
                    Message msg = new Message();
                    msg.what = DOWN_ERROR;
                    mHandler.sendMessage(msg);
                    e.printStackTrace();
                }
            }}.start();
    }


    private static File getFileFromServer(String path, ApkDownloadDialog pd, File fileCatch) throws Exception {
        try {
            //如果相等的话表示当前的sdcard挂载在手机上并且是可用的
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                URL url = new URL(path);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);

                //获取到文件的大小
                pd.setMax(conn.getContentLength());
                InputStream is = conn.getInputStream();
                File file = new File(Environment.getExternalStorageDirectory(), "app-release.apk");
                FileOutputStream fos = new FileOutputStream(file);
                BufferedInputStream bis = new BufferedInputStream(is);
                byte[] buffer = new byte[1024];
                int len;
                int currentLength = 0;
                while ((len = bis.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                    currentLength += len;
                    pd.setProgress(currentLength);
                }

                fos.close();
                bis.close();
                is.close();
                return file;
            } else {
                URL url = new URL(path);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);

                pd.setMax(conn.getContentLength());
                InputStream is = conn.getInputStream();
                File file = new File(fileCatch, "/app-release.apk");
                String[] command = {"chmod", "777", file.getPath()};
                ProcessBuilder builder = new ProcessBuilder(command);
                try {
                    builder.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                FileOutputStream fos = new FileOutputStream(file);
                BufferedInputStream bis = new BufferedInputStream(is);
                byte[] buffer = new byte[1024];
                int len;
                int currentLength = 0;
                while ((len = bis.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                    currentLength += len;
                    pd.setProgress(currentLength);
                }
                fos.close();
                bis.close();
                is.close();
                return file;
            }
        }catch (Exception e) {
            Log.d("TMS", e.toString());
        }

        return null;
    }

    private static void installApk(File file, Activity activity) {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri contentUri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".fileprovider", file);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            }

            if (activity.getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
                //如果APK安装界面存在，携带请求码跳转。使用forResult是为了处理用户 取消 安装的事件。外面这层判断理论上来说可以不要，但是由于国内的定制，这个加上还是比较保险的
                activity.startActivityForResult(intent,CANCEL_APP_INSTALL);
            }
//            android.os.Process.killProcess(android.os.Process.myPid());//部分机型会导致更新失败，因此去掉
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
