package com.example.formlocationapp.services;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.location.LocationListener;

public interface GpsManagerProxy {

    void startUpdates(Context context, LocationListener locationListener);

    void stopUpdates(Context context, LocationListener mLocationListener);

    Location getLastKnowLocation(Context context);

}
