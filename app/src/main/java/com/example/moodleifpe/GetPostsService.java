package com.example.moodleifpe;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by vanessagomes on 6/3/15.
 */
public class GetPostsService extends IntentService {
    private static final String ENDPOINT = "http://dead2.ifpe.edu.br/moodle";
    private static final String USERNAME = "estudantevisitante";
    private static final String PASSWORD = "2Patos";

    private IFPEService service;

    public GetPostsService() {
        super("GetPostsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("com.example.moodleifpe", "GetPostsServices: onHandleIntent");
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(ENDPOINT).build();
        service = restAdapter.create(IFPEService.class);
        fetchTask();
    }

    private void fetchTask() {
        Log.i("com.example.moodleifpe", "GetPostsServices: fetchTask");

        LocalDatabaseHandler localDb = new LocalDatabaseHandler(getApplicationContext());
//        Date dateOfLastCheck = localDb.getDateOfLastCheck();

        //get courses
        List<Course> courses = getCourses();

        //get forums
        List<Forum> forums = getForums(courses);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(localDb.getDateOfLastCheck());

        Log.i("com.example.moodleifpe", "GetPostsServices: getPost will fetch from the date: " + calendar.getTime().toString());
        List<Post> posts = getPosts(forums, calendar);

        if (posts != null) {
            Log.i("com.example.moodleifpe", "GetPostsServices - fetchTask() - posts is not null. Size: " + posts.size());
            for (Post post : posts) {
                localDb.insertPost(post);
            }
        }
        //Sets current date on Local DB.
        localDb.replaceDateOfLastFetch(Calendar.getInstance().getTime());
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
                forums.addAll(HtmlExtractor.getPosts(course, coursePage));
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
                posts.addAll(HtmlExtractor.getPosts(forum, forumPage, date));
            } catch (RetrofitError e) {
                Log.e("MainActivity", "Error retrieving forum page: " + e.getMessage());
            }
        }
        return posts;
    }

}
