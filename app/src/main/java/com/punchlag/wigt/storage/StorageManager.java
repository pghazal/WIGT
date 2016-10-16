package com.punchlag.wigt.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.punchlag.wigt.model.GeofenceModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StorageManager {

    public static final String SHARED_PREFS_GEOFENCES = "SHARED_PREFS_GEOFENCES";

    private Gson gson;
    private SharedPreferences sharedPreferences;

    public StorageManager(Context context, String sharedPrefsName) {
        gson = new Gson();
        sharedPreferences = context.getSharedPreferences(sharedPrefsName, Context.MODE_PRIVATE);
    }

    public void storeGeofence(String key, GeofenceModel value) {
        String json = gson.toJson(value);
        store(key, json);
    }

    private void store(String key, String json) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, json);
        editor.apply();
    }

    public List<GeofenceModel> loadGeofences() {
        List<GeofenceModel> geofences = new ArrayList<>();
        Map<String, ?> keys = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            String json = sharedPreferences.getString(entry.getKey(), null);
            GeofenceModel geofenceModel = gson.fromJson(json, GeofenceModel.class);
            geofences.add(geofenceModel);
        }

        return geofences;
    }
}
