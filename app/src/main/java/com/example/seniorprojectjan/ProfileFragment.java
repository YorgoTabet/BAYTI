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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;

    TextView uidtxt;
    TextView usernametxt;
    TextView emailtxt;
    UserInfo currentUser;
    ImageView userPic;
    Button signOutBtn;
    RelativeLayout picPreview;
    ListView yourPostsList;
    //Get the preview Image
    Uri previewImageUri;

    // Database info
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference RootReff = database.getReference();
    DatabaseReference postsList = RootReff.child("Posts");
    //Array list of all the user Result items
    ArrayList<CustomList> yourPosts = new ArrayList<>();

    //getting the instance of the Image storage  and referrencing the root node
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    //UI elements
    BottomNavigationView navBar;
    ProgressBar progressBar;


    @Nullable

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {



            getActivity().findViewById(R.id.nav_profile).setEnabled(false);



        // Profile settings
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();





        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //initializing UI elements
        navBar = getActivity().findViewById(R.id.Bottom_Nav);
        progressBar=getActivity().findViewById(R.id.loading);

        picPreview = getView().findViewById(R.id.picPreviewCont);
        userPic = getView().findViewById(R.id.userPic);
        uidtxt = getView().findViewById(R.id.uidTxtView);
        usernametxt = getView().findViewById(R.id.usernametxtView);
        emailtxt = getView().findViewById(R.id.emailTxtView);
        signOutBtn = getView().findViewById(R.id.signOutButton);
        yourPostsList = getView().findViewById(R.id.yourPostsList);




        //Loading
        progressBar = getActivity().findViewById(R.id.loading);

        if(progressBar!=null)
        progressBar.setVisibility(View.VISIBLE);

        yourPostsList.setVisibility(View.GONE);

        postsList.orderByChild("PostUploaderID").equalTo(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot.getChildrenCount());



                if(!yourPosts.isEmpty()){

                    yourPosts.clear();

                }


                for (final DataSnapshot searchResultproperty : dataSnapshot.getChildren()) {


                    //getting the preview image from data storage
                    final String parentNode = searchResultproperty.getKey();

                    storageRef.child(parentNode).child("1").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            previewImageUri= uri;
                            Log.i("info", previewImageUri + searchResultproperty.child("PostDescription").getValue().toString() + searchResultproperty.child("PostTitle").getValue().toString() + " ID is: " + parentNode);
                            if(getActivity()!=null && !getActivity().isDestroyed() ) {
                                yourPosts.add(new CustomList(previewImageUri.toString(), searchResultproperty.child("PostTitle").getValue().toString(), searchResultproperty.child("PostDescription").getValue().toString(), false, parentNode));

                                CustomArrayAdapter arrayAdapter = new CustomArrayAdapter(getActivity(), 0, yourPosts);
                                yourPostsList.setAdapter(arrayAdapter);
                                arrayAdapter.notifyDataSetChanged();

                                ListAdapter listadp = yourPostsList.getAdapter();
                                if (listadp != null) {
                                    int totalHeight = 0;
                                    for (int i = 0; i < listadp.getCount(); i++) {
                                        View listItem = listadp.getView(i, null, yourPostsList);
                                        listItem.measure(0, 0);
                                        totalHeight += listItem.getMeasuredHeight();
                                    }
                                    ViewGroup.LayoutParams params = yourPostsList.getLayoutParams();
                                    params.height = totalHeight + (yourPostsList.getDividerHeight() * (listadp.getCount() - 1));
                                    yourPostsList.setLayoutParams(params);
                                    yourPostsList.requestLayout();
                                }
                            }


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Failed to load images", Toast.LENGTH_SHORT).show();
                            previewImageUri=Uri.parse("https://firebasestorage.googleapis.com/v0/b/senior-774ec.appspot.com/o/Error.png?alt=media&token=105e314c-f283-4829-9c25-439930014fdc");

                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {

                        }
                    });
                    Log.i("Key", parentNode);
                    Log.i("Results",searchResultproperty.toString());
                    Log.i("info from outside", previewImageUri + searchResultproperty.child("PostDescription").getValue().toString() + searchResultproperty.child("PostTitle").getValue().toString() + " ID is: " + parentNode);
                    previewImageUri=null;




                }
                if (getActivity() != null) {
                    CustomArrayAdapter arrayAdapter = new CustomArrayAdapter(getActivity(), 0, yourPosts);
                    yourPostsList.setAdapter(arrayAdapter);
                    arrayAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        new Handler().postDelayed(new Runnable()
                                  {
                                      public void run()
                                      {


                                          enableBottomBar(true);

                                          if(progressBar!=null)
                                          progressBar.setVisibility(View.INVISIBLE);

                                          yourPostsList.setVisibility(View.VISIBLE);
                                      }
                                  }, 1000    //Specific time in milliseconds
        );

        Glide.with(this).load(currentUser.getPhotoUrl()).into(userPic);
        userPic.setClipToOutline(true);
        uidtxt.setText(currentUser.getUid().toString());
        usernametxt.setText(currentUser.getDisplayName().toString());
        emailtxt.setText(currentUser.getEmail().toString());

        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(getContext(),LoginPage.class);
                startActivity(intent);
                getActivity().finish();
            }
        });


        //On click listener for the LsitItem Children
        yourPostsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String PostID = yourPosts.get(position).getPostId();
                Log.i("This post ID is:",PostID );
                Intent intent = new Intent(getContext(),PropertyScreenActivity.class);
                intent.putExtra("ID",PostID);
                startActivity(intent);

            }
        });


    }

    private void enableBottomBar(boolean enable){
        for (int i = 0; i < navBar.getMenu().size(); i++) {
            navBar.getMenu().getItem(i).setEnabled(enable);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().findViewById(R.id.nav_profile).setEnabled(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().findViewById(R.id.nav_profile).setEnabled(true);
    }


}
