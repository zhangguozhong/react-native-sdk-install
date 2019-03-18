
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
import RNSdkInstall from 'react-native-sdk-install';

// TODO: What to do with the module?
RNSdkInstall;
```
  