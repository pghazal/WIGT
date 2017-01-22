package com.punchlag.wigt.maps;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class GoogleApiPresenter implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    
    private static final String TAG = "GoogleApiPresenter";

    private IGoogleApiView googleApiView;
    private GoogleApiClient googleApiClient;

    public GoogleApiPresenter(Context context, IGoogleApiView googleApiView) {
        this.googleApiView = googleApiView;
        initialize(context);
    }

    private synchronized void initialize(Context context) {
        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    public void connect() {
        if (googleApiClient != null && (!googleApiClient.isConnecting() || !googleApiClient.isConnected())) {
            googleApiClient.connect();
        }
    }

    public void disconnect() {
        if (googleApiClient != null && (googleApiClient.isConnecting() || googleApiClient.isConnected())) {
            googleApiClient.disconnect();
        }
    }

    public boolean isConnected() {
        return googleApiClient != null && !googleApiClient.isConnecting() && googleApiClient.isConnected();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Connection suspended to GoogleApiClient");
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Connection to GoogleApiClient Failed");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "Connected to GoogleApiClient");
        if (googleApiView != null) {
            googleApiView.onGoogleApiClientConnected();
        }
    }
}
