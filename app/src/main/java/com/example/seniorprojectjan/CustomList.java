package com.example.seniorprojectjan;

public class CustomList {


    private String PostTitle;
    private String PostDescription;
    private String postUrl;

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    private String postId;

    public CustomList(String imgId, String pTitle, String pDesc, String postId)
    {

        postUrl = imgId;
        PostTitle = pTitle;
        PostDescription = pDesc;

        this.postId = postId;
    }

    public CustomList() {

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
