package com.punchlag.wigt.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.punchlag.wigt.model.GeofenceModel;

public class AlarmReceiver extends BroadcastReceiver {

    public static final String KEY_EXTRA_DATA = "data";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent alarmIntent = new Intent(context, AlarmActivity.class);
        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        alarmIntent.putExtra(KEY_EXTRA_DATA, intent.getParcelableExtra(KEY_EXTRA_DATA));
        context.startActivity(alarmIntent);
    }

    public static void wakeUp(Context context, GeofenceModel data) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setPackage(context.getPackageName());
        intent.putExtra(KEY_EXTRA_DATA, data);
        context.sendBroadcast(intent);
    }
}
