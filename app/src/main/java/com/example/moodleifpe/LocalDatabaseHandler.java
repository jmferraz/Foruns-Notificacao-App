package com.example.moodleifpe;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by vanessagomes on 5/28/15.
 */
public class LocalDatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MOODLE_IFPE_DATABASE";

    public final String TABLE_COURSE = "table_course";
    private final String COURSE_TITLE = "course_title";
    private final String COURSE_LINK = "course_link";

    public final String TABLE_FORUM = "table_forum";
    private final String FORUM_COURSE_LINK = "forum_course_link";
    private final String FORUM_LINK = "forum_link";
    private final String FORUM_TITLE = "forum_title";

    public final String TABLE_POST = "table_post";
    private final String POST_FORUM_LINK = "post_forum_link";
    private final String POST_AUTHOR_NAME = "post_author_name";
    private final String POST_MESSAGE = "post_message";
    private final String POST_DATE = "post_date";


    public LocalDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_COURSE = "CREATE TABLE " + TABLE_COURSE + " ("
                + COURSE_TITLE
                + " VARCHAR" + ",  " + COURSE_LINK + " VARCHAR UNIQUE PRIMARY KEY)";

        String CREATE_TABLE_FORUM = "CREATE TABLE " + TABLE_FORUM
                + " (" + FORUM_COURSE_LINK + " VARCHAR"
                + ",  " + FORUM_LINK + " VARCHAR UNIQUE PRIMARY KEY"
                + ",  " + FORUM_TITLE + " VARCHAR)";

        String CREATE_TABLE_POST = "CREATE TABLE " + TABLE_POST + " ("
                + POST_FORUM_LINK + " VARCHAR"
                + ",  " + POST_AUTHOR_NAME + " VARCHAR"
                + ",  " + POST_MESSAGE + " VARCHAR"
                + ",  " + POST_DATE + " TEXT)";

        db.execSQL(CREATE_TABLE_COURSE);
        db.execSQL(CREATE_TABLE_FORUM);
        db.execSQL(CREATE_TABLE_POST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FORUM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POST);
        onCreate(db);
    }

    public void insertCourse(Course course) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COURSE_TITLE, course.getTitle());
        values.put(COURSE_LINK, course.getLink());

        long insertedRow = db.insert(TABLE_COURSE, null, values);
        Log.i("LocalDB = insertCourse", "inserted rows: " + insertedRow);
        db.close();
    }

    public void insertForum(Forum forum) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FORUM_COURSE_LINK, forum.getCourseLink());
        values.put(FORUM_LINK, forum.getLink());
        values.put(FORUM_TITLE, forum.getTitle());

        long insertedRow = db.insert(TABLE_FORUM, null, values);
        Log.i("LocalDB = insertForum", "inserted rows: " + insertedRow);
        db.close();
    }

    public void insertPost(Post post) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(POST_FORUM_LINK, post.getForumLink());
        values.put(POST_AUTHOR_NAME, post.getAuthorName());
        values.put(POST_MESSAGE, post.getMessage());
        values.put(POST_DATE, parseDateToString(post.getDate()));

        long insertedRow = db.insert(TABLE_POST, null, values);
        Log.i("LocalDB = insertPost", "inserted rows: " + insertedRow);
        db.close();
    }

    public void deleteCourse(String courseLink) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_COURSE, COURSE_LINK + "=" + courseLink, null);
        db.close();
    }

    public void deleteForum(String forumLink) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FORUM, FORUM_LINK + "=" + forumLink, null);
        db.close();
    }

    public void updateCourse(Course course) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COURSE_LINK, course.getLink());
        values.put(COURSE_TITLE, course.getTitle());

        long updatedRow = db.update(TABLE_COURSE, values, COURSE_LINK + "="
                + course.getLink(), null);
        Log.i("LocalDB = updateCourse", "inserted rows: " + updatedRow);
        db.close();
    }

    public void updateForum(Forum forum) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FORUM_LINK, forum.getLink());
        values.put(FORUM_TITLE, forum.getTitle());
        values.put(FORUM_COURSE_LINK, forum.getCourseLink());

        long updatedRow = db.update(TABLE_POST, values, FORUM_LINK + "="
                + forum.getLink(), null);
        Log.i("LocalDB = updateForum", "inserted rows: " + updatedRow);
        db.close();
    }

    public List<Course> listCourses() {
        List<Course> courses = new ArrayList<Course>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT  " + COURSE_LINK + ", "
                + COURSE_TITLE + " FROM " + TABLE_COURSE, null);
        if (c.moveToFirst()) {
            do {
                Course course = new Course();
                course.setLink(c.getString(0));
                course.setTitle(c.getString(1));
                courses.add(course);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return courses;
    }

    public List<Forum> listForum() {
        List<Forum> forums = new ArrayList<Forum>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT  " + FORUM_COURSE_LINK + ", "
                + FORUM_TITLE + ", "
                + FORUM_LINK + " FROM " + TABLE_FORUM, null);
        if (c.moveToFirst()) {
            do {
                Forum forum = new Forum(c.getString(0), c.getString(1), c.getString(2));
                forums.add(forum);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return forums;
    }

    /**
     * Lists all posts stored in the Database.
     *
     */
    public List<Post> listPost() {
        List<Post> posts = new ArrayList<Post>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT  " + POST_FORUM_LINK
                + ", " + POST_AUTHOR_NAME
                + ", " + POST_MESSAGE
                + ", " + POST_DATE
                + " FROM " + TABLE_POST, null);
        if (c.moveToFirst()) {
            do {
                Post post = null;
                try {
                    post = new Post(c.getString(0), c.getString(1), c.getString(2), parseStringToDate(c.getString(3)));
                } catch (ParseException e) {
                    Log.e("LocalDB - listPost", e.getMessage());
                    post = new Post(c.getString(0), c.getString(1), c.getString(2), null);
                }
                posts.add(post);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return posts;
    }

    /**
     * Lists all posts stored in the Database.
     * TODO: NEEDS TESTING
     */
    public List<Post> listPost(Date date) {
        List<Post> posts = new ArrayList<Post>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT  " + POST_FORUM_LINK
                + ", " + POST_AUTHOR_NAME
                + ", " + POST_MESSAGE
                + ", " + POST_DATE
                + " FROM " + TABLE_POST
                + " WHERE " + POST_DATE + " >= " + parseDateToString(date), null);
        if (c.moveToFirst()) {
            do {
                Post post = null;
                try {
                    post = new Post(c.getString(0), c.getString(1), c.getString(2), parseStringToDate(c.getString(3)));
                } catch (ParseException e) {
                    Log.e("LocalDB - listPost", e.getMessage());
                    post = new Post(c.getString(0), c.getString(1), c.getString(2), null);
                }
                posts.add(post);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return posts;
    }

    private String parseDateToString(Date date) {
        Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(date);
    }

    private Date parseStringToDate(String dateString) throws ParseException {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        return formatter.parse(dateString);
    }

}
