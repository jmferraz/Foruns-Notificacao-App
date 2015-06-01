package com.example.moodleifpe;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private ProgressBar progressBar;

    private static final String ENDPOINT = "http://dead2.ifpe.edu.br/moodle";
    private static final String USERNAME = "estudantevisitante";
    private static final String PASSWORD = "2Patos";

    private final IFPEService service;
    private AsyncTask asyncTask;

    public MainActivity() {
        super();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(ENDPOINT).build();
        service = restAdapter.create(IFPEService.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        textView = (TextView) findViewById(R.id.text);
        textView.setMovementMethod(new ScrollingMovementMethod());

        enableCookies();
        asyncTask = new GetPostsTask().execute(getLastCheckedDate());
    }

    /**
     * Return last time server was checked for new messages.
     *
     * @return
     */
    private Calendar getLastCheckedDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2015, Calendar.MAY, 28, 0, 0); //TODO make dynamic
        return calendar;
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

    private List<Course> getCourses() {
        List<Course> courses = new ArrayList<Course>();
        try {
            final Response response = service.auth(USERNAME, PASSWORD);
            String indexPage = Utils.responseToString(response);
            courses.addAll(HtmlExtractor.getCourses(indexPage));
        } catch (RetrofitError e) {
            Log.e("MainActivity", "Error retrieving index page: " + e.getMessage());
        }
        return courses;
    }

    private List<Forum> getForums(List<Course> courses) {
        List<Forum> forums = new ArrayList<Forum>();
        for (Course course : courses) {
            String id = course.getLink().split("\\?id=")[1];
            try {
                Response response = service.getCourse(id);
                String coursePage = Utils.responseToString(response);
                forums.addAll(HtmlExtractor.getForums(course, coursePage));
            } catch (RetrofitError e) {
                Log.e("MainActivity", "Error retrieving course page: " + e.getMessage());
            }
        }
        return forums;
    }

    private List<Post> getPosts(List<Forum> forums, Calendar date) {
        List<Post> posts = new ArrayList<Post>();
        for (Forum forum : forums) {
            String id = forum.getLink().split("\\?id=")[1];
            try {
                Response response = service.getForum(id);
                String forumPage = Utils.responseToString(response);
                posts.addAll(HtmlExtractor.getForums(forum, forumPage, date));
            } catch (RetrofitError e) {
                Log.e("MainActivity", "Error retrieving forum page: " + e.getMessage());
            }
        }
        return posts;
    }

    private class GetPostsTask extends AsyncTask<Calendar, Void, List<Post>> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Post> doInBackground(Calendar... params) {
            //get courses
            List<Course> courses = getCourses();
            if (courses.isEmpty()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(R.string.no_courses);
                    }
                });
                cancel(true);
                return null;
            }
            //get forums
            List<Forum> forums = getForums(courses);
            if (forums.isEmpty()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(R.string.no_forums);
                    }
                });
                cancel(true);
                return null;
            }
            //get posts
            Calendar date = params[0];
            List<Post> posts = getPosts(forums, date);
            if (posts.isEmpty()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(R.string.no_new_posts);
                    }
                });
                cancel(true);
                return null;
            }

            return posts;
        }

        @Override
        protected void onPostExecute(List<Post> posts) {
            progressBar.setVisibility(View.INVISIBLE);
            for (Post post : posts) {
                textView.append(post.getAuthorName() + " - " + post.getMessage() + "\n");
            }
        }

        @Override
        protected void onCancelled() {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
