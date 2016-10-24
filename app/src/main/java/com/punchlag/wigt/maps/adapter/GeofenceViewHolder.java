package com.punchlag.wigt.maps.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.punchlag.wigt.R;


public class GeofenceViewHolder extends RecyclerView.ViewHolder {

    TextView geofenceIdTv;
    TextView latitudeTv;
    TextView longitudeTv;

    public GeofenceViewHolder(View itemView) {
        super(itemView);
        geofenceIdTv = (TextView) itemView.findViewById(R.id.geofenceIdTv);
        latitudeTv = (TextView) itemView.findViewById(R.id.latitudeTv);
        longitudeTv = (TextView) itemView.findViewById(R.id.longitudeTv);
    }
}
