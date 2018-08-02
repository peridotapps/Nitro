package com.peridotapps.nitro.hardware;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.telephony.TelephonyManager;

import com.peridotapps.nitro.NitroApplication;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static android.net.ConnectivityManager.TYPE_WIFI;

public final class Network {
  
  private static final AtomicReference<ConnectivityManager> connectivityManagerAtomicReference = new AtomicReference<>((ConnectivityManager) NitroApplication.getSharedInstance()
                                                                                                                                                             .getSystemService(Context.CONNECTIVITY_SERVICE));
  private static final AtomicReference<NetworkMonitor> networkMonitor = new AtomicReference<>(new NetworkMonitor());
  private static final AtomicReference<NetworkReceiver> networkReceiver = new AtomicReference<>(new NetworkReceiver());
  private static final AtomicReference<TelephonyManager> telephonyManager = new AtomicReference<>((TelephonyManager) NitroApplication.getSharedInstance()
                                                                                                                                     .getSystemService(Context.TELEPHONY_SERVICE));
  
  @NonNull
  public static NetworkMonitor getNetworkMonitor () {
    NetworkMonitor monitor;
    
    synchronized (networkMonitor) {
      if (networkMonitor.get() == null) {
        networkMonitor.set(new NetworkMonitor());
      }
      
      monitor = networkMonitor.get();
    }
    
    return monitor;
  }
  
  @NonNull
  public static BroadcastReceiver getNetworkReceiver () {
    BroadcastReceiver receiver;
    synchronized (networkReceiver) {
      if (networkReceiver.get() == null) {
        networkReceiver.set(new NetworkReceiver());
      }
      receiver = networkReceiver.get();
    }
    
    return receiver;
  }
  
  @NonNull
  public static NetworkInfo getActiveNetworkInfo () {
    return getConnectivityManager().getActiveNetworkInfo();
  }
  
  @NonNull
  public static TelephonyManager getTelephonyManager () {
    TelephonyManager manager;
    synchronized (telephonyManager) {
      manager = telephonyManager.get();
    }
    
    return manager;
  }
  
  @NonNull
  @RequiresApi(api = Build.VERSION_CODES.M)
  public static android.net.Network getActiveNetwork () {
    return getConnectivityManager().getActiveNetwork();
  }
  
  @NonNull
  @RequiresApi(api = Build.VERSION_CODES.M)
  public static NetworkCapabilities getNetworkCapabilities () {
    return getConnectivityManager().getNetworkCapabilities(getActiveNetwork());
  }
  
  @NonNull
  private static ConnectivityManager getConnectivityManager () {
    ConnectivityManager instance;
    
    synchronized (connectivityManagerAtomicReference) {
      if (connectivityManagerAtomicReference.get() == null) {
        connectivityManagerAtomicReference.set((ConnectivityManager) NitroApplication.getSharedInstance()
                                                                                     .getSystemService(Context.CONNECTIVITY_SERVICE));
      }
      instance = connectivityManagerAtomicReference.get();
    }
    
    return instance;
  }
  
  private Network () {
  }
  
  public static boolean isConnected () {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      NetworkInfo activeNetwork = getActiveNetworkInfo();
      return activeNetwork.isConnected();
    } else {
      NetworkCapabilities capabilities = getNetworkCapabilities();
    
      return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
    }
  }
  
  public static boolean isConnectedOrConnecting () {
    return getActiveNetworkInfo().isConnectedOrConnecting();
  }
  
  public static boolean isMeteredNetwork () {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      return getConnectivityManager().isActiveNetworkMetered();
    }
    return false;
  }
  
  public static int getConnectionType () {
    return getActiveNetworkInfo().getType();
  }
  
  public static int getNetworkType () {
    return getTelephonyManager().getNetworkType();
  }
  
  public static boolean isWifi () {
    return getActiveNetworkInfo().getType() == TYPE_WIFI;
  }
  
  @NonNull
  public static String getNetworkTypeString () {
    return getActiveNetworkInfo().getTypeName();
  }
  
  @NonNull
  public static String getNetworkSubtypeName () {
    return getActiveNetworkInfo().getSubtypeName();
  }
  
  public final static class NetworkMonitor {
    private final List<NetworkStatusObserver> observers = new LinkedList<>();
    
    private NetworkMonitor () {
    }
    
    private void notifyStatusChange (boolean connected) {
      synchronized (observers) {
        if (observers.size() > 0) {
          for (NetworkStatusObserver listener : observers) {
            if (connected) {
              listener.onNetworkConnected(getNetworkTypeString());
            } else {
              listener.onNetworkDisconnected();
            }
          }
        }
      }
    }
    
    public void addObserver (@NonNull NetworkStatusObserver listener) {
      if (!observers.contains(listener)) {
        observers.add(listener);
      }
    }
    
    public void removeObserver (@NonNull NetworkStatusObserver listener) {
      observers.remove(listener);
    }
    
    public void clearObservers () {
      observers.clear();
    }
  }
  
  public interface NetworkStatusObserver {
    void onNetworkConnected (@NonNull String networkType);
    
    void onNetworkDisconnected ();
  }
  
  private final static class NetworkReceiver extends BroadcastReceiver {
    @Override
    public void onReceive (@NonNull Context context, @NonNull Intent intent) {
      if (intent.getAction() != null && (intent.getAction()
                                               .equals(ConnectivityManager.CONNECTIVITY_ACTION))) {
        getNetworkMonitor().notifyStatusChange(isConnected());
      }
    }
  }
  
}
