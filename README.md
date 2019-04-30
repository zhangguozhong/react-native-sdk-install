
# react-native-sdk-install

## Getting started

`$ npm install react-native-sdk-install --save`

### Mostly automatic installation

`$ react-native link react-native-sdk-install`

### Manual installation


#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.apk.install.RNSdkInstallPackage;` to the imports at the top of the file
  - Add `new RNSdkInstallPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-sdk-install'
  	project(':react-native-sdk-install').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-sdk-install/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-sdk-install')
  	```


## Usage
```javascript
import InstallUtil from 'react-native-sdk-install';

doCheckUpdate = () => {
    InstallUtil.downloadAndInstall('https://domain/app-release.apk',forceUpdate);
};
```

## 注意的地方

1、android 8.0以上系统需要在清单文件AndroidManifest.xml中配置权限以及provider：
```javascript

<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />

<provider
    android:name="android.support.v4.content.FileProvider"
    android:authorities="${applicationId}.fileprovider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_paths" />
</provider>

```



  
