package com.punchlag.wigt.maps;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.punchlag.wigt.R;
import com.punchlag.wigt.fragment.BaseFragment;
import com.punchlag.wigt.utils.NetworkUtils;
import com.punchlag.wigt.utils.PermissionChecker;

import butterknife.BindView;
import butterknife.OnClick;

public class MapsFragment extends BaseFragment implements MapsPresenterView, IGoogleApiView {

    public static final String FRAGMENT_TAG = "MapsFragment";

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 10;

    @BindView(R.id.mapView)
    MapView mapView;

    @BindView(R.id.myLocationButton)
    FloatingActionButton myLocationButton;

    private MapsPresenter mapsPresenter;
    private GoogleApiPresenter googleApiPresenter;

    public static MapsFragment newInstance() {
        MapsFragment fragment = new MapsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.fragment_maps;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkLocationPermission();

        mapsPresenter = new MapsPresenter(this);
        googleApiPresenter = new GoogleApiPresenter(getContext(), this);
    }

    public void configureSubviews(Bundle savedInstanceState) {
        updateActivityContainerConstraint();

        mapView.onCreate(savedInstanceState);
        mapsPresenter.getMapAsync(mapView);
    }

    private void updateActivityContainerConstraint() {
        ViewGroup activityContainer = (ViewGroup) getActivity().findViewById(R.id.activity_container);
        RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) activityContainer.getLayoutParams();
        p.removeRule(RelativeLayout.BELOW);
        activityContainer.setLayoutParams(p);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        mapView.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @OnClick(R.id.myLocationButton)
    public void onMyLocationButtonClicked() {
        if (PermissionChecker.hasLocationPermissionGranted(getContext())) {
            if (googleApiPresenter.isConnected()) {
                mapsPresenter.updateMyLocationCameraPosition(googleApiPresenter.getGoogleApiClient(), true);
            }
        } else {
            // TODO : show pop-up about permissions
            checkLocationPermission();
        }
    }

    private void checkLocationPermission() {
        if (!PermissionChecker.hasLocationPermissionGranted(getContext())) {
            PermissionChecker.requestLocationPermission(this, REQUEST_CODE_LOCATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_LOCATION_PERMISSION: {
                if (PermissionChecker.hasLocationPermissionResultGranted(permissions, grantResults)) {
                    if (PermissionChecker.hasLocationPermissionGranted(getContext())) {
                        mapsPresenter.getMapAsync(mapView);
                    }
                } else {
                    Toast.makeText(getContext(), R.string.text_location_permission_denied, Toast.LENGTH_SHORT).show();
                }
            }
            break;
            default:
                // TODO : show pop-up about permissions
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (PermissionChecker.hasLocationPermissionGranted(getContext())) {
            mapsPresenter.initialize(getContext());
            googleApiPresenter.connect();
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (PermissionChecker.hasLocationPermissionGranted(getContext())) {
            if(googleApiPresenter.isConnected()) {
                mapsPresenter.handleMapClick(getContext(), googleApiPresenter.getGoogleApiClient(), latLng);
            }
        } else {
            // TODO show message maybe + request permission ?
        }
    }

    @Override
    public void onGoogleApiClientConnected() {
        if (PermissionChecker.hasLocationPermissionGranted(getContext())) {
            if (googleApiPresenter.isConnected()) {
                mapsPresenter.restoreLastCameraPosition(googleApiPresenter.getGoogleApiClient());
                mapsPresenter.requestLocationUpdates(getContext(), googleApiPresenter.getGoogleApiClient(), LocationUpdateService.LocationRequestUpdate.HIGH, null);
                mapsPresenter.enableGeofenceTracking(getContext(), googleApiPresenter.getGoogleApiClient());
            }
        }
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
        googleApiPresenter.connect();

        if (!NetworkUtils.isConnectionAvailable(getContext())) {
            Toast.makeText(getContext(), R.string.text_network_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        googleApiPresenter.disconnect();
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
