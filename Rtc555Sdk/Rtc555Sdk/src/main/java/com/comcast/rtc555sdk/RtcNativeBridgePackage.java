package com.comcast.rtc555sdk;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.comcast.rtc555sdk.RtcNativeBridge.RtcNativeEventObserver;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

public class RtcNativeBridgePackage implements ReactPackage {

    private RtcNativeEventObserver observer;

    public RtcNativeBridgePackage(RtcNativeEventObserver observer){
        this.observer = observer;
    }

    @Nonnull
    @Override
    public List<NativeModule> createNativeModules(@Nonnull ReactApplicationContext reactContext) {
        List<NativeModule> modules = new ArrayList<>();
        modules.add(new RtcNativeBridge(reactContext,observer));
        return modules;
    }

    @Nonnull
    @Override
    public List<ViewManager> createViewManagers(@Nonnull ReactApplicationContext reactContext) {
        return Collections.emptyList();
    }
}
