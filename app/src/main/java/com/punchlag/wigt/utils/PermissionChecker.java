package com.punchlag.wigt.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

public class PermissionChecker {

    private static final String[] permissionsList = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    public static final int REQUEST_CODE_LOCATION_PERMISSION = 1;

    public static boolean hasLocationPermissionGranted(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED;
    }

    public static void requestLocationPermission(Fragment fragment) {
        fragment.requestPermissions(permissionsList, REQUEST_CODE_LOCATION_PERMISSION);
    }
}
