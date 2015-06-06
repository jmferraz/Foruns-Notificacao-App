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
    private final String FORUM_COURSE_NAME = "forum_course_name";
    private final String FORUM_LINK = "forum_link";
    private final String FORUM_TITLE = "forum_title";

    public final String TABLE_POST = "table_post";
    private final String POST_COURSE_TITLE = "post_course_title";
    private final String POST_AUTHOR_NAME = "post_author_name";
    private final String POST_MESSAGE = "post_message";
    private final String POST_DATE = "post_date";
    private final String POST_FORUM_TITLE = "post_forum_title";

    public final String TABLE_DATE = "table_date";
    public final String DATE_LAST_CHECK = "date_last_check";

    public final String TABLE_USER = "table_user";
    public final String USER_USERNAME = "user_username";
    public final String USER_PASSWORD = "user_password";

    public LocalDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_COURSE = "CREATE TABLE " + TABLE_COURSE + " ("
                + COURSE_TITLE
                + " VARCHAR" + ",  " + COURSE_LINK + " VARCHAR)";

        String CREATE_TABLE_FORUM = "CREATE TABLE " + TABLE_FORUM
                + " (" + FORUM_COURSE_NAME + " VARCHAR"
                + ",  " + FORUM_LINK + " VARCHAR"
                + ",  " + FORUM_TITLE + " VARCHAR)";

        String CREATE_TABLE_POST = "CREATE TABLE " + TABLE_POST + " ("
                + POST_COURSE_TITLE + " VARCHAR"
                + ",  " + POST_AUTHOR_NAME + " VARCHAR"
                + ",  " + POST_MESSAGE + " VARCHAR"
                + ",  " + POST_DATE + " TEXT"
                + ",  " + POST_FORUM_TITLE + " VARCHAR)";

        String CREATE_TABLE_DATE = "CREATE TABLE " + TABLE_DATE
                + " (" + DATE_LAST_CHECK + " VARCHAR)";

        String CREATE_TABLE_USER = "CREATE TABLE " + TABLE_USER + " ("
                + USER_USERNAME
                + " VARCHAR" + ",  " + USER_PASSWORD + " VARCHAR)";

        db.execSQL(CREATE_TABLE_COURSE);
        db.execSQL(CREATE_TABLE_FORUM);
        db.execSQL(CREATE_TABLE_POST);
        db.execSQL(CREATE_TABLE_DATE);
        db.execSQL(CREATE_TABLE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FORUM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    public String getUsername() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + USER_USERNAME + " FROM "
                + TABLE_USER, null);

        String username = null;

        if (c.moveToFirst()) {
            username = c.getString(0);

        }
        return username;
    }

    public String getPassword() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + USER_PASSWORD + " FROM "
                + TABLE_USER, null);

        String password = null;

        if (c.moveToFirst()) {
            password = c.getString(0);

        }
        return password;
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

    public void insertUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_USERNAME, username);
        values.put(USER_PASSWORD, password);

        long insertedRow = db.insert(TABLE_USER, null, values);
        Log.i("LocalDB = insertUser", "inserted rows: " + insertedRow);
        db.close();
    }

    public void insertForum(Forum forum) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FORUM_COURSE_NAME, forum.getCourseTitle());
        values.put(FORUM_LINK, forum.getLink());
        values.put(FORUM_TITLE, forum.getTitle());

        long insertedRow = db.insert(TABLE_FORUM, null, values);
        Log.i("LocalDB = insertForum", "inserted rows: " + insertedRow);
        db.close();
    }

    public void insertPost(Post post) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(POST_COURSE_TITLE, post.getCourseTitle());
        values.put(POST_AUTHOR_NAME, post.getAuthorName());
        values.put(POST_MESSAGE, post.getMessage());
        values.put(POST_DATE, parseDateToString(post.getDate()));
        values.put(POST_FORUM_TITLE, post.getForumTitle());

        long insertedRow = db.insert(TABLE_POST, null, values);
        Log.i("com.example.moodleifpe", "LocalDB - insertPost - inserted rows: " + insertedRow);
        db.close();
    }

    public Date getDateOfLastCheck() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + DATE_LAST_CHECK + " FROM "
                + TABLE_DATE, null);

        Date date = null;
        try {
            if (c.moveToFirst()) {
//                Log.i("LocalDB", "----->getDateOfLastCheck()- cursor.getType(0): " + c.getType(c.getColumnIndex(DATE_LAST_CHECK)));
                String dateString = c.getString(0);
                date = parseStringToDate(dateString);
            }
        } catch (ParseException e) {
            Log.e("LocalDB", "error at getDateOfLastCheck: " + e.getMessage());
        } catch (Exception e1) {
            Log.e("LocalDB", "error at getDateOfLastCheck: " + e1.getMessage());
        }
        return date;
    }

    public void replaceDateOfLastFetch(Date date) {
        deleteDateOfLastFetch();

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DATE_LAST_CHECK, parseDateToString(date));

        long insertedRow = db.insert(TABLE_DATE, null, values);
        Log.i("LocalDB", "replaceDateOfLastFetch - inserted rows: " + insertedRow);
        db.close();
    }

    public void deleteDateOfLastFetch() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_DATE);
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
        Cursor c = db.rawQuery("SELECT  " + FORUM_COURSE_NAME + ", "
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
     */
    public List<Post> listPost() {
        List<Post> posts = new ArrayList<Post>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT  " + POST_COURSE_TITLE
                + ", " + POST_AUTHOR_NAME
                + ", " + POST_MESSAGE
                + ", " + POST_DATE
                + ", " + POST_FORUM_TITLE
                + " FROM " + TABLE_POST, null);
        if (c.moveToFirst()) {
            do {
                Post post = null;
                try {
                    post = new Post(c.getString(0), c.getString(1), c.getString(2), parseStringToDate(c.getString(3)), c.getString(4));
                } catch (ParseException e) {
                    post = new Post(c.getString(0), c.getString(1), c.getString(2), null, c.getString(4));
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
        Cursor c = db.rawQuery("SELECT  " + POST_COURSE_TITLE
                + ", " + POST_AUTHOR_NAME
                + ", " + POST_MESSAGE
                + ", " + POST_DATE
                + " FROM " + TABLE_POST
                + " WHERE " + POST_DATE + " >= " + date.getTime(), null);
        if (c.moveToFirst()) {
            do {
                Post post = null;
                try {
                    post = new Post(c.getString(0), c.getString(1), c.getString(2), parseStringToDate(c.getString(3)), c.getString(4));
                } catch (ParseException e) {
                    post = new Post(c.getString(0), c.getString(1), c.getString(2), null, c.getString(4));
                }
                posts.add(post);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return posts;
    }

    private String parseDateToString(Date date) {
        if (date == null) {
            return "";
        }
        Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(date);
    }

    private Date parseStringToDate(String dateString) throws ParseException {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        return formatter.parse(dateString);
    }

}
