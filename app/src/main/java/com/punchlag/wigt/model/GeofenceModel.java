package com.punchlag.wigt.model;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

public class GeofenceModel {

    private final String id;
    private final double latitude;
    private final double longitude;
    private final float radius;
    private final long expirationDuration;
    private final int transitionType;

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

    public CircleOptions getCircleOptions() {
        return new CircleOptions()
                .center(getLatLng())
                .radius(radius)
                .fillColor(0x40ff0000)
                .strokeColor(0x20ff0000)
                .strokeWidth(2);
    }

    public Geofence build() {
        return new Geofence.Builder()
                .setRequestId(id)
                .setCircularRegion(latitude, longitude, radius)
                .setExpirationDuration(expirationDuration)
                .setTransitionTypes(transitionType)
                .build();
    }

    @Override
    public String toString() {
        return "Geofence : " + id;
    }
}
