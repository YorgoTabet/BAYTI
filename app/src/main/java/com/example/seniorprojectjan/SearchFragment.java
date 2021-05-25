package com.example.seniorprojectjan;


import android.app.AlertDialog;
import android.app.LauncherActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class SearchFragment extends Fragment{

    BottomNavigationView navBar;
    ProgressBar progressBar;

    ListView listView ;
    Button SearchButton;
    CustomArrayAdapter arrayAdapter;
   Button LocationButton;
    String[] Locations = {"Beirut", "South Lebanon","Beqaa","Aakar","Mount Lebanon","Nabatieh","North Lebanon"};

    // Initializing the DB
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference RootReff = database.getReference();
    DatabaseReference postsList = RootReff.child("Posts");



    ArrayList<CustomList> SearchResults = new ArrayList<>();

    //Array list of all the Search Result items
    ArrayList<CustomList> searchResultItems = new ArrayList<>();

    //getting the instance of the Image storage  and referrencing the root node
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    //Get the preview Image
    Uri previewImageUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {






        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        navBar = getActivity().findViewById(R.id.Bottom_Nav);


        //Initializing buttons
        LocationButton = (Button) getActivity().findViewById(R.id.regionSelectButton);
        SearchButton = (Button) getActivity().findViewById(R.id.SearchButton);
        SearchButton.setClipToOutline(true);



        //On click listener for the LsitItem Children
        listView = (ListView) view.findViewById(R.id.search_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String PostID = searchResultItems.get(position).getPostId();
                Log.i("This post ID is:",PostID );
                Intent intent = new Intent(getContext(),PropertyScreenActivity.class);
                intent.putExtra("ID",PostID);
                startActivity(intent);

            }
        });



        //code for location button to load the children
        LocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Pick a region");
                builder.setItems(Locations, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LocationButton.setText(Locations[which]);
                    }
                });
                builder.show();
            }
        });


        // Setting the OnClick for the search Button
        SearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //Loading
                progressBar = getActivity().findViewById(R.id.loading);

               if(progressBar!=null)
                progressBar.setVisibility(View.VISIBLE);

                listView.setVisibility(View.INVISIBLE);
                SearchButton.setEnabled(false);

                   //retrieving elements from the search and populating the list
                    loadTheList();


                previewImageUri=null;
            }
        });








    }
    private void enableBottomBar(boolean enable){
        for (int i = 0; i < navBar.getMenu().size(); i++) {
            navBar.getMenu().getItem(i).setEnabled(enable);
        }
    }

    private void loadTheList(){


        postsList.orderByChild("PostRegion").equalTo(LocationButton.getText().toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot.getChildrenCount());



                if(!searchResultItems.isEmpty()){

                    searchResultItems.clear();

                }


                for (final DataSnapshot searchResultproperty : dataSnapshot.getChildren()) {


                    //getting the preview image from data storage
                    final String parentNode = searchResultproperty.getKey();

                    storageRef.child(parentNode).child("1").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                           if(getActivity()!=null && !getActivity().isDestroyed() ){


                               previewImageUri= uri;
                               Log.i("info", previewImageUri + searchResultproperty.child("PostDescription").getValue().toString() + searchResultproperty.child("PostTitle").getValue().toString() + " ID is: " + parentNode);
                               searchResultItems.add(new CustomList(previewImageUri.toString(), searchResultproperty.child("PostTitle").getValue().toString(), searchResultproperty.child("PostDescription").getValue().toString(), parentNode));

                               CustomArrayAdapter arrayAdapter = new CustomArrayAdapter(getActivity(), 0, searchResultItems);
                               listView.setAdapter(arrayAdapter);
                               arrayAdapter.notifyDataSetChanged();


                           }


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Failed to load images", Toast.LENGTH_SHORT).show();
                            previewImageUri=Uri.parse("https://firebasestorage.googleapis.com/v0/b/senior-774ec.appspot.com/o/Error.png?alt=media&token=105e314c-f283-4829-9c25-439930014fdc");

                            //Remove the Loading and enable elements again
                            SearchButton.setEnabled(true);
                            enableBottomBar(true);
                            progressBar.setVisibility(View.INVISIBLE);
                            listView.setVisibility(View.VISIBLE);
                            previewImageUri=null;

                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {




                        }
                    });
                }



                if (getActivity() != null) {
                    CustomArrayAdapter arrayAdapter = new CustomArrayAdapter(getActivity(), 0, searchResultItems);
                    listView.setAdapter(arrayAdapter);
                    arrayAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                //Remove the Loading and enable elements again
                SearchButton.setEnabled(true);
                enableBottomBar(true);
                progressBar.setVisibility(View.INVISIBLE);
                listView.setVisibility(View.VISIBLE);
                previewImageUri=null;

            }
        });
        new Handler().postDelayed(new Runnable()
                                  {
                                      public void run()
                                      {

                                          SearchButton.setEnabled(true);
                                          enableBottomBar(true);

                                          if(progressBar!=null)
                                          progressBar.setVisibility(View.INVISIBLE);


                                          listView.setVisibility(View.VISIBLE);
                                      }
                                  }, 2000    //Specific time in milliseconds
        );


    }



}
