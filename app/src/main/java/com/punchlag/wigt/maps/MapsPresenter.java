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
import com.google.android.gms.location.*;
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
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnMapClickListener, ResultCallback<Status> {

    private MapsPresenterView mapsPresenterView;

    private GoogleApiClient mGoogleApiClient;
    private GoogleMap googleMap;
    private CameraPosition mLastCameraPosition;
    private LocationRequest mLocationRequest;
    private List<Geofence> mGeofenceList;

    MapsPresenter(MapsPresenterView mapsPresenterView) {
        this.mapsPresenterView = mapsPresenterView;
        mGeofenceList = new ArrayList<>();
    }

    void init(Context context) {
        initGoogleApiClient(context);
        initMapSettings();
        restoreLastCameraPosition();
    }

    private synchronized void initGoogleApiClient(Context context) {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
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
        if (mLastCameraPosition != null) {
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(mLastCameraPosition));
        }
    }

    void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Arguments.ARG_MAP_CAMERA_POSITION, googleMap.getCameraPosition());
    }

    void onViewStateRestored(Bundle savedInstanceState) {
        mLastCameraPosition = savedInstanceState.getParcelable(Arguments.ARG_MAP_CAMERA_POSITION);
    }

    void onPause() {
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
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
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
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
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        //move map camera
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(18));
        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    void addGeofences(Context context, LatLng latLng) {
        try {
            googleMap.clear();

       /* MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Lat: " + latLng.latitude + " Long: " + latLng.longitude);
        googleMap.addMarker(markerOptions);*/
            Geofence geofence = new Geofence.Builder()
                    .setRequestId("ID1")
                    .setCircularRegion(latLng.latitude, latLng.longitude, 100)
                    .setExpirationDuration(12 * 60 * 60 * 1000)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();
            mGeofenceList.clear();
            mGeofenceList.add(geofence);
            CircleOptions circleOptions = new CircleOptions()
                    .center(new LatLng(latLng.latitude, latLng.longitude))
                    .radius(100)
                    .fillColor(0x40ff0000)
                    .strokeColor(Color.TRANSPARENT)
                    .strokeWidth(2);
            googleMap.addCircle(circleOptions);
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            LocationServices.GeofencingApi
                    .addGeofences(mGoogleApiClient, getGeofencingRequest(), getGeofencePendingIntent(context))
                    .setResultCallback(this);
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
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
