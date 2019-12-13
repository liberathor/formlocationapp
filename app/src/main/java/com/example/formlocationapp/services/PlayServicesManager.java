package com.example.formlocationapp.services;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import com.example.formlocationapp.MainActivity;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import javax.annotation.Nullable;

public class PlayServicesManager implements GpsManagerProxy, GoogleApiClient.ConnectionCallbacks {
    private static final int DEFAULT_ELAPSED_TIME_TO_UPDATE_GPS = 3 * 60 * 1000;
    private final GoogleApiClient.ConnectionCallbacks mCallback;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private boolean mIsConnected;

    PlayServicesManager(Context context, GoogleApiClient.ConnectionCallbacks callback) {
        super();
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(DEFAULT_ELAPSED_TIME_TO_UPDATE_GPS);
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        mCallback = callback;
        if (checkLocationPermission(context))
            return;
        mGoogleApiClient.connect();
    }

    @Override
    public void startUpdates(Context context, LocationListener locationListener) {
        if (checkLocationPermission(context))
            return;
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, locationListener);
    }

    private boolean checkLocationPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                MainActivity.RequestPermissions(context);
                return true;
            }
        }
        return false;
    }

    @Override
    public void stopUpdates(Context context, LocationListener locationListener) {
        if (checkLocationPermission(context))
            return;
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, locationListener);
    }

    @Nullable
    @Override
    public Location getLastKnowLocation(Context context) {
        if (checkLocationPermission(context))
            return null;
        if (!mGoogleApiClient.isConnected())
            return null;
        return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        mCallback.onConnected(bundle);
        mIsConnected = true;
    }

    @Override
    public void onConnectionSuspended(int i) {
        mIsConnected = false;
    }
}
