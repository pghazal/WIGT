package com.punchlag.wigt.maps;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.punchlag.wigt.model.GeofenceModel;
import com.punchlag.wigt.storage.StorageManager;
import com.punchlag.wigt.utils.Arguments;

import java.util.ArrayList;
import java.util.List;

class MapsPresenter implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMapClickListener {

    private static final String TAG = "MapsPresenter";

    private MapsPresenterView mapsPresenterView;

    private StorageManager storageManager;
    private GoogleApiClient googleApiClient;
    private GoogleMap googleMap;

    private CameraPosition lastCameraPosition;
    private List<GeofenceModel> geofences;

    MapsPresenter(MapsPresenterView mapsPresenterView) {
        this.mapsPresenterView = mapsPresenterView;
        this.geofences = new ArrayList<>();
    }

    void init(Context context) {
        storageManager = new StorageManager(context, StorageManager.SHARED_PREFS_GEOFENCES);
        geofences.addAll(storageManager.loadGeofences());

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

    void onPause(Context context) {
        if (googleApiClient != null) {
            //LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, getLocationUpdatePendingIntent(context));
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
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended to GoogleApiClient");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection to GoogleApiClient Failed");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Connected to GoogleApiClient");
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

    void requestLocationUpdates(Context context) {
        try {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(3 * 60 * 1000); // 3 min
            locationRequest.setFastestInterval(30 * 1000); // 30 sec
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setSmallestDisplacement(25);
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, getLocationUpdatePendingIntent(context));
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
    }

    private PendingIntent getLocationUpdatePendingIntent(Context context) {
        Intent service = new Intent(context, LocationUpdateService.class);
        return PendingIntent.getService(context, 0, service, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    void enableGeofencingTracking(Context context) {
        displayGeofences();
        try {
            LocationServices.GeofencingApi
                    .addGeofences(googleApiClient, getGeofencingRequest(getGoogleGeofences()), getGeofencePendingIntent(context));
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
    }

    void addGeofence(Context context, LatLng latLng) {
        String id = "ID";
        int radius = 100;
        GeofenceModel geofenceModel = new GeofenceModel(id, latLng.latitude, latLng.longitude, radius,
                Geofence.NEVER_EXPIRE, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT);
        geofences.clear();
        geofences.add(geofenceModel);

        displayGeofences();
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(geofenceModel.getLatLng()));

        try {
            LocationServices.GeofencingApi
                    .addGeofences(googleApiClient, getGeofencingRequest(getGoogleGeofences()),
                            getGeofencePendingIntent(context)).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    Log.d("# onResult #", status.toString() + " " + status.getStatusMessage());

                    if (status.isSuccess()) {
                        Log.d("# onResult #", "Count geofences : " + geofences.size());
                        storageManager.storeGeofence(geofences.get(0).getId(), geofences.get(0));
                    }
                }
            });
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
        Intent intent = new Intent(context, GeofenceTransitionsService.class);
        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void displayGeofences() {
        googleMap.clear();

        for (GeofenceModel geofence : geofences) {
            CircleOptions circleOptions = new CircleOptions()
                    .center(geofence.getLatLng())
                    .radius(geofence.getRadius())
                    .fillColor(0x40ff0000)
                    .strokeColor(0x20ff0000)
                    .strokeWidth(2);
            googleMap.addCircle(circleOptions);
        }
    }

    private List<Geofence> getGoogleGeofences() {
        List<Geofence> geofenceList = new ArrayList<>();
        for (GeofenceModel geofenceModel : geofences) {
            geofenceList.add(geofenceModel.build());
        }
        return geofenceList;
    }
}
