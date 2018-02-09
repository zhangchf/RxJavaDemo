package com.zcf.rxjavademo.web;

import android.support.annotation.Keep;

/**
 * Created by zhangchf on 31/03/2017.
 */
@Keep
public class Comment {
    public int postId;
    public int id;
    public String name;
    public String email;
    public String body;

    @Override
    public String toString() {
        return "Comment{" +
                "postId=" + postId +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}
