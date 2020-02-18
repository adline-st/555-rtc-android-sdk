package com.comcast.rtc555sdk;

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
    void onRtcSessionError(@NonNull ReadableMap errorInfo,String traceid) {
        HashMap errorInfoMap =  ConversionUtil.toMap(errorInfo);
        observer.onRtcNativeEventSessionError(errorInfoMap, traceid);
    }

    @ReactMethod
    void onNotification(@NonNull ReadableMap notification) {
       HashMap notificationData =  ConversionUtil.toMap(notification);
       observer.onRtcNativeEventNotification(notificationData);
    }

    @ReactMethod
    void onCallResponse(@NonNull String response){
        observer.onRtcNativeEventCallSuccess(response);
    }

    @ReactMethod
    void onCallFailed(@NonNull ReadableMap errorInfo){
        HashMap errorInfoMap =  ConversionUtil.toMap(errorInfo);
        observer.onRtcNativeEventCallFailed(errorInfoMap);
    }

    @ReactMethod
    void onRejectSuccess(@NonNull String callId){
        observer.onRtcNativeEventCallSuccess(callId);
    }
    @ReactMethod
    void onRejectFailed(@NonNull ReadableMap errorInfo){
        HashMap errorInfoMap =  ConversionUtil.toMap(errorInfo);
        observer.onRtcNativeEventCallFailed(errorInfoMap);
    }

    @ReactMethod
    void onCallMerged(@NonNull String callId){
        observer.onRtcNativeEventCallMergeSuccess(callId);
    }

    public interface RtcNativeEventObserver{
        void onRtcNativeEventConnectionStateChange(String connectionState);
        void onRtcNativeEventConnectionError(HashMap errorInfo);
        void onRtcNativeEventSessionStatus(String sessionStatus, String callId);
        void onRtcNativeEventSessionError(HashMap errorInfo, String callId);
        void onRtcNativeEventNotification(HashMap notfyData);
        void onRtcNativeEventCallSuccess(String response);
        void onRtcNativeEventCallFailed(HashMap errorInfo);
        void onRtcNativeEventCallMergeSuccess(String callId);
    }

}
