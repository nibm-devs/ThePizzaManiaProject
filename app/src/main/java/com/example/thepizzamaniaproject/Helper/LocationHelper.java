package com.example.thepizzamaniaproject.Helper;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class LocationHelper {
    private Context context;
    private FusedLocationProviderClient fusedLocationClient;

    public LocationHelper(Context context) {
        this.context = context;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    public void getCurrentLocation(LocationCallback callback) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            callback.onLocationResult(location);
                        } else {
                            callback.onLocationError("Unable to get location");
                        }
                    })
                    .addOnFailureListener(e -> {
                        callback.onLocationError(e.getMessage());
                    });
        } else {
            callback.onLocationError("Location permission not granted");
        }
    }

    public interface LocationCallback {
        void onLocationResult(Location location);
        void onLocationError(String error);
    }
}
