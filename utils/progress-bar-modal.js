import { StyleSheet } from 'react-native';
import { getRealDP } from './utility';

// 进度条modal样式
const styles = StyleSheet.create({
    imageBg:{
        width:getRealDP(780),
        height:getRealDP(224),
        justifyContent:'center',
        alignItems:'center'
    },
    progressBarView:{
        flex:1,
        justifyContent:'center',
        alignItems:'center',
        backgroundColor:'rgba(0,0,0,0.2)'
    },
    // 默认进度条背景底色
    barBackgroundStyle:{
        backgroundColor:'#e0e0e0'
    },
    subView:{
        width:getRealDP(700),
        height:getRealDP(296),
        backgroundColor:'#fff',
        alignItems:'center',
        borderRadius:getRealDP(16),
        justifyContent:'center'
    },
    textPackageSize:{
        fontSize:getRealDP(28),
        color:'#686868',
        marginTop:getRealDP(32)
    },
    title:{
        marginHorizontal:getRealDP(32),
        color:'black',
        alignSelf:'flex-start',
        marginBottom:getRealDP(32),
        fontSize:getRealDP(32)
    }
});

export default styles;