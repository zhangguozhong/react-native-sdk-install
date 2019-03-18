import React,{ PureComponent } from 'react';
import { View,Animated } from 'react-native';
import { getRealDP } from '../utils/utility';

export default class ProgressBar extends PureComponent {
    constructor(props) {
        super(props);
        this.progress = new Animated.Value(0);
    }

    componentWillReceiveProps(nextProps) {
        const { progress } = nextProps;
        if (this.props.progress >= 0 && this.props.progress !== progress) {
            this.updateProgress(progress);
        }
    }

    updateProgress = (progress) => {
        Animated.spring(this.progress,{ toValue:progress }).start();
    };

    render() {
        const { backgroundStyle,style,progressBgColor } = this.props;
        const { width } = style;
        return (
            <View style={[backgroundStyle,style]}>
                <Animated.View style={{
                    backgroundColor:!progressBgColor? '#2593fc':progressBgColor,
                    height:getRealDP(20),
                    borderRadius:getRealDP(20),
                    width:this.progress.interpolate({
                        inputRange:[0,100],
                        outputRange:[0,width],
                    })}}/>
            </View>
        );
    }
}