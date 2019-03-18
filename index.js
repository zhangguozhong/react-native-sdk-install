import ProgressBarModal from './views/ProgressBarModal';
import { NativeModules,DeviceEventEmitter,Platform } from 'react-native';
const { RNSdkInstall } = NativeModules;
const listenerEvents = {}; //监听事件

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
       const event = DeviceEventEmitter.addListener(eventName, (event) => {
            excuteCallback(callback,event);
        });

        listenerEvents[eventName] = event;
    }

    static removeListeners() {//移除监听事件
        for (let eventName of Object.keys(listenerEvents)) {
            const eventItem = listenerEvents[eventName];
            eventItem && eventItem.remove();
        }
    }
};

function excuteCallback(callback,params) {
    callback && callback(params);
}

export { ProgressBarModal };
