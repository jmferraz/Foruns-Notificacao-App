package com.example.moodleifpe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by vanessagomes on 6/4/15.
 */
public class PostAdapter extends ArrayAdapter<Post> {
    private Context mContext;
    public PostAdapter(Context context, List<Post> posts) {
        super(context, 0, posts);
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Get data item for this position
        Post post = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_post, parent, false);
        }
        // Lookup view for data population
        TextView authorNameAndForumTitle = (TextView) convertView.findViewById(R.id.post_author_name_and_forum_title);
        TextView message = (TextView) convertView.findViewById(R.id.post_message);
        TextView courseTitle = (TextView) convertView.findViewById(R.id.post_course_title);
        TextView dateOfMessage = (TextView) convertView.findViewById(R.id.post_date);

        // Populate the data into the template view using the data object
        courseTitle.setText(post.getCourseTitle());
        dateOfMessage.setText(parseDateToString(post.getDate()));
        authorNameAndForumTitle.setText(buildAuthorForumText(post));
        message.setText(substring(post.getMessage().trim(), 100));


        // Return the completed view to render on screen
        return convertView;
    }

    private String buildAuthorForumText(Post post) {
        return  post.getAuthorName().trim() +" " + mContext.getText(R.string.posted_at) + " " + post.getForumTitle();
    }

    /**
     * Returns the substring of the String until you reach the given size.
     * If the string is smaller than the size, the whole string will be returned.
     *
     * @param message
     * @param size
     * @return
     */
    private String substring(String message, int size) {
        if (message.length() > size) {
            message = message.substring(0, size - 1);
            message = message + "...";
        }
        return message;
    }

    private String parseDateToString(Date date) {
        if (date == null) {
            return "";
        }
        Format hour = new SimpleDateFormat("HH:mm");
        Format day = new SimpleDateFormat("dd/MM/yyyy");
        return hour.format(date) + " do dia " + day.format(date);
    }
}