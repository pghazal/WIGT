package com.punchlag.wigt.model;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.model.LatLng;

public class GeofenceModel {

    private final String id;
    private final double latitude;
    private final double longitude;
    private final float radius;
    private long expirationDuration;
    private int transitionType;

    public GeofenceModel(String id, double latitude, double longitude, float radius, long expirationDuration, int transitionType) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.expirationDuration = expirationDuration;
        this.transitionType = transitionType;
    }

    public String getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public float getRadius() {
        return radius;
    }

    public long getExpirationDuration() {
        return expirationDuration;
    }

    public int getTransitionType() {
        return transitionType;
    }

    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }

    public Geofence build() {
        return new Geofence.Builder()
                .setRequestId(id)
                .setCircularRegion(latitude, longitude, radius)
                .setExpirationDuration(expirationDuration)
                .setTransitionTypes(transitionType)
                .build();
    }
}
