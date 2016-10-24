package com.punchlag.wigt.maps.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.punchlag.wigt.R;
import com.punchlag.wigt.model.GeofenceModel;

import java.util.List;


public class GeofenceAdapter extends RecyclerView.Adapter<GeofenceViewHolder> {

    private List<GeofenceModel> geofences;

    public GeofenceAdapter(List<GeofenceModel> geofences) {
        super();
        this.geofences = geofences;
    }

    @Override
    public GeofenceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_geofence, null);
        return new GeofenceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GeofenceViewHolder holder, int position) {
        GeofenceModel geofenceModel = geofences.get(position);

        holder.geofenceIdTv.setText(geofenceModel.getId());
        holder.latitudeTv.setText("Lat: " + geofenceModel.getLatitude());
        holder.longitudeTv.setText("Lon: " + geofenceModel.getLongitude());
    }

    @Override
    public int getItemCount() {
        return geofences.size();
    }
}
