import { NativeModules,Platform } from 'react-native';
const { RNSdkInstall } = NativeModules;

export default class InstallSDK {

    static downloadAndInstall(url,forceUpdate,application_id) {
        if (Platform.OS === 'android') {
            RNSdkInstall.downloadAndInstall(url,forceUpdate,application_id);
        }
    }
};
