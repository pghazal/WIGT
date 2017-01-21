package com.punchlag.wigt.maps;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.punchlag.wigt.R;
import com.punchlag.wigt.fragment.BaseFragment;
import com.punchlag.wigt.maps.adapter.GeofenceAdapter;
import com.punchlag.wigt.model.GeofenceModel;
import com.punchlag.wigt.storage.GeofenceStorage;

import java.util.List;

import butterknife.BindView;

public class GeofencesListFragment extends BaseFragment {

    public static final String FRAGMENT_TAG = "GeofencesListFragment";

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private GeofenceStorage geofenceStorage;
    private List<GeofenceModel> geofences;
    private GeofenceAdapter geofenceAdapter;

    public static GeofencesListFragment newInstance() {
        GeofencesListFragment fragment = new GeofencesListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        geofenceStorage = new GeofenceStorage(getContext());
        geofences = geofenceStorage.loadGeofences();
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.fragment_geofences_list;
    }

    @Override
    public void configureSubviews(Bundle savedInstanceState) {
        updateActivityContainerConstraint();

        geofenceAdapter = new GeofenceAdapter(geofences);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(geofenceAdapter);
    }

    private void updateActivityContainerConstraint() {
        ViewGroup activityContainer = (ViewGroup) getActivity().findViewById(R.id.activity_container);
        RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) activityContainer.getLayoutParams();
        p.addRule(RelativeLayout.BELOW, R.id.toolbar);
        activityContainer.setLayoutParams(p);
    }
}
