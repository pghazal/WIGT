package com.punchlag.wigt.maps;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.punchlag.wigt.utils.Arguments;

class MapsPresenter implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private MapsPresenterView mapsPresenterView;

    private GoogleApiClient mGoogleApiClient;
    private GoogleMap googleMap;
    private CameraPosition mLastCameraPosition;
    private LocationRequest mLocationRequest;

    MapsPresenter(MapsPresenterView mapsPresenterView) {
        this.mapsPresenterView = mapsPresenterView;
    }

    void init(Context context) {
        initGoogleApiClient(context);
        initMapSettings();
        restoreLastCameraPosition();
    }

    private void initGoogleApiClient(Context context) {
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
}
