package com.example.moodleifpe;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit.client.Response;

/**
 * Created by mateus on 01/06/15.
 */
public class Utils {
    public static final String ENDPOINT_LINK = "http://dead2.ifpe.edu.br/moodle";

    public static String responseToString(Response result) {
        BufferedReader reader;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(result.getBody().in()));
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static Calendar parseDate(String elementText) {
        Calendar calendar = null;
        try {
            String fullDateString = elementText.split(" - ")[1];
            String[] dateArray = fullDateString.split(",");
            String dateString = (dateArray[1] + dateArray[2]).trim();
            DateFormat format = new SimpleDateFormat("dd MMMM yyyy hh:mm", new Locale("pt", "BR"));
            Date date = format.parse(dateString);
            calendar = Calendar.getInstance();
            calendar.setTime(date);
        } catch (Exception e) {
            Log.e("Utils", e.getMessage());
        } finally {
            return calendar;
        }
    }

    public static void enableCookies() {
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);
    }
}
