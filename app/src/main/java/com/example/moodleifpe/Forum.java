package com.example.moodleifpe;

/**
 * Created by Vanessa Gomes on 5/28/15.
 */
public class Forum {
    private String courseTitle;
    private String title;
    private String link;

    public Forum(String courseTitle, String title, String forumLink){
        this.courseTitle = courseTitle;
        this.title = title;
        this.link = forumLink;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }
}
