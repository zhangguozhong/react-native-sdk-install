
package com.apk.install;

import android.Manifest;
import android.widget.Toast;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class RNSdkInstallModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;

  public RNSdkInstallModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNSdkInstall";
  }

  @ReactMethod
  public void downloadAndInstall(final String url) {
    PermissionUtils.permissionsCheck(getCurrentActivity(),new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE },new PermissionUtils.OnPermissionListener() {
      @Override
      public void onPermissionGranted() {
        DownloadAndInstall.start(url, getCurrentActivity(),getReactApplicationContext());
      }
      @Override
      public void onPermissionDenied(String[] deniedPermissions) {
        Toast.makeText(getCurrentActivity(),"读写权限不够,无法下载！",Toast.LENGTH_SHORT).show();
      }
    });
  }
}