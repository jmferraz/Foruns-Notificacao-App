package com.example.moodleifpe;

/**
 * Created by Vanessa Gomes on 5/28/15.
 */
public class Forum {
    private String courseLink;
    private String title;
    private String link;

    public Forum(String courseLink, String title, String forumLink){
        this.courseLink = courseLink;
        this.title = title;
        this.link = forumLink;
    }

    public String getCourseLink() {
        return courseLink;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }
}
