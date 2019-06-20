/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

import PWRecognize from 'react-native-peiwen-recognize';
import React, {Component} from 'react';
import {StyleSheet, Text, View,ScrollView, TouchableHighlight, DeviceEventEmitter} from 'react-native';



type Props = {};
export default class App extends Component<Props> {
  constructor(props){
    super(props);
    this.state = {
      recording:false,
      content:'Welcome'
    }
  }
  componentWillMount(): void {
    DeviceEventEmitter.addListener('PW_RECOGNIZE',this.recognizeEventReceived,this);
  }

  componentDidMount(): void {

  }

  componentWillUnmount(): void {
    DeviceEventEmitter.removeListener('PW_RECOGNIZE',this.recognizeEventReceived);
  }

  render() {
    return (
        <View style={styles.container}>
          <ScrollView ref="scrollView"
                      style={styles.welcome}
                      onContentSizeChange={() => this.refs.scrollView.scrollToEnd(true)}>
            <Text style={styles.welcome} numberOfLines={100}>{this.state.content}</Text>
          </ScrollView>
          <TouchableHighlight
              activeOpacity={1.0}
              style={styles.touch}
              underlayColor={'transparent'}
              onPress={() => this.recorderClicked()}>
            <Text style={styles.instructions}>{this.state.recording?'取消录音' : '开始录音'}</Text>
          </TouchableHighlight>
        </View>
    );
  }
  recorderClicked(){
    if(!this.state.recording){
      PWRecognize.start();
    }else{
      PWRecognize.cancel();
    }
  }

  recognizeEventReceived(msg){
    let event = JSON.parse(msg);
    if(event.EventType === "asr.ready"){
      this.setState({recording:true,content: '【' +  event.EventType +'】引擎就绪，可以开始说话'});
    }else if(event.EventType === "asr.begin"){
      this.setState({content:this.state.content +  '\n【' +  event.EventType +'】检测到用户说话'});
    }else if(event.EventType === "asr.end"){
      this.setState({content:this.state.content +  '\n【' +  event.EventType +'】检测到用户说话结束'});
    }else if(event.EventType === "asr.partial"){
      if(event.Final){
        this.setState({content:this.state.content +  '\n【' +  event.EventType +'】最终识别结果:' + event.Result});
      }else{
        this.setState({content:this.state.content +  '\n【' +  event.EventType +'】临时识别结果:' + event.Result});
      }
    }else if(event.EventType === "asr.finish"){
      if(event.ErrorCode === 0){
        this.setState({content:this.state.content +  '\n【' +  event.EventType +'】识别一段话结束'});
      }else{
        this.setState({content:this.state.content +  '\n【' +  event.EventType +'】识别错误, 错误码:' + event.ErrorCode + " ," + event.SubErrorCode});
      }
    }else if(event.EventType === "asr.volume"){
      this.setState({content:this.state.content +  '\n【' +  event.EventType +'】音量:' + event.Volume+',百分比:' + event.Percent});
    }else if(event.EventType === "asr.exit"){
      this.setState({recording:false,content:this.state.content +  '\n【' +  event.EventType +'】识别引擎结束并空闲中'});
    }
  }
}


const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    flex: 1,
    margin: 10,
    backgroundColor: 'red'
  },
  touch: {
    marginLeft: 10,
    marginRight: 10,
    marginBottom: 10,
    backgroundColor:'blue'
  },
  instructions: {
    fontSize: 20,
    color: '#ffffff',
    textAlign: 'center',
    marginVertical: 10
  },
});
