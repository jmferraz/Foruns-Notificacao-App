package com.example.moodleifpe;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by vanessagomes on 6/3/15.
 */
public class GetPostsService extends IntentService {
    private static final String ENDPOINT = Utils.ENDPOINT_LINK;

    private IFPEService service;
    private LocalDatabaseHandler localDb;

    public GetPostsService() {
        super("GetPostsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("com.example.moodleifpe", "GetPostsServices: onHandleIntent");
        localDb = new LocalDatabaseHandler(getApplicationContext());
        if (localDb.getUsername() == null) {
            return;
        }
        FetchPosts fetchPosts= new FetchPosts(this);
        Integer amountOfPosts = fetchPosts.fetchPostsTask();

        if (amountOfPosts > 0) {
            sendNotification(amountOfPosts);
        }

    }

    private void sendNotification(Integer amountOfPosts) {
        String message = getResources().getQuantityString(R.plurals.new_post_notifications, amountOfPosts, amountOfPosts);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.icone_header)
                        .setContentTitle(getText(R.string.app_name))
                        .setContentText(message)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                                R.drawable.icone))
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setTicker(message)
                        .setAutoCancel(true);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                resultIntent, 0);


        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= 16) {
            mBuilder.setPriority(Notification.PRIORITY_HIGH);
        }
        mBuilder.setContentIntent(contentIntent);

        // mId allows you to update the notification later on.
        mNotificationManager.notify(getRandomInt(), mBuilder.build());
    }

    public int getRandomInt() {
        int randomNumber = new Random().nextInt(Integer.MAX_VALUE) + 1;
        return randomNumber;
    }
//    /**
//     * Fetching new Posts.
//     *
//     * @return The amount of Posts retrieved.
//     */
//    public Integer fetchTask() {
//        Integer result = 0;
//
//        //get courses
//        List<Course> courses = getCourses();
//
//        //get forums
//        List<Forum> forums = getForums(courses);
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(localDb.getDateOfLastCheck());
//
//        Log.i("com.example.moodleifpe", "GetPostsServices: getPost will fetch from the date: " + calendar.getTime().toString());
//        List<Post> posts = getPosts(forums, calendar);
//
//        if (posts != null) {
//            result = posts.size();
//            for (Post post : posts) {
//                localDb.insertPost(post);
//            }
//            //Sets current date on Local DB.
//            localDb.replaceDateOfLastFetch(Calendar.getInstance().getTime());
//        }
//        return result;
//    }
//
//    private List<Course> getCourses() {
//        List<Course> courses = new ArrayList<Course>();
//        try {
//            LocalDatabaseHandler localDatabaseHandler = new LocalDatabaseHandler(this);
//            String username = localDatabaseHandler.getUsername();
//            String password = localDatabaseHandler.getPassword();
//            if (username != null && password != null) {
//                final Response response = service.auth(username, password);
//                String indexPage = Utils.responseToString(response);
//                courses.addAll(HtmlExtractor.getCourses(indexPage));
//            }
//        } catch (RetrofitError e) {
//            Log.e("MainActivity", "Error retrieving index page: " + e.getMessage());
//        }
//        return courses;
//    }
//
//    private List<Forum> getForums(List<Course> courses) {
//        List<Forum> forums = new ArrayList<Forum>();
//        for (Course course : courses) {
//            String id = course.getLink().split("\\?id=")[1];
//            try {
//                Response response = service.getCourse(id);
//                String coursePage = Utils.responseToString(response);
//                forums.addAll(HtmlExtractor.getForums(course, coursePage));
//            } catch (RetrofitError e) {
//                Log.e("MainActivity", "Error retrieving course page: " + e.getMessage());
//            }
//        }
//        return forums;
//    }
//
//    private List<Post> getPosts(List<Forum> forums, Calendar date) {
//        List<Post> posts = new ArrayList<Post>();
//        for (Forum forum : forums) {
//            String id = forum.getLink().split("\\?id=")[1];
//            try {
//                Response response = service.getForum(id);
//                String forumPage = Utils.responseToString(response);
//                posts.addAll(HtmlExtractor.getPosts(forum, forumPage, date));
//            } catch (RetrofitError e) {
//                Log.e("MainActivity", "Error retrieving forum page: " + e.getMessage());
//            }
//        }
//        return posts;
//    }
//

}
