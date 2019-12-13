package com.example.formlocationapp.services;

public class GpsSettings {
    // Elapsed time in millis to update
    static GPSCallback mGpsCallback = null;

    public static void setGpsCallback(GPSCallback mGpsCallback) {
        GpsSettings.mGpsCallback = mGpsCallback;
    }
}
