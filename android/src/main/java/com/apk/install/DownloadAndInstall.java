package com.apk.install;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.facebook.react.bridge.UiThreadUtil.runOnUiThread;

public class DownloadAndInstall {

    public static void start(final String url, final Activity activity, final ReactApplicationContext reactContext) {
        if (url == null) {
            return;
        }
        sendEvent(reactContext,"progressWillShow",null); //弹出进度条对话框

        new Thread(){
            @Override
            public void run() {
                try {
                    File file = getFileFromServer(url,reactContext);
                    //安装APK
                    installApk(file,activity);
                    sendEvent(reactContext,"progressWillHide",null); //结束掉进度条对话框
                }catch (Exception e) {
                }
            }}.start();
    }

    private static File getFileFromServer(String path, final ReactApplicationContext reactContext) throws Exception {
        //如果相等的话表示当前的sdcard挂载在手机上并且是可用的
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);

            //获取到文件的大小
            int contentLength = conn.getContentLength();
            InputStream is = conn.getInputStream();
            File file = new File(Environment.getExternalStorageDirectory(), "app-release.apk");
            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int len ;
            int currentLength = 0;
            while((len = bis.read(buffer)) != -1){
                fos.write(buffer, 0, len);
                currentLength += len;
                final int progress = currentLength / contentLength;

                //获取当前下载量
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        WritableMap params = Arguments.createMap();
                        params.putDouble("progress",progress);
                        sendEvent(reactContext,"progressWillUpdate",params);
                    }
                });
            }
            fos.close();
            bis.close();
            is.close();
            return file;
        }
        else{
            return null;
        }
    }

    private static void sendEvent(ReactContext reactContext,
                           String eventName,
                           @Nullable WritableMap params) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }

    private static void installApk(File file, Activity activity) {
        Intent intent = new Intent();
        //执行动作
        intent.setAction(Intent.ACTION_VIEW);
        //执行的数据类型
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        activity.startActivity(intent);
    }
}
