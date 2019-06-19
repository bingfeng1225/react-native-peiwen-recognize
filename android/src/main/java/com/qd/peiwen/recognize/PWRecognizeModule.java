package com.qd.peiwen.recognize;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.util.HashMap;
import java.util.Map;

public class PWRecognizeModule extends ReactContextBaseJavaModule {
    public PWRecognizeModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "PWRecognize";
    }

    @ReactMethod
    public void start(){
        Map<String, Object> params = new HashMap();
        params.put("pid",1536);
        PWRecognizer.getInstance().start(params);
    }

    @ReactMethod
    public void stop(){
        PWRecognizer.getInstance().stop();
    }

    @ReactMethod
    public void cancel(){
        PWRecognizer.getInstance().cancel();
    }
}
