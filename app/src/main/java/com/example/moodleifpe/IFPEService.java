package com.example.moodleifpe;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by mateus on 15/05/15.
 */
public interface IFPEService {

    @FormUrlEncoded
    @POST("/login/index.php")
    Response auth(@Field("username") String username, @Field("password") String password);

    @GET("/course/view.php")
    Response getCourse(@Query("id") String courseId);

    @GET("/mod/forum/view.php")
    Response getForum(@Query("id") String forumId);
}
