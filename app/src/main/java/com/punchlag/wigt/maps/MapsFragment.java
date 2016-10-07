package com.punchlag.wigt.maps;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.punchlag.wigt.R;
import com.punchlag.wigt.fragment.BaseFragment;
import com.punchlag.wigt.utils.NetworkUtils;
import com.punchlag.wigt.utils.PermissionChecker;

import butterknife.BindView;

public class MapsFragment extends BaseFragment implements OnMapReadyCallback, IMapsView {

    public static final String FRAGMENT_TAG = MapsFragment.class.getSimpleName();

    @BindView(R.id.mapView)
    MapView mapView;

    private MapsPresenter mapsPresenter;

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
        if (!NetworkUtils.isConnectionAvailable(getContext())) {
            Toast.makeText(getContext(), R.string.text_network_not_available, Toast.LENGTH_SHORT).show();
        }

        mapsPresenter = new MapsPresenter(this, googleMap);

        if (PermissionChecker.hasLocationPermissionGranted(getContext())) {
            mapsPresenter.initMaps(getContext());
        } else {
            PermissionChecker.requestLocationPermission(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionChecker.REQUEST_CODE_LOCATION_PERMISSION:
                mapsPresenter.initMaps(getContext());
                break;
            default:
                // TODO : show pop-up about permissions
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
