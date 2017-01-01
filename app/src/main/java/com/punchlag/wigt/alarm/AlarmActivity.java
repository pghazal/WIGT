package com.punchlag.wigt.alarm;

import android.content.DialogInterface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

public class AlarmActivity extends AppCompatActivity {

    private Ringtone ringtone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtone = RingtoneManager.getRingtone(this, uri);
        ringtone.play();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Geofence reached");
        builder.setCancelable(false);
        builder.setPositiveButton("Great",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ringtone.stop();

                        dialog.dismiss();
                        finish();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        ringtone.stop();
        super.onDestroy();
    }
}
