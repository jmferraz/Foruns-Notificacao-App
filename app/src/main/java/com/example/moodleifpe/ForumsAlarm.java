package com.example.moodleifpe;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * Created by vanessagomes on 6/8/15.
 */
public class ForumsAlarm {
    /**
     * ALARM_INTERVAL is 8 hours in milliseconds.
     */
    public static final Integer ALARM_INTERVAL = 1000 * 60 * 60 * 6;//60 * 8;


    public static void startAlarm(Context context) {
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
        AlarmManager alertManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        int interval = ALARM_INTERVAL;
        alertManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);

    }
}
