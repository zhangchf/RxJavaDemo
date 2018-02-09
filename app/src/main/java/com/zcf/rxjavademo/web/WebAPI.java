package com.zcf.rxjavademo.web;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by zhangchf on 31/03/2017.
 */

public interface WebAPI {

    public static final String BASE_URL = "https://jsonplaceholder.typicode.com/";

    @GET("posts")
    Observable<Response<Post[]>> getPosts();

    @GET("posts/{id}/comments")
    Observable<Response<Comment[]>> getComments(@Path("id") int postId);

    @GET("users")
    Observable<Response<User[]>> getUsers();

    @GET("users/{id}/posts")
    Observable<Response<Post[]>> getUserPosts(@Path("id") int userId);
}
