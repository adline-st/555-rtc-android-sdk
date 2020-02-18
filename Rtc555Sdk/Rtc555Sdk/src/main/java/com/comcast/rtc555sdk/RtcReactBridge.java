package com.comcast.rtc555sdk;

import android.os.Handler;

import com.facebook.react.ReactInstanceManager;
import com.facebook.react.bridge.CatalystInstance;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableNativeArray;
import com.comcast.rtc555sdk.Rtc555Sdk.Rtc555SdkObserver;

public class RtcReactBridge {
    private static RtcReactBridge rtcReactBridge;
    private Rtc555SdkObserver observer;
    private ReactInstanceManager mReactInstanceManager;

    private RtcReactBridge(){

    }

    void setRtcEventObserver(Rtc555SdkObserver observer){
        this.observer = observer;
    }

    void setReactInstanceManager(ReactInstanceManager mReactInstanceManager){
        this.mReactInstanceManager = mReactInstanceManager;
    }

    public static RtcReactBridge getInstance() {
        if (rtcReactBridge == null)
            rtcReactBridge = new RtcReactBridge();

        return rtcReactBridge;
    }

    public void callReactMethod(String apiname, WritableNativeArray args){

        if(mReactInstanceManager.getCurrentReactContext() != null){
            ReactContext reactContext = mReactInstanceManager.getCurrentReactContext();
            CatalystInstance catalystInstance = reactContext.getCatalystInstance();
            catalystInstance.callFunction("RtcSdk", apiname, args);
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ReactContext reactContext = mReactInstanceManager.getCurrentReactContext();
                    if(reactContext != null) {
                        CatalystInstance catalystInstance = reactContext.getCatalystInstance();
                        catalystInstance.callFunction("RtcSdk", apiname, args);
                    }
                }
            }, 500);
        }

    }


}
