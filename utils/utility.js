/**
 * 按比例将设计的px转换成适应不同屏幕的dp
 * @param designPx 设计稿标注的px值
 * @returns {*|number}
 */
import { PixelRatio,Dimensions } from 'react-native';
const { width } = Dimensions.get('window');
export function getRealDP(designPx) {
    return PixelRatio.roundToNearestPixel((designPx / 750) * width);
}