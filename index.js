import ProgressBarModal from './views/ProgressBarModal';
import { NativeModules,DeviceEventEmitter,Platform } from 'react-native';
const { RNSdkInstall } = NativeModules;

export default class InstallSDK {

    static downloadAndInstall(url,show,hide,update) {
        if (Platform.OS === 'android') {
            this.addListener('progressWillShow',show);
            this.addListener('progressWillHide',hide);
            this.addListener('progressWillUpdate',update);
            RNSdkInstall.downloadAndInstall(url);
        }
    }

    static addListener(eventName,callback) {
        DeviceEventEmitter.addListener(eventName, (event) => {
            excuteCallback(callback,event);
        });
    }
};

function excuteCallback(callback,params) {
    callback && callback(params);
}

export { ProgressBarModal };
