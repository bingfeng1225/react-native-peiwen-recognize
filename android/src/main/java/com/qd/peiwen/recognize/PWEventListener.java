package com.qd.peiwen.recognize;

import android.content.Context;
import android.util.Log;

import com.baidu.speech.EventListener;
import com.baidu.speech.asr.SpeechConstant;
import com.facebook.react.ReactApplication;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.qd.peiwen.recognize.entity.ResultEntity;
import com.qd.peiwen.recognize.entity.VolumeEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fujiayi on 2017/6/14.
 */

public class PWEventListener implements EventListener {
    private Context context;
    private static final String TAG = "RecogEventAdapter";

    public PWEventListener(Context context) {
        this.context = context;
    }
    // 基于DEMO集成3.1 开始回调事件
    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {
        String logMessage = "name:" + name + "; params:" + params;
        // logcat 中 搜索RecogEventAdapter，即可以看见下面一行的日志
        Log.i(TAG, logMessage);

         if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_READY)) {
             // 引擎准备就绪，可以开始说话
             Log.i(TAG,"【"+name+"】引擎就绪，可以开始说话");
             Map<String,Object> map = new HashMap<>();
             map.put("EventType",name);
             this.sendReactNativeEvent(map);
         } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_BEGIN)) {
             // 检测到用户的已经开始说话
             Log.i(TAG,"【"+name+"】检测到用户说话");
             Map<String,Object> map = new HashMap<>();
             map.put("EventType",name);
             this.sendReactNativeEvent(map);
         } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_END)) {
            // 检测到用户的已经停止说话
             Log.i(TAG,"【"+name+"】检测到用户说话结束");
             Map<String,Object> map = new HashMap<>();
             map.put("EventType",name);
             this.sendReactNativeEvent(map);
         } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)) {
             ResultEntity recogResult = this.parseResultJson(params);
            String[] results = recogResult.getResultsRecognition();
            if (recogResult.isFinalResult()) {
                // 最终识别结果，长语音每一句话会回调一次
                Log.i(TAG,"【"+name+"】最终识别结果:" + results[0]);
                Map<String,Object> map = new HashMap<>();
                map.put("EventType",name);
                map.put("Final",true);
                map.put("Result",results[0]);
                this.sendReactNativeEvent(map);
            } else if (recogResult.isPartialResult()) {
                // 临时识别结果
                Log.i(TAG,"【"+name+"】临时识别结果:" + results[0]);
                Map<String,Object> map = new HashMap<>();
                map.put("EventType",name);
                map.put("Final",false);
                map.put("Result",results[0]);
                this.sendReactNativeEvent(map);
            }
        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_FINISH)) {
            // 识别结束
            ResultEntity recogResult = this.parseResultJson(params);
            if (recogResult.hasError()) {
                int errorCode = recogResult.getError();
                String descMessage = recogResult.getDesc();
                int subErrorCode = recogResult.getSubError();
                Log.i(TAG,"【"+name+"】识别错误, 错误码:" + errorCode + " ," + subErrorCode + " ; " + descMessage);
            } else {
                Log.i(TAG,"【"+name+"】识别一段话结束");
            }
             Map<String,Object> map = new HashMap<>();
             map.put("EventType",name);
             map.put("ErrorCode",recogResult.getError());
             map.put("SubErrorCode",recogResult.getSubError());
             this.sendReactNativeEvent(map);
        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_EXIT)) {
             //引擎空闲
             Log.i(TAG,"【"+name+"】识别引擎结束并空闲中");
             Map<String,Object> map = new HashMap<>();
             map.put("EventType",name);
             this.sendReactNativeEvent(map);
         } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_VOLUME)) {
            VolumeEntity volume = this.parseVolumeJson(params);
             Log.i(TAG,"【"+name+"】音量:" + volume.getVolume()+",百分比:" + volume.getPercent());
             Map<String,Object> map = new HashMap<>();
             map.put("EventType",name);
             map.put("Volume",volume.getVolume());
             map.put("Percent",volume.getPercent());
             this.sendReactNativeEvent(map);
         }
    }


    private void sendReactNativeEvent(Map<String,Object> map){
        try {
            JSONObject object = new JSONObject();
            for (String key:map.keySet()) {
                object.put(key,map.get(key));
            }
            this.deviceEventEmitter().emit("PW_RECOGNIZE",object.toString());
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private DeviceEventManagerModule.RCTDeviceEventEmitter deviceEventEmitter(){
        ReactApplication application = (ReactApplication)this.context.getApplicationContext();
        ReactContext context = application.getReactNativeHost().getReactInstanceManager().getCurrentReactContext();
        return context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class);
    }

    private VolumeEntity parseVolumeJson(String jsonStr) {
        VolumeEntity volume = new VolumeEntity();
        try {
            JSONObject json = new JSONObject(jsonStr);
            volume.setVolume(json.getInt("volume"));
            volume.setPercent(json.getInt("volume-percent"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return volume;
    }

    private ResultEntity parseResultJson(String jsonStr){
        ResultEntity result = new ResultEntity();
        try {
            JSONObject json = new JSONObject(jsonStr);
            int error = json.optInt("error");
            int subError = json.optInt("sub_error");
            result.setError(error);
            result.setSubError(subError);
            result.setDesc(json.optString("desc"));
            result.setResultType(json.optString("result_type"));
            if (!result.hasError()) {
                result.setOrigalResult(json.getString("origin_result"));
                JSONArray arr = json.optJSONArray("results_recognition");
                if (arr != null) {
                    int size = arr.length();
                    String[] recogs = new String[size];
                    for (int i = 0; i < size; i++) {
                        recogs[i] = arr.getString(i);
                    }
                    result.setResultsRecognition(recogs);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
}
