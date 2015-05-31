package com.example.moodleifpe;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
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
        authenticateToEndpoint();
    }

    private void authenticateToEndpoint() {
        service.auth(USERNAME, PASSWORD, new Callback<Response>() {
            @Override
            public void success(Response result, Response response) {
                String resultString = convertToString(result);
                browseIndexPage(resultString);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                textView.setText("Error: " + retrofitError.getKind());
            }
        });
    }

    private void browseIndexPage(String resultString) {
        Document document = Jsoup.parse(resultString);
        Elements elements = document.getElementsByClass("type_course");
        List<Course> courses = new ArrayList<Course>();
        for (Element courseElement : elements) {
            Elements links = courseElement.getElementsByTag("a");
            if (links.isEmpty()) {
                continue;
            }
            String title = links.first().attr("title");
            String link = links.first().attr("href");
            String id = link.split("\\?id=")[1];

            textView.append(title + " - " + id + "\n");

            service.getCourse(id, new Callback<Response>() {
                @Override
                public void success(Response result, Response response) {
                    browseCoursePage(result);
                }

                @Override
                public void failure(RetrofitError error) {
                    textView.append("Failure: " + error.getMessage() + "\n");
                }
            });

            Course course = new Course();
            course.setTitle(title);
            course.setLink(link);
            courses.add(course);

        }
    }

    private void browseCoursePage(Response result) {
        //TODO move to an appropriate place
        progressBar.setVisibility(View.INVISIBLE);

        String resultString = convertToString(result);
        Document document = Jsoup.parse(resultString);
        Elements elements = document.getElementsByClass("forum");
        for (Element forum : elements) {
            Element forumTitle = forum.getElementsByClass("instancename").first();
            String link = forum.getElementsByTag("a").first().attr("href");
            if (forumTitle != null) {
                textView.append(forumTitle.ownText() + "\n");
                textView.append(link + "\n\n");
            }
        }
    }

    private String convertToString(Response result) {
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
}
