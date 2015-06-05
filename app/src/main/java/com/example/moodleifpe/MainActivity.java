package com.example.moodleifpe;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit.RestAdapter;


public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private ProgressBar progressBar;
    private ListView postsListView;

    private static final String ENDPOINT = "http://dead2.ifpe.edu.br/moodle";
    private static final String USERNAME = "estudantevisitante";
    private static final String PASSWORD = "2Patos";

    private final IFPEService service;
    private AsyncTask asyncTask;

    private PendingIntent pendingIntent;
    private AlarmManager alertManager;

    public MainActivity() {
        super();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(ENDPOINT).build();
        service = restAdapter.create(IFPEService.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("com.example.moodleifpe", "---------> onCreate");
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        textView = (TextView) findViewById(R.id.text);
        textView.setMovementMethod(new ScrollingMovementMethod());
        postsListView = (ListView) findViewById(R.id.list_posts);

        configureAlarmSettings();
        enableCookies();
        asyncTask = new GetPostsTask().execute();
    }

    private void configureAlarmSettings() {
        // Retrieve a PendingIntent that will perform a broadcast
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        //Setting initial date
        setInitialDate();
        startAlarm();
    }

    private void setInitialDate() {
        LocalDatabaseHandler localDb = new LocalDatabaseHandler(getApplicationContext());
        Date date = localDb.getDateOfLastCheck();
        if (date == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(2015, Calendar.JUNE, 01, 0, 0);
            localDb.replaceDateOfLastFetch(calendar.getTime());
        }
    }

    public void startAlarm() {
        alertManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //TODO Pesquisando a cada 2 minutos. precisa mudar para 1000*60*60*8 (8h)
        int interval = 1000 * 60 * 2;
        alertManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        Log.i("com.example.moodleifpe", "--->Alarm Set");
    }

    private void enableCookies() {
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if(id == R.id.action_refresh){
            asyncTask = new GetPostsTask().execute();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if (asyncTask != null && asyncTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
            asyncTask.cancel(true);
        }
        super.onDestroy();
    }

    private class GetPostsTask extends AsyncTask<Void, Void, List<Post>> {
        private LocalDatabaseHandler localDb;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Post> doInBackground(Void... params) {
            localDb = new LocalDatabaseHandler(getApplicationContext());

            List<Post> posts = localDb.listPost();
            //Should use listPost(date). But it is not working.
//            List<Post> posts = localDb.listPost(getLastCheckedDate().getTime());
            return posts;
        }

        @Override
        protected void onPostExecute(List<Post> posts) {
            progressBar.setVisibility(View.INVISIBLE);
            if (posts.isEmpty()) {
                textView.setText(R.string.no_new_posts);
            } else {
                //TODO ORDERNAR LISTA DE POSTS POR CURSO DEPOIS POR FÓRUM DEPOIS POR DATA DENTRO DO FORUM (DECRESCENTE)
                textView.setText(posts.size() + " " + getText(R.string.new_posts) +"\n");
                // Attach the adapter to a ListView
                ListView listView = (ListView) findViewById(R.id.list_posts);
                listView.setAdapter(new PostAdapter(getApplicationContext(), posts));
            }
        }

        @Override
        protected void onCancelled() {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}