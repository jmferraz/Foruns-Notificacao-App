package com.example.moodleifpe;

import java.util.Date;

/**
 * Created by vanessagomes on 5/28/15.
 */
public class Post {
    private String courseTitle;
    private String authorName;
    private String message;
    private Date date;
    private String forumTitle;

    public Post(String courseTitle, String authorName, String message, Date date, String forumTitle) {
        this.courseTitle = courseTitle;
        this.authorName = authorName;
        this.message = message;
        this.date = date;
        this.forumTitle = forumTitle;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getMessage() {
        return message;
    }

    public Date getDate() {
        return date;
    }

    public String getForumTitle() {
        return forumTitle;
    }
}
