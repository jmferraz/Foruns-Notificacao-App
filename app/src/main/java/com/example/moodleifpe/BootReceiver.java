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
            startAlarm(context);
            Log.i("com.example.moodleifpe", "----> onReceive BootReceiver - Alarm");
        }
    }

    public void startAlarm(Context context) {
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
        AlarmManager alertManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        int interval = 1000 * 60 * 60 * 8;
        alertManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);

    }
}
