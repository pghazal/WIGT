package com.punchlag.wigt.maps;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public interface MapsPresenterView {

    void onMapReady(GoogleMap googleMap);

    void onGoogleApiClientConnected();

    void onMapClick(LatLng latLng);
}
