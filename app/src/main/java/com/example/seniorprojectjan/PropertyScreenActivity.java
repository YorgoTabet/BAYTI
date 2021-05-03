package com.example.seniorprojectjan;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.util.List;

public class PropertyScreenActivity extends AppCompatActivity {

    private TextView mTextView;

    int counter = 1;
    int numberofImages;
    Uri ImageUri;

    //Initializing UI items
    EditText TitleTxtView;
    EditText DescriptionEditTxt;
    EditText ContactEditTxt;
    TextView LocationTextView;
    Button editBtn;
    Button deleteBtn;
    Button cancelBtn;

    //Initializing the items
    String pTitle ;
    String pDescription;
    String pContactInfo ;
    String pRegion ;
    String pUploaderID ;
    LinearLayout imageContainer;
    boolean isloading = true;
    ValueEventListener ValueListener;




    private FirebaseAuth mAuth;
    String currentUserId;
    UserInfo currentUser;







    //getting the instance of the Image storage  and referrencing the root node
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    StorageReference postImages ;
    DatabaseReference postsList;




    //Initializing the view items to be edited
    final List<Bitmap> bitmaps = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_propert_screen);

        final Button messageBtn = findViewById(R.id.MessageButton);

        // Profile settings
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
         currentUserId = mAuth.getCurrentUser().getUid();
        Log.i("Current user ID", currentUserId);






        postImages = storageRef.child(getIntent().getStringExtra("ID"));

        //Getting the PostID
        Intent intent = getIntent();
        final String postID = intent.getStringExtra("ID");

        // Initializing the DB
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference RootReff = database.getReference();
         postsList = RootReff.child("Posts");
        final DatabaseReference currentPost = postsList.child(postID);





        if (currentPost != null) {

            //Getting the UI refrences and inserting the Data Into them
            TitleTxtView = PropertyScreenActivity.this.findViewById(R.id.TitleTxtView);
            DescriptionEditTxt = PropertyScreenActivity.this.findViewById(R.id.DescriptionEditTxt);
            ContactEditTxt= PropertyScreenActivity.this.findViewById(R.id.ContactEditTxt);
            LocationTextView = PropertyScreenActivity.this.findViewById(R.id.LocationTextView);
            editBtn = PropertyScreenActivity.this.findViewById(R.id.editBtn);
            deleteBtn = PropertyScreenActivity.this.findViewById(R.id.deleteBtn);
            cancelBtn = PropertyScreenActivity.this.findViewById(R.id.cancelBtn);

            //Getting the Post Info



            ValueListener = currentPost.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                     pTitle = snapshot.child("PostTitle").getValue().toString();
                     pDescription =  snapshot.child("PostDescription").getValue().toString();
                     pContactInfo =  snapshot.child("PostContactInfo").getValue().toString();
                     pRegion =  snapshot.child("PostRegion").getValue().toString();
                     pUploaderID =  snapshot.child("PostUploaderID").getValue().toString();
                     Log.i("UploaderID", pUploaderID);
                     if(pUploaderID.equals(currentUserId)){

                         messageBtn.setEnabled(false);
                     }

                     //Populating the form
                    TitleTxtView.setText(pTitle);
                    DescriptionEditTxt.setText(pDescription);
                    ContactEditTxt.setText(pContactInfo);
                    LocationTextView.setText(pRegion);
                    // Checking if the post belongs to this user to show Edit and delete buttons
                    if(currentUserId.equals(pUploaderID)){

                        Log.i("This post is: ","YOURS");

                        deleteBtn.setVisibility(View.VISIBLE);
                        editBtn.setVisibility(View.VISIBLE);

                    }else{

                        Log.i("This post is: ","NOT YOURS");

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            messageBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PropertyScreenActivity.this,messagingActivity.class);
                    intent.putExtra("postID",postID);
                    intent.putExtra("pUploaderID",pUploaderID);

                    startActivity(intent);


                }
            });


            //Getting the Post Images
             getImages(Integer.valueOf(postID));





        } else {
            //Show Alert In case of error
            AlertDialog alertDialog = new AlertDialog.Builder(PropertyScreenActivity.this).create();
            alertDialog.setTitle("Error");
            alertDialog.setMessage("Something went wrong....");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent(PropertyScreenActivity.this, SearchFragment.class);
                            startActivity(intent);
                            finish();
                        }
                    });
            alertDialog.show();


        }

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(editBtn.getText().toString().toUpperCase().equals("EDIT")){

                    Toast.makeText(PropertyScreenActivity.this, "Edit mode: ON", Toast.LENGTH_SHORT).show();

                    cancelBtn.setVisibility(View.VISIBLE);
                    ContactEditTxt.setEnabled(true);
                    TitleTxtView.setEnabled(true);
                    currentPost.removeEventListener(ValueListener);
                    Log.i("You clicked the:","Edit button");
                    editBtn.setText("SAVE");
                    DescriptionEditTxt.setEnabled(true);
                    ContactEditTxt.setEnabled(true);
                    TitleTxtView.setEnabled(true);

                    DescriptionEditTxt.requestFocus();
                    ContactEditTxt.requestFocus();
                    TitleTxtView.requestFocus();





                }
                else if (editBtn.getText().toString().toUpperCase().equals("SAVE") && TitleTxtView.getText().length()!=0 && DescriptionEditTxt.getText().length()!=0 && ContactEditTxt.getText().length()!=0) {
                    Log.i("You clicked the:","Save button");

                    cancelBtn.setVisibility(View.INVISIBLE);
                    postsList.child(postID).child("PostDescription").setValue(DescriptionEditTxt.getText().toString());
                    postsList.child(postID).child("PostContactInfo").setValue(ContactEditTxt.getText().toString());
                    postsList.child(postID).child("PostTitle").setValue(TitleTxtView.getText().toString());
                    editBtn.setText("EDIT");
                    Toast.makeText(PropertyScreenActivity.this, "SAVED", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(PropertyScreenActivity.this,PropertyScreenActivity.class);
                    intent.putExtra("ID",postID);
                    startActivity(intent);
                    finish();

                }
                else{

                    Toast.makeText(PropertyScreenActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                currentPost.removeEventListener(ValueListener);
                                postsList.child(postID).removeValue();
                                RootReff.child("Latlngs").child(postID).removeValue();
                                for(int i=1;i<=numberofImages;i++)
                                {
                                    storageRef.child(postID).child(String.valueOf(i)).delete();
                                }
                                Toast.makeText(PropertyScreenActivity.this, "Post Deleted", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(PropertyScreenActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                dialog.dismiss();
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(PropertyScreenActivity.this);
                builder.setMessage("Are you sure you want to delete this post?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PropertyScreenActivity.this,PropertyScreenActivity.class);
                intent.putExtra("ID",postID);
                startActivity(intent);
                finish();
            }
        });



    }









    public void getImages(final int PostID){

        postsList.child(String.valueOf(PostID)).child("numberOfImages").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    numberofImages = Integer.parseInt(task.getResult().getValue().toString()) ;
                    Log.i("Number of Images",String.valueOf(numberofImages) );
                    for(int i=1; i <= numberofImages; i++){


                        postImages.child(String.valueOf(i)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                if(!PropertyScreenActivity.this.isDestroyed()){


                                    ImageUri=uri;
                                    Log.i("Uri",ImageUri.toString());
                                    ImageView image = new ImageView(PropertyScreenActivity.this);
                                    Glide.with(PropertyScreenActivity.this).load(ImageUri).dontAnimate().into(image);
                                    imageContainer = PropertyScreenActivity.this.findViewById(R.id.ImageContainer);

                                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(400, 420) ;
                                    image.setLayoutParams(lp);
                                    image.setScaleType(ImageView.ScaleType.CENTER_CROP);

                                    // setting the image
                                    image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                    imageContainer.addView(image);

                                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) image.getLayoutParams();
                                    layoutParams.setMargins(0, 10, 10, 10);

                                }




                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(PropertyScreenActivity.this, "Failed to Load images", Toast.LENGTH_SHORT).show();
                            }
                        });



                    }

                }else{
                    Toast.makeText(PropertyScreenActivity.this, "Error...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        new Handler().postDelayed(new Runnable()
                                  {
                                      public void run()
                                      {

                                          isloading = false;

                                      }
                                  }, 3000    //Specific time in milliseconds
        );
        isloading = false;

    }




}