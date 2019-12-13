package com.example.formlocationapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.formlocationapp.services.LocationService;
import com.example.formlocationapp.ui.main.MainFragment;
import com.example.formlocationapp.ui.main.MapsActivity;

public class MainActivity extends AppCompatActivity {
    public static final String ACTION_REQUEST_PERMISSIONS = "ACTION_REQUEST_PERMISSIONS";
    private static final int MY_PERMISSION_REQUEST_STORAGE = 7620;
    private static final int MY_PERMISSION_REQUEST_LOCATION = 1243;
    private static final int MY_PERMISSION_REQUEST_FINE_LOCATION = 157;
    private static final int MY_PERMISSION_REQUEST_HONE_STATE = 563;

    public static void RequestPermissions(Context context) {
        Intent starter = new Intent(context, MainActivity.class);
        starter.putExtra(ACTION_REQUEST_PERMISSIONS, true);
        starter.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(starter);
    }

    public void showMap() {
        MapsActivity.start(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow();
        }
        if (getIntent().getBooleanExtra(ACTION_REQUEST_PERMISSIONS, false)) {
            checkPermissions();
        }
        startTracking();
    }

    //TODO: use this method to stopTracking();
    public void stopTracking() {
        LocationService.stopTracking(this);
    }

    public void startTracking() {
        LocationService.startTracking(this);
    }

    public void checkPermissions() {
        checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, MY_PERMISSION_REQUEST_FINE_LOCATION);
        checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, MY_PERMISSION_REQUEST_LOCATION);
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, MY_PERMISSION_REQUEST_STORAGE);
        checkPermission(Manifest.permission.READ_PHONE_STATE, MY_PERMISSION_REQUEST_HONE_STATE);
    }

    public void checkPermission(String permissionToRequest, int myPermissionRequestId) {
        int permissionCheck = ContextCompat.checkSelfPermission(this, permissionToRequest);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissionToRequest)) {
                AlertBuilder.ShowAlert(this, "Please accept permission", new AlertBuilder.OnResponseUser<Boolean>() {
                    @Override
                    public void onResponse(Boolean response) {
                        if (response) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{permissionToRequest}, myPermissionRequestId);
                        } else {
                            Toast.makeText(MainActivity.this, "Permissions rejected", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError() {

                    }
                });
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permissionToRequest}, myPermissionRequestId);
            }
        }
    }
}
