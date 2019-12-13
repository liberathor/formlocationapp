package com.example.formlocationapp.services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.formlocationapp.MainActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;


public class LocationService extends Service {
    public static final String TAG = LocationService.class.getSimpleName();
    public static final String INTENT_START_TRACKING = "com.example.formlocationapp.ACTION_START_TRACKING";
    public static final String INTENT_STOP_TRACKING = "com.example.formlocationapp..ACTION_STOP_TRAKING";
    private static final Object LOCK = new Object();
    public static PlayServicesManager mGpsManagerProxy;
    private final Binder mBinder = new LocalBinder();
    private LocationListener mLocationListener;
    private GoogleApiClient.ConnectionCallbacks mConnectedCallback;
    private boolean mIsTracking;

    public LocationService() {
    }

    public static void bindService(Context context, ServiceConnection serviceConnection, int flags) {
        Intent intent = new Intent(context, LocationService.class);
        context.bindService(intent, serviceConnection, flags);
    }

    public static void startTracking(Context context) {
        Intent intent = new Intent(context, LocationService.class);
        intent.setAction(LocationService.INTENT_START_TRACKING);
        context.startService(intent);
    }

    public static void stopTracking(Context context) {
        Intent intent = new Intent(context, LocationService.class);
        intent.setAction(LocationService.INTENT_STOP_TRACKING);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
        if (mGpsManagerProxy == null) {
            Context context = getApplicationContext();
            mGpsManagerProxy = new PlayServicesManager(context, getConnectedCallback());
        }
    }

    @NonNull
    private GoogleApiClient.ConnectionCallbacks getConnectedCallback() {
        if (mConnectedCallback == null) {
            synchronized (LOCK) {
                mConnectedCallback = new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        if (mIsTracking) {
                            mGpsManagerProxy.startUpdates(LocationService.this, getLocationListener());
                        }
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        mGpsManagerProxy.stopUpdates(LocationService.this, getLocationListener());
                    }
                };
            }
        }
        return mConnectedCallback;
    }

    public boolean checkGooglePlayServices() {
        Context context = getApplicationContext();
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(context);
        return resultCode == ConnectionResult.SUCCESS;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() " + intent);
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(INTENT_START_TRACKING)) {
                    startsWorking(getLocationListener());
                } else if (action.equals(INTENT_STOP_TRACKING)) {
                    stopsWorking(getLocationListener());
                    return super.onStartCommand(intent, flags, startId);
                }
            }
        }
        startsWorking(getLocationListener());
        return START_STICKY;
    }

    @NonNull
    private LocationListener getLocationListener() {
        if (mLocationListener == null) {
            synchronized (LOCK) {
                mLocationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        showLocationToast(location);
                    }
                };
            }
        }
        return mLocationListener;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    f

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private void startsWorking(LocationListener mLocationListener) {
        mIsTracking = true;
    }

    private void stopsWorking(LocationListener mLocationListener) {
        mIsTracking = false;
    }

    private void showLocationToast(Location location) {
        Log.d(TAG, "IMEI: " + getImei() + " LOCATION: " + location.getLatitude() + "," + location.getLongitude());
        if (location != null) {
            Toast.makeText(this, "IMEI: " + getImei() + "\nLOCATION: " + location.getLatitude() + "," + location.getLongitude(), Toast.LENGTH_LONG).show();
        }
    }

    public String getImei() {
        if (checkPhoneStatePermission())
            return "Check permissions";
        return ((TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
    }

    private boolean checkPhoneStatePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                MainActivity.RequestPermissions(this);
                return true;
            }
        }
        return false;
    }

    public class LocalBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }
}
