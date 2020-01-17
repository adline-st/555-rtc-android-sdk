package com.rtcreactsdk;

import android.app.Application;
import android.os.Handler;

import com.facebook.react.ReactInstanceManager;
import com.facebook.react.bridge.CatalystInstance;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.common.LifecycleState;
import com.facebook.react.shell.MainReactPackage;
import com.facebook.soloader.SoLoader;
import com.learnium.RNDeviceInfo.RNDeviceInfo;
import com.oblador.vectoricons.VectorIconsPackage;
import com.oney.WebRTCModule.WebRTCModulePackage;
import com.psykar.cookiemanager.CookieManagerPackage;
import com.reactnativecommunity.asyncstorage.AsyncStoragePackage;
import com.reactnativecommunity.netinfo.NetInfoPackage;
import com.reactnativecommunity.webview.RNCWebViewPackage;
import com.zxcpoiu.incallmanager.InCallManagerPackage;

import java.util.HashMap;
import java.util.UUID;


public class RtcReactSdk implements ReactInstanceManager.ReactInstanceEventListener ,RtcNativeBridge.RtcNativeEventObserver{
    RtcEventObserver rtcInterface;
    ReactInstanceManager mReactInstanceManager;


    private void init(Application appContext,RtcEventObserver observer){

        SoLoader.init(appContext, /* native exopackage */ false);
        mReactInstanceManager = ReactInstanceManager.builder()
                .setApplication(appContext)
                .setUseDeveloperSupport(false)
                .setBundleAssetName("index.android.bundle")
                .setJSBundleFile("assets://index.android.bundle")
                .addPackage(new VectorIconsPackage())
                .addPackage(new RNCWebViewPackage())
                .addPackage(new CookieManagerPackage())
                .addPackage(new NetInfoPackage())
                .addPackage(new AsyncStoragePackage())
                .addPackage(new RNDeviceInfo())
                .addPackage(new InCallManagerPackage())
                .addPackage(new MainReactPackage())
                .addPackage(new WebRTCModulePackage())
                .addPackage(new RtcNativeBridgePackage(this))
                .setInitialLifecycleState(LifecycleState.BEFORE_RESUME)
                .build();
        mReactInstanceManager.addReactInstanceEventListener(this);
        mReactInstanceManager.createReactContextInBackground();
        rtcInterface = observer;
    };

    public RtcReactSdk(Application appContext,RtcEventObserver observer){
        this.init(appContext,observer);

    }

    @Override
    public void onReactContextInitialized(ReactContext context) {
        System.out.println("onReactContextInitialized");

    }

    public void connect(String evmUrl, String irisToken, String routingId){

        WritableNativeArray params = new WritableNativeArray();
        params.pushString(evmUrl);
        params.pushString(irisToken);
        params.pushString(routingId);
        this.callReactMethod("connect",params);

    }

    public String dial(String sourceNumber, String targetNumber, String notificationPayload, HashMap config){
        String callId = null;

        if(config.get("callId") != null){
            callId = config.get("callId").toString();
        }else{
             callId = UUID.randomUUID().toString();
             config.put("callId",callId);
        }

        WritableMap configuration = ConversionUtil.toWritableMap(config);

        WritableNativeArray params = new WritableNativeArray();
        params.pushString(sourceNumber);
        params.pushString(targetNumber);
        params.pushString(notificationPayload);
        params.pushMap(configuration);

        this.callReactMethod("dial",params);

        return callId;
    }

    //accept
    public void accept(HashMap notificationData, HashMap config){

        WritableMap notificationmap = ConversionUtil.toWritableMap(notificationData);
        WritableMap configmap = ConversionUtil.toWritableMap(config);

        WritableNativeArray params = new WritableNativeArray();

        params.pushMap(notificationmap);
        params.pushMap(configmap);

        this.callReactMethod("accept",params);
    }

    //hangUp
    public void hangup(String callId){

        WritableNativeArray params = new WritableNativeArray();
        params.pushString(callId);
        this.callReactMethod("hangup",params);

    }

    //hold
    public void hold(String callId){
        WritableNativeArray params = new WritableNativeArray();
        params.pushString(callId);
        this.callReactMethod("hold",params);
    }

    //unhold
    public void unHold(String callId){
        WritableNativeArray params = new WritableNativeArray();
        params.pushString(callId);
        this.callReactMethod("unhold",params);
    }

    //mute
    public void mute(String callId){
        WritableNativeArray params = new WritableNativeArray();
        params.pushString(callId);
        this.callReactMethod("mute",params);
    }

    //unMute
    public void unMute(String callId){
        WritableNativeArray params = new WritableNativeArray();
        params.pushString(callId);
        this.callReactMethod("unmute",params);
    }

    public void callReactMethod(String apiname,WritableNativeArray args){

        if(mReactInstanceManager.getCurrentReactContext() != null){
            ReactContext reactContext = mReactInstanceManager.getCurrentReactContext();
            CatalystInstance catalystInstance = reactContext.getCatalystInstance();
            catalystInstance.callFunction("RtcSdk", apiname, args);
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ReactContext reactContext = mReactInstanceManager.getCurrentReactContext();
                    CatalystInstance catalystInstance = reactContext.getCatalystInstance();
                    catalystInstance.callFunction("RtcSdk", apiname, args);
                }
            }, 500);
        }

    }

    @Override
    public void onRtcNativeEventConnectionStateChange(String connectionState) {
        rtcInterface.onRtcConnectionStateChange(connectionState);
    }

    @Override
    public void onRtcNativeEventConnectionError(HashMap errorInfo) {
        rtcInterface.onRtcConnectionError(errorInfo);
    }

    @Override
    public void onRtcNativeEventSessionStatus(String sessionStatus, String callId) {
        rtcInterface.onSessionStatus(sessionStatus,callId);
    }

    @Override
    public void onRtcNativeEventSessionError(HashMap errorInfo) {
        rtcInterface.onSessionError(errorInfo);
    }

    @Override
    public void onRtcNativeEventNotification(HashMap notfyData) {
       rtcInterface.onNotification(notfyData);
    }


    public interface RtcEventObserver{
        void onRtcConnectionStateChange(String connectionState);
        void onRtcConnectionError(HashMap errorInfo);
        void onSessionStatus(String sessionStatus, String callId);
        void onSessionError(HashMap errorInfo);
        void onNotification(HashMap notfyData);
    }

}
