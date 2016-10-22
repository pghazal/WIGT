package com.punchlag.wigt.maps;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;


public class LocationUpdateService extends IntentService {

    private static final String TAG = "LocationUpdateService";

    public enum LocationRequestUpdate {
        LOW(60 * 1000, 30 * 1000, LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, 30),
        HIGH(20 * 1000, 10 * 1000, LocationRequest.PRIORITY_HIGH_ACCURACY, 10);

        private final int interval;
        private final int fastestInterval;
        private final int priority;
        private final int smallestDisplacement;

        LocationRequestUpdate(int interval, int fastestInterval, int priority, int smallestDisplacement) {
            this.interval = interval;
            this.fastestInterval = fastestInterval;
            this.priority = priority;
            this.smallestDisplacement = smallestDisplacement;
        }

        public static LocationRequest build(LocationRequestUpdate locationRequestUpdate) {
            return new LocationRequest()
                    .setInterval(locationRequestUpdate.interval)
                    .setFastestInterval(locationRequestUpdate.fastestInterval)
                    .setPriority(locationRequestUpdate.priority)
                    .setSmallestDisplacement(locationRequestUpdate.smallestDisplacement);
        }
    }

    public LocationUpdateService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "# onLocationChanged");

        if (LocationResult.hasResult(intent)) {
            LocationResult locationResult = LocationResult.extractResult(intent);
            Location location = locationResult.getLastLocation();
            if (location != null) {
                Log.d("locationtesting", "accuracy: " + location.getAccuracy() + " lat: " + location.getLatitude() + " lon: " + location.getLongitude());
            }
        }
    }
}
