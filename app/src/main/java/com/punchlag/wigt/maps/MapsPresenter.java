package com.punchlag.wigt.maps;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;

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
import com.google.android.gms.maps.model.LatLng;
import com.punchlag.wigt.model.GeofenceModel;
import com.punchlag.wigt.storage.GeofenceStorage;

import java.util.ArrayList;
import java.util.List;

class MapsPresenter implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private static final String TAG = "MapsPresenter";

    private static final float MY_LOCATION_ZOOM = 15.0f;

    private MapsPresenterView mapsPresenterView;

    private GeofenceStorage geofenceStorage;
    private GoogleMap googleMap;

    private List<GeofenceModel> geofences;

    MapsPresenter(MapsPresenterView mapsPresenterView) {
        this.mapsPresenterView = mapsPresenterView;
        this.geofences = new ArrayList<>();
    }

    void initialize(Context context) {
        geofenceStorage = new GeofenceStorage(context);
        geofences.addAll(geofenceStorage.loadGeofences());

        initMapSettings();
    }

    private void initMapSettings() {
        try {
            this.googleMap.setMyLocationEnabled(true);
            this.googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            this.googleMap.getUiSettings().setMapToolbarEnabled(true);
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
    }

    void onPause(Context context) {
       /* if (isGoogleApiClientAvailable()) {
            requestLocationUpdates(context, LocationUpdateService.LocationRequestUpdate.LOW, null);
        }*/
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

    public void updateMyLocationCameraPosition(GoogleApiClient googleApiClient, boolean animate) {
        try {
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (lastLocation != null) {
                LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                if (animate) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, MY_LOCATION_ZOOM));
                } else {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, MY_LOCATION_ZOOM));
                }
            }
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
    }

    public void restoreLastCameraPosition(GoogleApiClient googleApiClient) {
        updateMyLocationCameraPosition(googleApiClient, false);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (mapsPresenterView != null) {
            mapsPresenterView.onMapClick(latLng);
        }
    }

    void requestLocationUpdates(Context context, GoogleApiClient googleApiClient, LocationUpdateService.LocationRequestUpdate locationRequestUpdate, ResultCallback<Status> resultCallback) {
        try {
            LocationRequest locationRequest = LocationUpdateService.LocationRequestUpdate.build(locationRequestUpdate);
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, getLocationUpdatePendingIntent(context))
                    .setResultCallback(resultCallback);
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
    }

    private PendingIntent getLocationUpdatePendingIntent(Context context) {
        Intent service = new Intent(context, LocationUpdateService.class);
        return PendingIntent.getService(context, 0, service, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    void enableGeofenceTracking(Context context, GoogleApiClient googleApiClient) {
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

    void handleMapClick(Context context, GoogleApiClient googleApiClient, final LatLng latLng) {
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
            removeGeofence(googleApiClient, geofenceClicked);
            drawAllGeofences();
        } else {
            GeofenceModel geofenceModel = addGeofence(context, googleApiClient, latLng);
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

    private void removeGeofence(GoogleApiClient googleApiClient, final GeofenceModel geofenceModel) {
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
                                geofenceStorage.removeGeofence(geofenceModel);
                            }
                        }
                    });
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
    }

    private GeofenceModel addGeofence(Context context, GoogleApiClient googleApiClient, LatLng latLng) {
        String id = geofenceStorage.generateId();
        int radius = 100;
        final GeofenceModel geofenceModel = new GeofenceModel(id, latLng.latitude, latLng.longitude, radius,
                Geofence.NEVER_EXPIRE, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT, true);

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
                        geofenceStorage.storeGeofence(geofenceModel.getId(), geofenceModel);
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
