package com.punchlag.wigt.alarm;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class AlarmReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent alarmIntent = new Intent(context, AlarmActivity.class);
        context.startActivity(alarmIntent);
    }

    public static void wakeUp(Context context) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setPackage(context.getPackageName());
        context.sendBroadcast(intent);
    }
}
