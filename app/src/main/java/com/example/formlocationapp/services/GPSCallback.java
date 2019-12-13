package com.example.formlocationapp.services;

import android.location.Location;

public interface GPSCallback {
    void onLocationChanged(Location location);
}
