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

        db.execSQL(CREATE_TABLE_POST);
        db.execSQL(CREATE_TABLE_DATE);
        db.execSQL(CREATE_TABLE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
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
        c.close();
        db.close();
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
        c.close();
        db.close();
        return password;
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

    public void deleteUser() {
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows = db.delete(TABLE_USER, null, null);
        Log.i("LocalDB = deleteUser", "deleted rows: " + deletedRows);
        db.close();
    }

    public void insertPost(Post post) {
        Post existingPost = getPost(post);
        if (existingPost == null) {
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
    }

    public Post getPost(Post post) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT  " + POST_COURSE_TITLE
                + ", " + POST_AUTHOR_NAME
                + ", " + POST_MESSAGE
                + ", " + POST_DATE
                + ", " + POST_FORUM_TITLE
                + " FROM " + TABLE_POST, null);

        Post result = null;
        if (c.moveToFirst()) {
            try {
                result = new Post(c.getString(0), c.getString(1), c.getString(2), parseStringToDate(c.getString(3)), c.getString(4));
            } catch (ParseException e) {
                result = new Post(c.getString(0), c.getString(1), c.getString(2), null, c.getString(4));
            }
        }
        return result;
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

    public void deletePosts() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_POST);
        db.close();
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
