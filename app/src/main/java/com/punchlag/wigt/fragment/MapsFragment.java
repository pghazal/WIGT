package com.punchlag.wigt.fragment;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;
import butterknife.BindView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.punchlag.wigt.R;
import com.punchlag.wigt.utils.PermissionChecker;

import static android.content.Context.LOCATION_SERVICE;

public class MapsFragment extends BaseFragment implements OnMapReadyCallback {

    public static final String FRAGMENT_TAG = MapsFragment.class.getSimpleName();

    @BindView(R.id.mapView)
    MapView mapView;

    private GoogleMap mMap;

    public static MapsFragment newInstance() {
        MapsFragment mapFragment = new MapsFragment();
        Bundle args = new Bundle();
        mapFragment.setArguments(args);
        return mapFragment;
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.fragment_maps;
    }

    public void configureSubviews(Bundle savedInstanceState) {
        initMapView(savedInstanceState);
    }

    private void initMapView(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (PermissionChecker.hasLocationPermissionGranted(getContext())) {
            try {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                // Getting LocationManager object from System Service LOCATION_SERVICE
                LocationManager locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
                // Creating a criteria object to retrieve provider
                Criteria criteria = new Criteria();
                // Getting the name of the best provider
                String provider = locationManager.getBestProvider(criteria, true);
                // Getting Current Location
                Location location = locationManager.getLastKnownLocation(provider);
                if (location != null) {
                    // Getting latitude of the current location
                    double latitude = location.getLatitude();
                    // Getting longitude of the current location
                    double longitude = location.getLongitude();
                    // Creating a LatLng object for the current location
                    LatLng myPosition = new LatLng(latitude, longitude);
                    googleMap.addMarker(new MarkerOptions().position(myPosition).title("Start"));
                }
            } catch (SecurityException ex) {
                ex.printStackTrace();
            }
        } else {
            PermissionChecker.requestLocationPermission(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionChecker.REQUEST_CODE_LOCATION_PERMISSION:
                Toast.makeText(getContext(),
                        "Permission : " + permissions + "\nGranted : " + grantResults, Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
