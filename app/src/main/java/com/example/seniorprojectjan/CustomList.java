package com.example.seniorprojectjan;

public class CustomList {


    private String PostTitle;
    private String PostDescription;
    private String postUrl;

    public Boolean getFeatured() {
        return isFeatured;
    }

    private final Boolean isFeatured;

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    private String postId;

    public CustomList(String imgId, String pTitle, String pDesc, Boolean isFeatured, String postId)
    {

        postUrl = imgId;
        PostTitle = pTitle;
        PostDescription = pDesc;
        this.isFeatured = isFeatured;

        this.postId = postId;
    }

    public CustomList(Boolean isFeatured) {

        this.isFeatured = isFeatured;
    }

    public String getPropImgResId()
    {


        return postUrl;

    }

    public String getPropTitle()
    {


        return PostTitle;

    }
    public String getPropDescription()
    {


        return PostDescription;

    }

}
