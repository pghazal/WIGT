package com.punchlag.wigt.maps;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.punchlag.wigt.utils.Arguments;

import java.util.ArrayList;
import java.util.List;

class MapsPresenter implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnMapClickListener,
        ResultCallback<Status> {

    private static final String TAG = "MapsPresenter";

    private MapsPresenterView mapsPresenterView;

    private GoogleApiClient googleApiClient;
    private GoogleMap googleMap;
    private CameraPosition lastCameraPosition;
    private LocationRequest locationRequest;
    private List<Geofence> geofenceList;

    MapsPresenter(MapsPresenterView mapsPresenterView) {
        this.mapsPresenterView = mapsPresenterView;
        geofenceList = new ArrayList<>();
    }

    void init(Context context) {
        initGoogleApiClient(context);
        initMapSettings();
        restoreLastCameraPosition();
    }

    private synchronized void initGoogleApiClient(Context context) {
        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();
    }

    private void initMapSettings() {
        try {
            this.googleMap.setMyLocationEnabled(true);
            this.googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            this.googleMap.getUiSettings().setMapToolbarEnabled(false);
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
    }

    private void restoreLastCameraPosition() {
        if (lastCameraPosition != null) {
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(lastCameraPosition));
        }
    }

    void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Arguments.ARG_MAP_CAMERA_POSITION, googleMap.getCameraPosition());
    }

    void onViewStateRestored(Bundle savedInstanceState) {
        lastCameraPosition = savedInstanceState.getParcelable(Arguments.ARG_MAP_CAMERA_POSITION);
    }

    void onPause() {
        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }

    void getMapAsync(MapView mapView) {
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setOnMapClickListener(this);
        if (mapsPresenterView != null) {
            mapsPresenterView.onMapReady(googleMap);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Connected to GoogleApiClient");
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (mapsPresenterView != null) {
            mapsPresenterView.onGoogleApiClientConnected();
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (mapsPresenterView != null) {
            mapsPresenterView.onMapClick(latLng);
        }
    }

    void requestLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended to GoogleApiClient");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection to GoogleApiClient Failed");
    }

    @Override
    public void onLocationChanged(Location location) {
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        //move map camera
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(18));
        //stop location updates
        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }

    void addGeofence(Context context, LatLng latLng) {
        try {
            googleMap.clear();

            Geofence geofence = new Geofence.Builder()
                    .setRequestId("ID1")
                    .setCircularRegion(latLng.latitude, latLng.longitude, 100)
                    .setExpirationDuration(12 * 60 * 60 * 1000)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();
            geofenceList.clear();
            geofenceList.add(geofence);
            CircleOptions circleOptions = new CircleOptions()
                    .center(new LatLng(latLng.latitude, latLng.longitude))
                    .radius(100)
                    .fillColor(0x40ff0000)
                    .strokeColor(Color.TRANSPARENT)
                    .strokeWidth(2);
            googleMap.addCircle(circleOptions);
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            LocationServices.GeofencingApi
                    .addGeofences(googleApiClient, getGeofencingRequest(geofenceList), getGeofencePendingIntent(context))
                    .setResultCallback(this);
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
    }

    private GeofencingRequest getGeofencingRequest(List<Geofence> geofenceList) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent(Context context) {
        Intent intent = new Intent(context, GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onResult(@NonNull Status status) {
        Log.d("# onResult #", status.toString() + " " + status.getStatusMessage());
    }
}
