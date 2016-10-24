package com.punchlag.wigt.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class StorageManager<T> {

    private Gson gson;
    private SharedPreferences sharedPreferences;

    protected abstract String getSharedPrefsName();

    StorageManager(Context context) {
        gson = new Gson();
        sharedPreferences = context.getSharedPreferences(getSharedPrefsName(), Context.MODE_PRIVATE);
    }

    void store(String key, T value) {
        String json = gson.toJson(value);
        store(key, json);
    }

    private void store(String key, String json) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, json);
        editor.apply();
    }

    void remove(String key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }

    T get(String key, Class<T> clazz) {
        String json = sharedPreferences.getString(key, null);
        return gson.fromJson(json, clazz);
    }

    List<T> getList(Class<T> clazz) {
        List<T> list = new ArrayList<>();
        Map<String, ?> keys = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            T data = get(entry.getKey(), clazz);
            list.add(data);
        }

        return list;
    }

    public String generateId() {
        return getSharedPrefsName() + "_" + UUID.randomUUID().toString();
    }
}
