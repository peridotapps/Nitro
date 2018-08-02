package com.peridotapps.nitro;

import android.app.Application;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.annotation.CallSuper;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;

import com.peridotapps.nitro.hardware.Network;
import com.peridotapps.nitro.logging.Logger;
import com.peridotapps.nitro.ui.core.INitroUiConcurrent;

import java.lang.annotation.Documented;
import java.lang.annotation.Target;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;

public abstract class NitroApplication extends Application implements INitroUiConcurrent, Network.NetworkStatusObserver, LifecycleObserver {
  
  private static final String APP_STATUS_STRING_UNKNOWN = "unknown";
  private static final String APP_STATUS_STRING_BACKGROUND = "background";
  private static final String APP_STATUS_STRING_FOREGROUND = "foreground";
  
  private static final AtomicReference<NitroApplication> instance = new AtomicReference<>();
  private static final AtomicInteger applicationState = new AtomicInteger(ApplicationState.UNKNOWN);
  private static final AtomicBoolean hasNetworkConnection = new AtomicBoolean(true);
  
  public static NitroApplication getSharedInstance () {
    synchronized (instance) {
      return instance.get();
    }
  }
  
  private static void setSharedInstance (@NonNull NitroApplication app) {
    synchronized (instance) {
      instance.set(app);
    }
  }
  
  @ApplicationState
  public int getApplicationState () {
    return applicationState.get();
  }
  
  private void setApplicationState (@ApplicationState int status) {
    applicationState.set(status);
  }
  
  public String getApplicationStatusString () {
    switch (getApplicationState()) {
      case ApplicationState.BACKGROUND:
        return APP_STATUS_STRING_BACKGROUND;
      case ApplicationState.FOREGROUND:
        return APP_STATUS_STRING_FOREGROUND;
    }
    
    return APP_STATUS_STRING_UNKNOWN;
  }
  
  @CallSuper
  @Override
  public void onCreate () {
    super.onCreate();
    
    NitroApplication.setSharedInstance(this);
    
    ProcessLifecycleOwner.get()
                         .getLifecycle()
                         .addObserver(this);
    
    Network.getNetworkMonitor()
           .addObserver(this);
    
    registerBroadcastReceivers();
  }
  
  @CallSuper
  public void onEnterForeground () {
    //stub
  }
  
  @CallSuper
  public void onEnterBackground () {
    //stub
  }
  
  @CallSuper
  public void registerBroadcastReceivers () {
    registerNetworkReceiver();
  }
  
  @CallSuper
  public void unregisterBroadcastReceivers () {
    unregisterNetworkReceiver();
  }
  
  public void onDeviceBootCompleted () {
    // Stub
  }
  
  protected boolean isMultiDexEnabled () {
    return true;
  }
  
  private void setupMultiDex () {
    if (isMultiDexEnabled()) {
      MultiDex.install(this);
    }
  }
  
  @CallSuper
  protected void attachBaseContext (Context base) {
    super.attachBaseContext(base);
    setupMultiDex();
  }
  
  private void registerNetworkReceiver () {
    IntentFilter filter = new IntentFilter();
    filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
    getApplicationContext().registerReceiver(Network.getNetworkReceiver(), filter);
  }
  
  private void unregisterNetworkReceiver () {
    try {
      getApplicationContext().unregisterReceiver(Network.getNetworkReceiver());
    } catch (Exception e) {
      Logger.E(this, e);
    }
  }
  
  @OnLifecycleEvent(Lifecycle.Event.ON_START)
  final void enterForeground () {
    setApplicationState(ApplicationState.FOREGROUND);
    onEnterForeground();
  }
  
  @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
  final void enterBackground () {
    setApplicationState(ApplicationState.BACKGROUND);
    onEnterBackground();
  }
  
  @CallSuper
  public void onNetworkConnected (@NonNull String networkType) {
    // stub
  }
  
  @CallSuper
  public void onNetworkDisconnected () {
    // stub
  }
  
  @Documented
  @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE})
  @IntDef({ApplicationState.UNKNOWN, ApplicationState.BACKGROUND, ApplicationState.FOREGROUND})
  private @interface ApplicationState {
    int UNKNOWN = -1;
    int BACKGROUND = 1;
    int FOREGROUND = 2;
  }
}
