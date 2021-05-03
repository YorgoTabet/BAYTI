package com.example.seniorprojectjan;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class CustomArrayAdapter extends ArrayAdapter<CustomList> {


    public CustomArrayAdapter(@NonNull Context context, int resource, @NonNull ArrayList<CustomList> customLists) {
        super(context, resource, customLists);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        View listItemView = convertView;
         FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        if (listItemView == null) {
            listItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_list_item, parent, false);

        }
        CustomList currentItem = getItem(position);
        ImageView imageView = listItemView.findViewById(R.id.PropertyImg);
       // imageView.setImageResource(currentItem.getPropImgResId());
        Log.i("Message from ArrayAdapter","URL is "+storageRef.child(currentItem.getPostId()).child("1").getDownloadUrl());
        Glide.with(parent.getContext()).load(Uri.parse(currentItem.getPropImgResId())).dontAnimate().into(imageView);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        TextView textView = listItemView.findViewById(R.id.propertyTitle);
        textView.setText(currentItem.getPropTitle());
        TextView textView1 = listItemView.findViewById(R.id.propertyDescription);
        textView1.setText(currentItem.getPropDescription());
        parent.setTag(currentItem.getPostId());

        return listItemView;
    }
}
