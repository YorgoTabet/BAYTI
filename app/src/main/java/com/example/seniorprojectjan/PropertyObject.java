package com.example.seniorprojectjan;

import android.graphics.Bitmap;
import android.media.Image;

import java.util.ArrayList;

public class PropertyObject {


    public PropertyObject(int propId, String propTitle, String propDesc, int propContact, ArrayList<Image> propImages, String userId) {
        PropId = propId;
        PropTitle = propTitle;
        PropDesc = propDesc;
        PropContact = propContact;
        PropImages = propImages;
        UserId = userId;
    }

    private int PropId;
         private String PropTitle;
         private String PropDesc;
         private int PropContact;
         private ArrayList<Image> PropImages = new ArrayList<Image>();
         private String UserId;


    public int getPropId() {
        return PropId;
    }

    public void setPropId(int propId) {
        PropId = propId;
    }

    public String getPropTitle() {
        return PropTitle;
    }

    public void setPropTitle(String propTitle) {
        PropTitle = propTitle;
    }

    public String getPropDesc() {
        return PropDesc;
    }

    public void setPropDesc(String propDesc) {
        PropDesc = propDesc;
    }

    public int getPropContact() {
        return PropContact;
    }

    public void setPropContact(int propContact) {
        PropContact = propContact;
    }

    public ArrayList<Image> getPropImages() {
        return PropImages;
    }

    public void setPropImages(ArrayList<Image> propImages) {
        PropImages = propImages;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }
}
