package com.comcast.rtc555sdk;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.UUID;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;


public class Rtc555Sdk implements ReactInstanceManager.ReactInstanceEventListener ,RtcNativeBridge.RtcNativeEventObserver{
    Rtc555SdkObserver rtcInterface;
    ReactInstanceManager mReactInstanceManager;
    SingleEmitter<String> callEventEmitter;
    Single<String> dialCallObservable;
    Single<String> acceptCallObservable;
    Single<String> rejectCallObservable;

    // Call states
    public enum CallStatus {
        initializing,
        connecting,
        connected,
        disconnected,
        hold
    }

    /**
     * Input for DTMF tone during audio call.
     */
    public enum DtmfInputType{
        kNUMBER1("1"),
        kNUMBER2("2"),
        kNUMBER3("3"),
        kNUMBER4("4"),
        kNUMBER5("5"),
        kNUMBER6("6"),
        kNUMBER7("7"),
        kNUMBER8("8"),
        kNUMBER9("9"),
        kNUMBER0("0"),
        kSTAR("*"),
        kHASH("#"),
        kLETTERA("A"),
        kLETTERB("B"),
        kLETTERC("C"),
        kLETTERD("D");

        private final String toneType;

        DtmfInputType(String toneType){
            this.toneType = toneType;
        }

        public String getToneType(){
            return toneType;
        }

    }




    public Rtc555Sdk(Application appContext, Rtc555SdkObserver observer){
        this.init(appContext,observer);

    }


    public void setConfig(Rtc555Config config){
         HashMap configMap = new HashMap();
        try {
            configMap.put("sourceTelephonenum",config.sourceTelephoneNumber);
            configMap.put("token",config.token);
            configMap.put("routingId",config.routingId);
            configMap.put("eventManagerUrl",config.eventManagerUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        WritableMap configWrittableMap = ConversionUtil.toWritableMap(configMap);
        WritableNativeArray params = new WritableNativeArray();

        params.pushMap(configWrittableMap);
        RtcReactBridge.getInstance().callReactMethod("setConfig",params);
    }


    private void init(Application appContext,Rtc555SdkObserver observer){

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
        this.setRtcReactBridgeParam();
    };

    private void setRtcReactBridgeParam(){
        RtcReactBridge.getInstance().setReactInstanceManager(mReactInstanceManager);
        RtcReactBridge.getInstance().setRtcEventObserver(rtcInterface);
    }

    @Override
    public void onReactContextInitialized(ReactContext context) {
        System.out.println("onReactContextInitialized");

    }

    private void connect(String evmUrl, String irisToken, String routingId){
        WritableNativeArray params = new WritableNativeArray();
        params.pushString(evmUrl);
        params.pushString(irisToken);
        params.pushString(routingId);
        RtcReactBridge.getInstance().callReactMethod("connect",params);

    }

    public Single<String> dial(String targetTelephoneNum, HashMap notificationPayload,Rtc555Result observer){
        WritableMap notificationConfig = ConversionUtil.toWritableMap(notificationPayload);
        WritableNativeArray params = new WritableNativeArray();
        params.pushString(targetTelephoneNum);
        params.pushString(notificationConfig.toString());

        dialCallObservable = Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(SingleEmitter<String> e) throws Exception {
                callEventEmitter = e;
                RtcReactBridge.getInstance().callReactMethod("dial",params);
            }
        });

        dialCallObservable.subscribeWith(observer);


        return dialCallObservable;
    }


    public Single<String> accept(HashMap notificationData,Rtc555Result observer){
        WritableMap notificationmap = ConversionUtil.toWritableMap(notificationData);
        WritableNativeArray params = new WritableNativeArray();
        params.pushMap(notificationmap);

        acceptCallObservable = Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(SingleEmitter<String> e) throws Exception {
                callEventEmitter = e;
                RtcReactBridge.getInstance().callReactMethod("accept",params);
            }
        });

        acceptCallObservable.subscribeWith(observer);


        return acceptCallObservable;
    }

    //hangUp
    public void hangup(String callId){

        WritableNativeArray params = new WritableNativeArray();
        params.pushString(callId);
        RtcReactBridge.getInstance().callReactMethod("hangup",params);

    }

    //hold
    public void hold(String callId){
        WritableNativeArray params = new WritableNativeArray();
        params.pushString(callId);
        RtcReactBridge.getInstance().callReactMethod("hold",params);
    }

    //unhold
    public void unHold(String callId){
        WritableNativeArray params = new WritableNativeArray();
        params.pushString(callId);
        RtcReactBridge.getInstance().callReactMethod("unhold",params);
    }

    //mute
    public void mute(String callId){
        WritableNativeArray params = new WritableNativeArray();
        params.pushString(callId);
        RtcReactBridge.getInstance().callReactMethod("mute",params);
    }

    //unMute
    public void unMute(String callId){
        WritableNativeArray params = new WritableNativeArray();
        params.pushString(callId);
        RtcReactBridge.getInstance().callReactMethod("unmute",params);
    }

    //merge
    public void merge(String callId){
        WritableNativeArray params = new WritableNativeArray();
        params.pushString(callId);
        RtcReactBridge.getInstance().callReactMethod("merge",params);
    }

    //send DTMF
    public void sendDTMF(DtmfInputType tone){
        WritableNativeArray params = new WritableNativeArray();
        params.pushString(tone.getToneType());
        RtcReactBridge.getInstance().callReactMethod("sendDTMF",params);
    }

    public void cleanup(){
        RtcReactBridge.getInstance().callReactMethod("cleanup",null);
    }


    //reject call

    public Single<String> reject(HashMap notificationData,Rtc555Result observer){
        WritableMap notificationmap = ConversionUtil.toWritableMap(notificationData);
        WritableNativeArray params = new WritableNativeArray();
        params.pushMap(notificationmap);

        rejectCallObservable = Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(SingleEmitter<String> e) throws Exception {
                callEventEmitter = e;
                RtcReactBridge.getInstance().callReactMethod("reject",params);
            }
        });

        rejectCallObservable.subscribeWith(observer);


        return rejectCallObservable;
    }



    @Override
    public void onRtcNativeEventConnectionStateChange(String connectionState) {
    }

    @Override
    public void onRtcNativeEventConnectionError(HashMap errorInfo) {
    }

    @Override
    public void onRtcNativeEventSessionStatus(String sessionStatus, String callId) {
        rtcInterface.onCallStatus(getCallStatus(sessionStatus),callId);
    }

    @Override
    public void onRtcNativeEventSessionError(HashMap sessionErrorInfo, String callId) {
        String errorReason = (String) sessionErrorInfo.get("reason");
        String errorCode = (String) sessionErrorInfo.get("code");
        rtcInterface.onCallError(new Rtc555Exception(errorReason,errorCode),callId);
    }

    @Override
    public void onRtcNativeEventNotification(HashMap notfyData) {
       rtcInterface.onNotification(notfyData);
    }

    @Override
    public void onRtcNativeEventCallSuccess(String response) {
        callEventEmitter.onSuccess(response);
    }

    @Override
    public void onRtcNativeEventCallFailed(HashMap errorInfo) {
        String reason = (String) errorInfo.get("reason");
        String code = (String) errorInfo.get("code");
        callEventEmitter.onError(new Rtc555Exception(reason,code));

    }

    @Override
    public void onRtcNativeEventCallMergeSuccess(String callId) {
        rtcInterface.onCallMerged(callId);
    }

    private CallStatus getCallStatus(String status){
        CallStatus callStatus = CallStatus.initializing;
        switch (status){
            case "initializing":
                callStatus = CallStatus.initializing;
                break;
            case "connecting" :
                callStatus = CallStatus.connecting;
                break;
            case "connected" :
                callStatus = CallStatus.connected;
                 break;
            case "disconnected" :
                callStatus = CallStatus.disconnected;
                break;
            case "hold":
                callStatus = CallStatus.hold;
                break;
        }
        return callStatus;

    }
    public interface Rtc555SdkObserver{
        void onCallStatus(CallStatus callStatus, String callId);
        void onCallError(Rtc555Exception errorInfo,String callId);
        void onNotification(HashMap notfyData);
        void onCallMerged(String callId);
    }

}
