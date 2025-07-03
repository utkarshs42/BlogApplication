package com.utkarsh.blog.models;

import java.util.Date;

public class Comment {
    private int id;
    private String name;
    private String email;
    private String comment;
    private int postId;
    private Date createdAt;
    private Date updatedAt;
}
