package com.punchlag.wigt.maps.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.TextView;

import com.punchlag.wigt.R;
import com.punchlag.wigt.model.GeofenceModel;
import com.punchlag.wigt.storage.GeofenceStorage;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class GeofenceViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.geofenceIdTv)
    TextView geofenceIdTv;
    @BindView(R.id.latitudeTv)
    TextView latitudeTv;
    @BindView(R.id.longitudeTv)
    TextView longitudeTv;
    @BindView(R.id.switch_enabled)
    SwitchCompat switchEnabled;

    private GeofenceModel geofenceModel;

    public GeofenceViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(GeofenceModel geofenceModel) {
        if (geofenceModel == null) {
            return;
        }

        this.geofenceModel = geofenceModel;

        geofenceIdTv.setText(geofenceModel.getId());
        latitudeTv.setText("Lat: " + geofenceModel.getLatitude());
        longitudeTv.setText("Lon: " + geofenceModel.getLongitude());
        switchEnabled.setChecked(geofenceModel.isEnabled());
    }

    @OnClick(R.id.switch_enabled)
    public void onGeofenceSwitchClicked() {
        GeofenceStorage geofenceStorage = new GeofenceStorage(itemView.getContext());

        geofenceModel.setEnabled(switchEnabled.isChecked());

        geofenceStorage.storeGeofence(geofenceModel.getId(), geofenceModel);
    }
}
