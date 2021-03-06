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

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private ProgressBar progressBar;

    private AsyncTask asyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("com.example.moodleifpe", "---------> onCreate");
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        textView = (TextView) findViewById(R.id.text);
        textView.setMovementMethod(new ScrollingMovementMethod());

        boolean firstTimeOfUse = configureAlarmSettings();

        if (firstTimeOfUse) {
            asyncTask = new FetchPostsFirstTimeTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        Utils.enableCookies();
        asyncTask = new GetPostsTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * This method will configure the Alarm Settings. It will return true if it is the first time the Alarm is being set.
     *
     * @return
     */
    private boolean configureAlarmSettings() {
        //Setting initial date
        boolean inserted = setInitialDate();
        if (inserted) {
            startAlarm();
        }
        return inserted;
    }

    /**
     * By default, initial date is current date at time 00:00:00.
     */
    private boolean setInitialDate() {
        boolean inserted = false;
        LocalDatabaseHandler localDb = new LocalDatabaseHandler(getApplicationContext());
        Date date = localDb.getDateOfLastCheck();
        if (date == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            localDb.replaceDateOfLastFetch(calendar.getTime());
            inserted = true;
        }
        return inserted;
    }

    public void startAlarm() {
        AlarmManager alertManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        int interval = 1000 * 60 * 60 * 6;//* 60 * 8;
        alertManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, alarmIntent);
        Log.i("com.example.moodleifpe", "--->Alarm Set");
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
        if (id == R.id.action_logout) {
            asyncTask = new LogoutTask().execute();
            return true;
        }
        return false;
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
            return posts;
        }

        @Override
        protected void onPostExecute(List<Post> posts) {
            progressBar.setVisibility(View.INVISIBLE);
            if (posts.isEmpty()) {
                textView.setVisibility(View.VISIBLE);
                textView.setText(R.string.no_new_posts);
            } else {
                posts = sortPosts(posts);
                textView.setVisibility(View.INVISIBLE);
                // Attach the adapter to a ListView
                ListView listView = (ListView) findViewById(R.id.list_posts);
                listView.setAdapter(new PostAdapter(getApplicationContext(), posts));
            }
        }

        private List<Post> sortPosts(List<Post> list) {
            Collections.sort(list, new Comparator<Post>() {
                public int compare(Post post1, Post post2) {
                    int compareDate = post1.getDate().compareTo(post2.getDate());
                    return compareDate * (-1);
                }
            });
            return list;
        }

        @Override
        protected void onCancelled() {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private class LogoutTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            LocalDatabaseHandler localDb = new LocalDatabaseHandler(getApplicationContext());
            localDb.deleteUser();
            localDb.deletePosts();
            localDb.deleteDateOfLastFetch();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressBar.setVisibility(View.INVISIBLE);
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        @Override
        protected void onCancelled() {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private class FetchPostsFirstTimeTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            FetchPosts fetchPosts = new FetchPosts(getApplicationContext());
            fetchPosts.fetchPostsTask();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onCancelled() {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}