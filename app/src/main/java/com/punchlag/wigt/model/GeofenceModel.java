package com.punchlag.wigt.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

public class GeofenceModel implements Parcelable {

    private final String id;
    private final double latitude;
    private final double longitude;
    private final float radius;
    private final long expirationDuration;
    private final int transitionType;
    private boolean enabled;

    public GeofenceModel(String id, double latitude, double longitude, float radius, long expirationDuration, int transitionType, boolean enabled) {
        super();
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.expirationDuration = expirationDuration;
        this.transitionType = transitionType;
        this.enabled = enabled;
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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

    public GeofenceModel(Parcel in) {
        super();
        this.id = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.radius = in.readFloat();
        this.expirationDuration = in.readLong();
        this.transitionType = in.readInt();
        this.enabled = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeFloat(radius);
        dest.writeLong(expirationDuration);
        dest.writeInt(transitionType);
        dest.writeByte((byte) (enabled ? 1 : 0));
    }

    public static final Creator<GeofenceModel> CREATOR = new Creator<GeofenceModel>() {
        @Override
        public GeofenceModel createFromParcel(Parcel in) {
            return new GeofenceModel(in);
        }

        @Override
        public GeofenceModel[] newArray(int size) {
            return new GeofenceModel[size];
        }
    };
}
