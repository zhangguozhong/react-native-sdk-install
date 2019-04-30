import { NativeModules,Platform } from 'react-native';
const { RNSdkInstall } = NativeModules;

const InstallUtil = {

    downloadAndInstall:function (url, forceUpdate) {
        if (Platform.OS === 'android') {
            RNSdkInstall.downloadAndInstall(url,forceUpdate);
        }
    }
}

export default InstallUtil;
