package com.example.moodleifpe;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by mateus on 01/06/15.
 */
public class HtmlExtractor {
    public static List<Course> getCourses(String indexPage) {
        Document document = Jsoup.parse(indexPage);
        Elements elements = document.getElementsByClass("type_course");
        List<Course> courses = new ArrayList<Course>();
        for (Element courseElement : elements) {
            Elements links = courseElement.getElementsByTag("a");
            if (links.isEmpty()) {
                continue;
            }
            String title = links.first().attr("title");
            String link = links.first().attr("href");

            Course course = new Course();
            course.setTitle(title);
            course.setLink(link);
            courses.add(course);
        }
        return courses;
    }

    public static List<Forum> getForums(Course course, String coursePage) {
        Document document = Jsoup.parse(coursePage);
        Elements elements = document.getElementsByClass("forum");
        List<Forum> forums = new ArrayList<Forum>();
        for (Element forumElement : elements) {
            Element forumTitle = forumElement.getElementsByClass("instancename").first();
            String link = forumElement.getElementsByTag("a").first().attr("href");
            if (forumTitle != null) {
                Forum forum = new Forum(course.getTitle(), forumTitle.ownText(), link);
                forums.add(forum);
            }
        }
        return forums;
    }

    public static List<Post> getPosts(Forum forum, String forumPage, Calendar date) {
        Document document = Jsoup.parse(forumPage);
        Elements elements = document.getElementsByClass("forumpost");
        List<Post> posts = new ArrayList<Post>();
        for (Element postElement : elements) {
            //get date
            Element authorElement = postElement.getElementsByClass("author").first();
            Calendar postDate = Utils.parseDate(authorElement.ownText());
            if (postDate != null) {
                if (postDate.after(date)) {
                    String authorName = "", message = "";
                    //get author
                    Element nameElement = authorElement.getElementsByTag("a").first();
                    if (nameElement != null) {
                        authorName = nameElement.ownText();
                    }
                    //get message
                    Element messageElement = postElement.getElementsByClass("fullpost").first();
                    if (messageElement != null) {
                        message = messageElement.text();
                    }

                    Post post = new Post(forum.getCourseTitle(), authorName, message, postDate.getTime(), forum.getTitle());
                    posts.add(post);
                }
            }
        }
        return posts;
    }
}
