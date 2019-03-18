
package com.apk.install;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

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
}