package com.example.thepizzamaniaproject.Activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import com.example.thepizzamaniaproject.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.LatLngBounds;

public class RiderMapActivity extends FragmentActivity implements OnMapReadyCallback {
    private double riderLat, riderLng, customerLat, customerLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_map);

        Intent intent = getIntent();
        riderLat = intent.getDoubleExtra("riderLat", 0);
        riderLng = intent.getDoubleExtra("riderLng", 0);
        customerLat = intent.getDoubleExtra("customerLat", 0);
        customerLng = intent.getDoubleExtra("customerLng", 0);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        LatLng riderLocation = new LatLng(riderLat, riderLng);
        LatLng customerLocation = new LatLng(customerLat, customerLng);

        // Add markers for rider and customer
        googleMap.addMarker(new MarkerOptions().position(riderLocation).title("Rider Location").snippet("You are here"));
        googleMap.addMarker(new MarkerOptions().position(customerLocation).title("Customer Location"));

        // Move camera to show both markers
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(customerLocation, 13));

        // Optionally, zoom out to fit both markers
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(riderLocation);
        builder.include(customerLocation);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
    }
}
