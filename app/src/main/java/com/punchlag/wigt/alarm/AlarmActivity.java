package com.punchlag.wigt.alarm;

import android.content.DialogInterface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.punchlag.wigt.model.GeofenceModel;
import com.punchlag.wigt.storage.GeofenceStorage;

public class AlarmActivity extends AppCompatActivity {

    private Ringtone ringtone;

    private GeofenceStorage geofenceStorage;
    private GeofenceModel geofenceModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parseExtras(getIntent().getExtras());

        geofenceStorage = new GeofenceStorage(getApplicationContext());

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtone = RingtoneManager.getRingtone(this, uri);
        ringtone.play();

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Geofence reached")
                .setMessage(geofenceModel.getId())
                .setCancelable(false)
                .setPositiveButton("Great",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if(geofenceModel.isEnabled()) {
                                    disableGeofence(geofenceModel);
                                }

                                ringtone.stop();

                                dialog.dismiss();
                                finish();
                            }
                        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void parseExtras(Bundle extras) {
        if (extras == null) {
            return;
        }

        this.geofenceModel = extras.getParcelable(AlarmReceiver.KEY_EXTRA_DATA);
    }

    private void disableGeofence(GeofenceModel geofenceModel) {
        if (geofenceModel == null) {
            return;
        }

        geofenceModel.setEnabled(false);
        geofenceStorage.storeGeofence(geofenceModel.getId(), geofenceModel);
    }

    @Override
    protected void onDestroy() {
        ringtone.stop();
        super.onDestroy();
    }
}
