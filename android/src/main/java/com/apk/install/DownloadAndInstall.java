package com.apk.install;
import android.Manifest;
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
import android.provider.Settings;
import android.support.annotation.RequiresApi;
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
    private static String application_id = null;
    private static File file = null;

    @SuppressLint("HandlerLeak")
    public static void start(final String url, final boolean forceUpdate, final Activity activity, final String applicationId) {
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(applicationId)) {
            return;
        }

        application_id = applicationId;
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

                    file = getFileFromServer(url,apkDownloadDialog,activity.getDir("tmp", Context.MODE_PRIVATE));
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

    /**
     * 开启设置安装未知来源应用权限界面
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void startInstallPermissionSettingActivity(Context context) {
        if (context == null){
            return;
        }

        Uri packageUri = Uri.parse("package:" + context.getPackageName());
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,packageUri);
        ((Activity)context).startActivityForResult(intent,REQUEST_CODE_APP_INSTALL);
    }

    /*
     *
     * 判断是否是8.0,8.0需要处理未知应用来源权限问题,否则直接安装
     */
    private static void checkIsAndroidO(final File file,final Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            boolean b = activity.getPackageManager().canRequestPackageInstalls();
            if (b) {
                installApk(file, activity);
            } else {
//                new AlertDialog.Builder(activity)
//                        .setTitle("温馨提示")
//                        .setMessage("安装应用需要打开未知来源权限，请去设置中开启应用权限，以允许安装来自此来源的应用")
//                        .setNeutralButton("取消", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                dialogInterface.dismiss();
//                            }
//                        })
//                        .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                                    startInstallPermissionSettingActivity(activity);
//                                }
//                            }
//                        }).show();

                //请求安装未知应用来源的权限
                PermissionUtils.requestPermissions(activity, 0x11, new String[]{
                        Manifest.permission.REQUEST_INSTALL_PACKAGES}, new PermissionUtils.OnPermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startInstallPermissionSettingActivity(activity);
                        }else {
                            installApk(file,activity);
                        }
                    }

                    @Override
                    public void onPermissionDenied(String[] deniedPermissions) {
                        Toast.makeText(activity, "权限不够,无法下载！", Toast.LENGTH_SHORT).show();
                    }});
            }
        } else {
            installApk(file, activity);
        }
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
                Uri contentUri = FileProvider.getUriForFile(activity, application_id + ".fileprovider", file);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            }

            if (activity.getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
                //如果APK安装界面存在，携带请求码跳转。使用forResult是为了处理用户 取消 安装的事件。外面这层判断理论上来说可以不要，但是由于国内的定制，这个加上还是比较保险的
                activity.startActivityForResult(intent,CANCEL_APP_INSTALL);
            }
            android.os.Process.killProcess(android.os.Process.myPid());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
