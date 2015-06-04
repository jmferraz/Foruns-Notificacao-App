package com.example.moodleifpe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by vanessagomes on 6/4/15.
 */
public class PostAdapter extends ArrayAdapter<Post> {
    public PostAdapter(Context context, List<Post> posts) {
        super(context, 0, posts);
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
        TextView authorName = (TextView) convertView.findViewById(R.id.post_author_name);
        TextView message = (TextView) convertView.findViewById(R.id.post_message);

        // Populate the data into the template view using the data object
        authorName.setText(post.getAuthorName());
        message.setText(post.getMessage().trim());

        // Return the completed view to render on screen
        return convertView;
    }
}