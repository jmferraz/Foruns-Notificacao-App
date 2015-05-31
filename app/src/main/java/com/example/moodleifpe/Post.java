package com.example.moodleifpe;

import java.util.Date;

/**
 * Created by vanessagomes on 5/28/15.
 */
public class Post {
    private String forumLink;
    private String authorName;
    private String message;
    private Date date;

    public Post(String forumLink, String authorName, String message, Date date) {
        this.forumLink = forumLink;
        this.authorName = authorName;
        this.message = message;
        this.date = date;
    }

    public String getForumLink() {
        return forumLink;
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
}
