package com.punchlag.wigt.storage;

import android.content.Context;

import com.punchlag.wigt.model.GeofenceModel;

import java.util.List;

public class GeofenceStorage extends StorageManager<GeofenceModel> {

    private static final String SHARED_PREFS_GEOFENCES = "SHARED_PREFS_GEOFENCES";

    @Override
    protected String getSharedPrefsName() {
        return SHARED_PREFS_GEOFENCES;
    }

    public GeofenceStorage(Context context) {
        super(context);
    }

    public void storeGeofence(String key, GeofenceModel value) {
        store(key, value);
    }

    public void removeGeofence(GeofenceModel value) {
        remove(value.getId());
    }

    public List<GeofenceModel> loadGeofences() {
        return getList(GeofenceModel.class);
    }
}
