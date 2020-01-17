package com.rtcreactsdk;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;

import java.util.HashMap;

import javax.annotation.Nonnull;

public class RtcNativeBridge extends ReactContextBaseJavaModule{

    private RtcNativeEventObserver observer;

    RtcNativeBridge(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    RtcNativeBridge(ReactApplicationContext reactContext,RtcNativeEventObserver obj) {
        super(reactContext);
        this.observer = obj;
    }

    @Nonnull
    @Override
    public String getName() {
        return "RtcNativeBridge";
    }

    @ReactMethod
    void onRtcConnectionStateChange(@NonNull String state) {
        observer.onRtcNativeEventConnectionStateChange(state);
    }

    @ReactMethod
    void onRtcConnectionError(@NonNull ReadableMap errorInfo) {
        HashMap errorInfoMap =  ConversionUtil.toMap(errorInfo);
        observer.onRtcNativeEventConnectionError(errorInfoMap);
    }

    @ReactMethod
    void onRtcSessionStatus(@NonNull String status,String traceid) {
        observer.onRtcNativeEventSessionStatus(status,traceid);
    }

    @ReactMethod
    void onRtcSessionError(@NonNull ReadableMap errorInfo) {
        HashMap errorInfoMap =  ConversionUtil.toMap(errorInfo);
        observer.onRtcNativeEventSessionError(errorInfoMap);
    }

    @ReactMethod
    void onNotification(@NonNull ReadableMap notification) {
       HashMap notificationData =  ConversionUtil.toMap(notification);
       observer.onRtcNativeEventNotification(notificationData);
    }


    public interface RtcNativeEventObserver{
        void onRtcNativeEventConnectionStateChange(String connectionState);
        void onRtcNativeEventConnectionError(HashMap errorInfo);
        void onRtcNativeEventSessionStatus(String sessionStatus, String callId);
        void onRtcNativeEventSessionError(HashMap errorInfo);
        void onRtcNativeEventNotification(HashMap notfyData);
    }

}
