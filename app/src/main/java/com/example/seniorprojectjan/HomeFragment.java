package com.example.seniorprojectjan;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class HomeFragment extends Fragment {

    ProgressBar progressBar;


    // Getting instance of the database and referencing the root node and the featured list
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference RootReff = database.getReference();
    DatabaseReference FeaturedpostsList = RootReff.child("Posts");

    //getting the instance of the Image storage  and referrencing the root node
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();



    ListView listView ;



    //Array list of all the featured items
    ArrayList<CustomList> FeaturedItemsList = new ArrayList<>();



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {






       return inflater.inflate(R.layout.fragment_home, container, false);

    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){

        // refer to the list
        listView = getView().findViewById(R.id.FeaturedList);

        progressBar = getActivity().findViewById(R.id.loading);
        progressBar.setVisibility(View.VISIBLE);
        listView.setVisibility(View.INVISIBLE);


        new Handler().postDelayed(new Runnable()
                                  {
                                      public void run()
                                      {

                                          progressBar.setVisibility(View.INVISIBLE);
                                          listView.setVisibility(View.VISIBLE);
                                      }
                                  }, 2000    //Specific time in milliseconds
        );


        // Getting the featured posts from the database and displaying them
        FeaturedpostsList.orderByChild("isFeatured").equalTo("true").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {



                if(FeaturedItemsList.isEmpty()!=true){

                    FeaturedItemsList.clear();

                }

                if (getActivity() != null) {
                    CustomArrayAdapter arrayAdapter = new CustomArrayAdapter(getActivity(), 0, FeaturedItemsList);
                    listView.setAdapter(arrayAdapter);
                }


                for (DataSnapshot featuredPropertyItem : snapshot.getChildren()) {

                    String parentNode = featuredPropertyItem.getKey();
                    Log.i("Key",parentNode.toString());

                    storageRef.child(parentNode).child("1").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                       Uri previewImageUri = uri;
                        Log.i("info", previewImageUri + featuredPropertyItem.child("PostDescription").getValue().toString() + featuredPropertyItem.child("PostTitle").getValue().toString() + " ID is: " + parentNode);
                        if (getActivity() != null && !getActivity().isDestroyed()) {
                            FeaturedItemsList.add(new CustomList(previewImageUri.toString(), featuredPropertyItem.child("PostTitle").getValue().toString(), featuredPropertyItem.child("PostDescription").getValue().toString(), true, parentNode));

                            CustomArrayAdapter arrayAdapter = new CustomArrayAdapter(getActivity(), 0, FeaturedItemsList);
                            listView.setAdapter(arrayAdapter);
                            arrayAdapter.notifyDataSetChanged();
                        }
                    }


                    });
                   
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // UI settings for the list
        listView.setClipToOutline(true);


        //Click Listener for the list
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String PostID = FeaturedItemsList.get(position).getPostId();
                Log.i("This post ID is:",PostID );
                Intent intent = new Intent(getContext(),PropertyScreenActivity.class);
                intent.putExtra("ID",PostID);
                startActivity(intent);
            }
        });









        }


}



