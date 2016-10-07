package com.punchlag.wigt.maps;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsPresenter {

    private IMapsView mapsView;
    private GoogleMap googleMap;

    public MapsPresenter(IMapsView mapsView, GoogleMap googleMap) {
        this.mapsView = mapsView;
        this.googleMap = googleMap;
    }

    public void initMaps(Context context) {
        try {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            // Getting LocationManager object from System Service LOCATION_SERVICE
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();
            // Getting the name of the best provider
            String provider = locationManager.getBestProvider(criteria, true);
            // Getting Current Location
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                // Getting latitude of the current location
                double latitude = location.getLatitude();
                // Getting longitude of the current location
                double longitude = location.getLongitude();
                // Creating a LatLng object for the current location
                LatLng myPosition = new LatLng(latitude, longitude);
                googleMap.addMarker(new MarkerOptions().position(myPosition).title("Start"));
            }
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
    }
}
