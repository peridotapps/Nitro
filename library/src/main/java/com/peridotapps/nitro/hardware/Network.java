package com.peridotapps.nitro.hardware;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;

import com.peridotapps.nitro.NitroApplication;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static android.net.ConnectivityManager.TYPE_WIFI;

public final class Network {

    private static final AtomicReference<ConnectivityManager> connectivityManagerAtomicReference = new AtomicReference<>((ConnectivityManager) NitroApplication.getSharedInstance().getSystemService(Context.CONNECTIVITY_SERVICE));
    private static final AtomicReference<NetworkMonitor> networkMonitor = new AtomicReference<>(new NetworkMonitor());
    private static final AtomicReference<NetworkReceiver> networkReceiver = new AtomicReference<>(new NetworkReceiver());

    public static NetworkMonitor getNetworkMonitor() {
        NetworkMonitor monitor;

        synchronized (networkMonitor) {
            if (networkMonitor.get() == null) {
                networkMonitor.set(new NetworkMonitor());
            }

            monitor = networkMonitor.get();
        }

        return monitor;
    }

    public static BroadcastReceiver getNetworkReceiver() {
        BroadcastReceiver receiver;
        synchronized (networkReceiver) {
            if (networkReceiver.get() == null) {
                networkReceiver.set(new NetworkReceiver());
            }
            receiver = networkReceiver.get();
        }

        return receiver;
    }

    public static NetworkInfo getActiveNetwork() {
        return getConnectivityManager().getActiveNetworkInfo();
    }

    private static ConnectivityManager getConnectivityManager() {
        ConnectivityManager instance;

        synchronized (connectivityManagerAtomicReference) {
            if (connectivityManagerAtomicReference.get() == null) {
                connectivityManagerAtomicReference.set((ConnectivityManager) NitroApplication.getSharedInstance().getSystemService(Context.CONNECTIVITY_SERVICE));
            }
            instance = connectivityManagerAtomicReference.get();
        }

        return instance;
    }

    private Network() {
    }

    public static boolean isConnected() {
        NetworkInfo activeNetwork = getActiveNetwork();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static boolean isMeteredNetwork() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return getConnectivityManager().isActiveNetworkMetered();
        }
        return false;
    }

    public static boolean isWifi() {
        return getActiveNetwork().getType() == TYPE_WIFI;
    }

    public final static class NetworkMonitor {

        private final List<NetworkStatusObserver> observers = new LinkedList<>();

        private NetworkMonitor() {
        }

        private void notifyStatusChange(boolean connected) {
            synchronized (observers) {
                if (observers.size() > 0) {
                    for (NetworkStatusObserver listener : observers) {
                        if (connected) {
                            listener.onNetworkConnected();
                        } else {
                            listener.onNetworkDisconnected();
                        }
                    }
                }
            }
        }

        public void addObserver(@NonNull NetworkStatusObserver listener) {
            if (!observers.contains(listener)) {
                observers.add(listener);
            }
        }

        public void removeObserver(@NonNull NetworkStatusObserver listener) {
            observers.remove(listener);
        }

        public void clearObservers() {
            observers.clear();
        }

    }

    private final static class NetworkReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null
                    && (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)))

                getNetworkMonitor().notifyStatusChange(isConnected());

        }
    }

    public interface NetworkStatusObserver {
        void onNetworkConnected();

        void onNetworkDisconnected();
    }
}
