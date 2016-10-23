package com.punchlag.wigt.maps;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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

    void onResume(Context context) {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            requestLocationUpdates(context, LocationUpdateService.LocationRequestUpdate.HIGH);
        }
    }

    void onPause(Context context) {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            requestLocationUpdates(context, LocationUpdateService.LocationRequestUpdate.LOW);
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

    void requestLocationUpdates(Context context, LocationUpdateService.LocationRequestUpdate locationRequestUpdate) {
        try {
            LocationRequest locationRequest = LocationUpdateService.LocationRequestUpdate.build(locationRequestUpdate);
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, getLocationUpdatePendingIntent(context));
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
    }

    private PendingIntent getLocationUpdatePendingIntent(Context context) {
        Intent service = new Intent(context, LocationUpdateService.class);
        return PendingIntent.getService(context, 0, service, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    void enableGeofenceTracking(Context context) {
        List<Geofence> geofences = getGoogleGeofences();
        if (!geofences.isEmpty()) {
            drawAllGeofences();
            try {
                LocationServices.GeofencingApi
                        .addGeofences(googleApiClient, getGeofencingRequest(geofences), getGeofencePendingIntent(context));
            } catch (SecurityException ex) {
                ex.printStackTrace();
            }
        }
    }

    void handleMapClick(Context context, final LatLng latLng) {
        GeofenceModel geofenceClicked = null;
        for (GeofenceModel geofenceModel : geofences) {
            if (hasGeofenceClicked(latLng, geofenceModel)) {
                geofenceClicked = geofenceModel;
                break;
            }
        }

        // Geofence clicked and found
        if (geofenceClicked != null) {
            Log.d(TAG, "Geofence Clicked with id : " + geofenceClicked.getId());
            removeGeofence(geofenceClicked);
        } else {
            GeofenceModel geofenceModel = addGeofence(context, latLng);
            drawGeofence(geofenceModel);
        }
    }

    private boolean hasGeofenceClicked(final LatLng clickedPosition, final GeofenceModel geofenceModel) {
        LatLng center = geofenceModel.getLatLng();
        double radius = geofenceModel.getRadius();
        float[] distance = new float[1];
        Location.distanceBetween(clickedPosition.latitude, clickedPosition.longitude, center.latitude, center.longitude, distance);
        return distance[0] < radius;
    }

    private void removeGeofence(final GeofenceModel geofenceModel) {
        try {
            List<String> geofenceIdToRemove = new ArrayList<>();
            geofenceIdToRemove.add(geofenceModel.getId());
            geofences.remove(geofenceModel);

            LocationServices.GeofencingApi.removeGeofences(googleApiClient, geofenceIdToRemove)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                                Log.d(TAG, "REMOVE : " + geofenceModel.toString());
                                storageManager.removeGeofence(geofenceModel);
                            }
                        }
                    });
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
    }

    private GeofenceModel addGeofence(Context context, LatLng latLng) {
        String id = storageManager.generateId();
        int radius = 100;
        final GeofenceModel geofenceModel = new GeofenceModel(id, latLng.latitude, latLng.longitude, radius,
                Geofence.NEVER_EXPIRE, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT);

        geofences.add(geofenceModel);
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(geofenceModel.getLatLng()));

        try {
            LocationServices.GeofencingApi
                    .addGeofences(googleApiClient, getGeofencingRequest(getGoogleGeofences()),
                            getGeofencePendingIntent(context)).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if (status.isSuccess()) {
                        Log.d(TAG, "ADD : " + geofenceModel.toString());
                        storageManager.storeGeofence(geofenceModel.getId(), geofenceModel);
                    }
                }
            });
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }

        return geofenceModel;
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

    private void drawAllGeofences() {
        googleMap.clear();

        for (GeofenceModel geofence : geofences) {
            drawGeofence(geofence);
        }
    }

    private void drawGeofence(GeofenceModel geofenceModel) {
        googleMap.addCircle(geofenceModel.getCircleOptions());
    }

    private List<Geofence> getGoogleGeofences() {
        List<Geofence> geofenceList = new ArrayList<>();
        for (GeofenceModel geofenceModel : geofences) {
            geofenceList.add(geofenceModel.build());
        }
        return geofenceList;
    }
}
