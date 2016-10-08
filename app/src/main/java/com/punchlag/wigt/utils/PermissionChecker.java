package com.punchlag.wigt.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import java.util.Arrays;

public class PermissionChecker {

    private static final String[] locationPermissionsList = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    public static boolean hasLocationPermissionGranted(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean hasLocationPermissionResultGranted(@NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && Arrays.equals(locationPermissionsList, permissions)
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        return false;
    }

    public static void requestLocationPermission(Fragment fragment, int requestCode) {
        fragment.requestPermissions(locationPermissionsList, requestCode);
    }
}
