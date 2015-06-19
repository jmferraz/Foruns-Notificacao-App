package com.example.moodleifpe;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

    public FetchPosts(Context context) {
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

        try {
            //get courses
            List<Course> courses = getCourses();

            //get forums
            List<Forum> forums = getForums(courses);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(localDb.getDateOfLastCheck());

            Log.i("com.example.moodleifpe", "FetchPosts: getPost will fetch from the date: " + calendar.getTime().toString());
            List<Post> posts = null;
            posts = getPosts(forums, calendar);
            if (posts != null) {
                savePostsToLocalDb(posts);
                result = posts.size();
            }
        } catch (InternetConnectionException e) {
            localDb.replaceDateOfLastFetch(localDb.getDateOfLastCheck());
            Log.e(this.getClass().getName(), "FetchPosts: it was not possible to retrieve " +
                    "the posts. The same date will be checked next time the alarm works.");
            Log.i(this.getClass().getName(), "FetchPosts - The date will be: "
                    + localDb.getDateOfLastCheck().toString());
        }
        return result;
    }

    private void savePostsToLocalDb(List<Post> posts) {
        if (!posts.isEmpty()) {
            Log.i(this.getClass().getName(), "savePostsToLocalDb");
            for (Post post : posts) {
                localDb.insertPost(post);
            }
            //Sets current date on Local DB.
            localDb.replaceDateOfLastFetch(Calendar.getInstance().getTime());
        }
    }

    private List<Course> getCourses() throws InternetConnectionException {
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
            Log.e(this.getClass().getName(), "getCourses - Error retrieving index page: " + e.getMessage());
            throw new InternetConnectionException(e.getMessage());
        }
        return courses;
    }

    private List<Forum> getForums(List<Course> courses) throws InternetConnectionException {
        List<Forum> forums = new ArrayList<Forum>();
        for (Course course : courses) {
            String id = course.getLink().split("\\?id=")[1];
            try {
                Response response = service.getCourse(id);
                String coursePage = Utils.responseToString(response);
                forums.addAll(HtmlExtractor.getForums(course, coursePage));
            } catch (RetrofitError e) {
                Log.e(this.getClass().getName(), "getForums - Error retrieving index page: " + e.getMessage());
                throw new InternetConnectionException(e.getMessage());
            }
        }
        return forums;
    }

    private List<Post> getPosts(List<Forum> forums, Calendar date) throws InternetConnectionException {
        List<Post> posts = new ArrayList<Post>();
        for (Forum forum : forums) {
            String id = forum.getLink().split("\\?id=")[1];
            try {
                Response response = service.getForum(id);
                String forumPage = Utils.responseToString(response);
                posts.addAll(HtmlExtractor.getPosts(forum, forumPage, date));
            } catch (RetrofitError e) {
                Log.e(this.getClass().getName(), "getPosts - Error retrieving index page: " + e.getMessage());
                throw new InternetConnectionException(e.getMessage());
            }
        }
        return posts;
    }
}
