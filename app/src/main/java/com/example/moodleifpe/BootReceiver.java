package com.example.moodleifpe;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by vanessagomes on 6/7/15.
 */
public class BootReceiver extends BroadcastReceiver {
    private AlarmManager alertManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            ForumsAlarm.startAlarm(context);
            Log.i("com.example.moodleifpe", "----> onReceive BootReceiver - Alarm");
        }
    }
}
