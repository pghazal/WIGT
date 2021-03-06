package com.punchlag.wigt.maps.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.punchlag.wigt.R;
import com.punchlag.wigt.model.GeofenceModel;

import java.util.List;


public class GeofenceAdapter extends RecyclerView.Adapter<GeofenceViewHolder> {

    private LayoutInflater layoutInflater;
    private List<GeofenceModel> geofences;

    public GeofenceAdapter(Context context, List<GeofenceModel> geofences) {
        super();
        this.layoutInflater = LayoutInflater.from(context);
        this.geofences = geofences;
    }

    @Override
    public GeofenceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_geofence, parent, false);
        return new GeofenceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GeofenceViewHolder holder, int position) {
        GeofenceModel geofenceModel = geofences.get(position);
        holder.bind(geofenceModel);
    }

    @Override
    public int getItemCount() {
        return geofences.size();
    }
}
