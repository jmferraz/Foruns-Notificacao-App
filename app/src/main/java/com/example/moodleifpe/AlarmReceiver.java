package com.example.moodleifpe;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static android.support.v4.content.WakefulBroadcastReceiver.startWakefulService;

/**
 * Created by vanessagomes on 6/3/15.
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("com.example.moodleifpe", "----> onReceive AlarmReceiver");
        Toast.makeText(context, "I'm running.", Toast.LENGTH_LONG).show();

        ComponentName comp = new ComponentName(context.getPackageName(),
                GetPostsService.class.getName());

        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }


}