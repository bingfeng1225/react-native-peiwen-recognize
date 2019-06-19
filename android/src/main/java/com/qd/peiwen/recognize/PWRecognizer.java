package com.qd.peiwen.recognize;

import android.content.Context;
import android.util.Log;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.qd.peiwen.recognize.recog.listener.IRecogListener;
import com.qd.peiwen.recognize.recog.listener.RecogEventAdapter;
import com.qd.peiwen.recognize.util.MyLogger;

import org.json.JSONObject;

import java.util.Map;

public class PWRecognizer {
    private EventManager manager;
    private EventListener listener;
    private volatile boolean isInited = false;
    private static PWRecognizer instance = null;
    private static final String TAG = "PWRecognizer";

    //只对需要锁的部分代码加锁
    public static PWRecognizer getInstance(){
        if(instance == null){
            //只需在第一次创建对象的时候加同步块，执行效率高
            synchronized (PWRecognizer.class){
                //双重判断，并发情况下保证只有一个实例
                if (instance == null) {
                    instance = new PWRecognizer();
                }
            }
        }
        return instance;
    }

    public void init(Context context){
        if(this.isInited) {
            throw new RuntimeException("还未调用release()，请勿新建一个新类");
        }
        this.isInited = true;
        this.listener = new PWEventListener();
        // SDK集成步骤 初始化asr的EventManager示例，多次得到的类，只能选一个使用
        this.manager = EventManagerFactory.create(context, "asr");
        // SDK集成步骤 设置回调event， 识别引擎会回调这个类告知重要状态和识别结果
        this.manager.registerListener(this.listener);
    }

    /**
     * @param params
     */
    public void start(Map<String, Object> params) {
        if (!isInited) {
            throw new RuntimeException("release() was called");
        }
        // SDK集成步骤 拼接识别参数
        String json = new JSONObject(params).toString();
        Log.i(TAG + ".Debug", "识别参数（反馈请带上此行日志）" + json);
        this.manager.send(SpeechConstant.ASR_START, json, null, 0, 0);
    }


    /**
     * 提前结束录音等待识别结果。
     */
    public void stop() {
        Log.i(TAG, "停止录音");
        // SDK 集成步骤（可选）停止录音
        if (!isInited) {
            throw new RuntimeException("release() was called");
        }
        this.manager.send(SpeechConstant.ASR_STOP, "{}", null, 0, 0);
    }

    /**
     * 取消本次识别，取消后将立即停止不会返回识别结果。
     * cancel 与stop的区别是 cancel在stop的基础上，完全停止整个识别流程，
     */
    public void cancel() {
        Log.i(TAG, "取消识别");
        if (!isInited) {
            throw new RuntimeException("release() was called");
        }
        // SDK集成步骤 (可选） 取消本次识别
        this.manager.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);
    }

    public void release() {
        if (this.manager == null) {
            return;
        }
        this.cancel();
        // SDK 集成步骤（可选），卸载listener
        this.manager.unregisterListener(this.listener);
        this.manager = null;
        this.listener = null;
        this.isInited = false;
    }
}
