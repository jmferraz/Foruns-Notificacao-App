package com.example.moodleifpe;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by vanessagomes on 6/8/15.
 */
public class FetchPosts {
    private static final String ENDPOINT = Utils.ENDPOINT_LINK;

    private IFPEService service;
    private LocalDatabaseHandler localDb;
    private Context context;

    public FetchPosts(Context context){
        this.context = context;
        localDb = new LocalDatabaseHandler(context);
        if (localDb.getUsername() == null) {
            return;
        }
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(ENDPOINT).build();
        service = restAdapter.create(IFPEService.class);
    }

    /**
     * Fetching new Posts.
     * This method will fetch new Posts from the getLastCheckedDate and will insert them on the Local DB.
     *
     * @return The amount of Posts retrieved.
     */
    public Integer fetchPostsTask() {
        Integer result = 0;

        //get courses
        List<Course> courses = getCourses();

        //get forums
        List<Forum> forums = getForums(courses);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(localDb.getDateOfLastCheck());

        Log.i("com.example.moodleifpe", "FetchPosts: getPost will fetch from the date: " + calendar.getTime().toString());
        List<Post> posts = getPosts(forums, calendar);

        if (posts != null) {
            savePostsToLocalDb(posts);
            result = posts.size();
        }
        return result;
    }

    private void savePostsToLocalDb(List<Post> posts){
        for (Post post : posts) {
            localDb.insertPost(post);
        }
        //Sets current date on Local DB.
        localDb.replaceDateOfLastFetch(Calendar.getInstance().getTime());
    }

    private List<Course> getCourses() {
        List<Course> courses = new ArrayList<Course>();
        try {
            LocalDatabaseHandler localDatabaseHandler = new LocalDatabaseHandler(this.context);
            String username = localDatabaseHandler.getUsername();
            String password = localDatabaseHandler.getPassword();
            if (username != null && password != null) {
                final Response response = service.auth(username, password);
                String indexPage = Utils.responseToString(response);
                courses.addAll(HtmlExtractor.getCourses(indexPage));
            }
        } catch (RetrofitError e) {
            Log.e("FetchPosts", "Error retrieving index page: " + e.getMessage());
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
                Log.e("FetchPosts", "Error retrieving course page: " + e.getMessage());
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
                Log.e("FetchPosts", "Error retrieving forum page: " + e.getMessage());
            }
        }
        return posts;
    }
}
