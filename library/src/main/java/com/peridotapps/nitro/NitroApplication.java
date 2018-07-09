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
import android.support.multidex.MultiDex;
import com.peridotapps.nitro.hardware.Network;
import com.peridotapps.nitro.logging.Logger;
import com.peridotapps.nitro.ui.core.INitroUiConcurrent;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public abstract class NitroApplication extends Application implements INitroUiConcurrent, Network.NetworkStatusObserver {
  
  private static final String APP_STATUS_STRING_UNKNOWN = "unknown";
  private static final String APP_STATUS_STRING_BACKGROUND = "background";
  private static final String APP_STATUS_STRING_FOREGROUND = "foreground";
  
  private static final AtomicReference<NitroApplication> instance = new AtomicReference<>();
  private static final AtomicInteger applicationStatus = new AtomicInteger(ApplicationStatus.UNKNOWN);
  
  private final ForegroundMonitor foregroundMonitor = new ForegroundMonitor();
  
  public static NitroApplication getSharedInstance() {
    synchronized (instance) {
      return instance.get();
    }
  }
  
  private static void setSharedInstance(NitroApplication app) {
    synchronized (instance) {
      instance.set(app);
    }
  }
  
  @ApplicationStatus
  public int getApplicationStatus() {
    return applicationStatus.get();
  }
  
  private void setApplicationStatus(@ApplicationStatus int status) {
    applicationStatus.set(status);
  }
  
  public String getApplicationStatusString() {
    switch (getApplicationStatus()) {
      case ApplicationStatus.BACKGROUND:
        return APP_STATUS_STRING_BACKGROUND;
      case ApplicationStatus.FOREGROUND:
        return APP_STATUS_STRING_FOREGROUND;
    }
    
    return APP_STATUS_STRING_UNKNOWN;
  }
  
  @CallSuper
  @Override
  public void onCreate() {
    super.onCreate();
    NitroApplication.setSharedInstance(this);
    ProcessLifecycleOwner.get().getLifecycle().addObserver(foregroundMonitor);
    Network.getNetworkMonitor().addObserver(this);
    registerBroadcastReceivers();
  }
  
  @CallSuper
  public void onEnterForeground() {
    //stub
  }
  
  @CallSuper
  public void onEnterBackground() {
    //stub
  }
  
  @CallSuper
  public void registerBroadcastReceivers() {
    registerNetworkReceiver();
  }
  
  @CallSuper
  public void unregisterBroadcastReceivers() {
    unregisterNetworkReceiver();
  }
  
  public void onDeviceBootCompleted() {
    // Stub
  }
  
  protected boolean isMultiDexEnabled() {
    return true;
  }
  
  private void setupMultiDex() {
    if (isMultiDexEnabled()) {
      MultiDex.install(this);
    }
  }
  
  @CallSuper
  protected void attachBaseContext(Context base) {
    super.attachBaseContext(base);
    setupMultiDex();
  }
  
  private void registerNetworkReceiver() {
    IntentFilter filter = new IntentFilter();
    filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
    getApplicationContext().registerReceiver(Network.getNetworkReceiver(), filter);
  }
  
  private void unregisterNetworkReceiver() {
    try {
      getApplicationContext().unregisterReceiver(Network.getNetworkReceiver());
    } catch (Exception e) {
      Logger.E(this, e);
    }
  }
  
  class ForegroundMonitor implements LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    final void enterForeground() {
      setApplicationStatus(ApplicationStatus.FOREGROUND);
      onEnterForeground();
    }
    
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    final void enterBackground() {
      setApplicationStatus(ApplicationStatus.BACKGROUND);
      onEnterBackground();
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @IntDef({ApplicationStatus.UNKNOWN, ApplicationStatus.BACKGROUND, ApplicationStatus.FOREGROUND})
  private @interface ApplicationStatus {
    int UNKNOWN = -1;
    int BACKGROUND = 1;
    int FOREGROUND = 2;
  }
}
