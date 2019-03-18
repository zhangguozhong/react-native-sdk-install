import React,{ PureComponent } from 'react';
import { View,Modal,Text } from 'react-native';
import ProgressBar from './ProgressBar';
import { getRealDP } from '../utils/utility';
import styles from '../utils/progress-bar-modal';

const propTypes = { ...Modal.propTypes };
const defaultProps = {
    animationType:'none',
    transparent:true,
    progressModalVisible:false,
    onRequestClose:() => {},
};

/* 更新进度条Modal */
export default class ProgressBarModal extends PureComponent {

    render() {
        const { animationType,transparent,onRequestClose,progress,progressModalVisible,totalPackageSize,receivedPackageSize,title,progressBgColor } = this.props;
        return (
            <Modal
                animationType={animationType}
                transparent={transparent}
                visible={progressModalVisible}
                onRequestClose={onRequestClose}
            >
                <View style={styles.progressBarView}>
                    <View style={styles.subView}>
                        <Text style={styles.title}>{ title }</Text>
                        <ProgressBar style={{ width:getRealDP(600),borderRadius:getRealDP(20) }}
                            progress={progress}
                            backgroundStyle={styles.barBackgroundStyle} progressBgColor={progressBgColor}
                        />
                        <Text style={styles.textPackageSize}>{`${ receivedPackageSize } / ${ totalPackageSize }`}</Text>
                    </View>
                </View>
            </Modal>
        );
    }
}

ProgressBarModal.propTypes = propTypes;
ProgressBarModal.defaultProps = defaultProps;
