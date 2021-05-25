package com.example.seniorprojectjan;


import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static androidx.core.content.ContextCompat.getSystemService;

public class AddFragment extends Fragment {








    // initializing items and UI items
    Button ImageBrowser;
    Button uploadBtn;
    EditText pTitle;
    EditText pDesc;
    EditText pContact;
    RadioButton radioBtnYes;
    RadioButton RadioButtonNo;
    RadioGroup RadioGrp;
    Button LocationButton;
    String[] Locations = {"Beirut", "South Lebanon", "Beqaa", "Aakar", "Mount Lebanon", "Nabatieh", "North Lebanon"};
    final List<Bitmap> bitmaps = new ArrayList<>();

    //Location settings
    LocationManager lm;


    BottomNavigationView navBar;


    //The One time Post ID;
    int PostId;

    //Setting the Database up with the Root Posts node
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference RootReff = database.getReference();
    DatabaseReference postsList = RootReff.child("Posts");

    //Setting the File Storage Databse Instance
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    int PicCounter;

    //User Settings
    FirebaseUser user;
    String UserId;




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(AddFragment.this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


                RadioButtonNo.setChecked(true);


            }


        }


    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }





        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {


        if (requestCode == 1 && resultCode == RESULT_OK) {

            ClipData clipData = data.getClipData();

            if (clipData != null) {

                for (int i = 0; i < clipData.getItemCount(); i++) {
                    Uri imageUri = clipData.getItemAt(i).getUri();
                    try {

                        Bitmap bitmap = decodeImage(imageUri);

                        bitmaps.add(bitmap);


                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }

            } else {
                Uri imageUri = data.getData();
                try {
                    Bitmap bitmap = decodeImage(imageUri);
                    bitmaps.add(bitmap);


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }


            setthelist(bitmaps);


        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {


        navBar = getActivity().findViewById(R.id.Bottom_Nav);
        RadioButtonNo = getActivity().findViewById(R.id.RadioButtonNo);


        lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        //User Info
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            UserId = user.getUid();
        } else {
            uploadBtn.setEnabled(false);
        }


        //Initializing UI items
        ImageBrowser = getActivity().findViewById(R.id.UploadImageBtn);
        pTitle = getActivity().findViewById(R.id.TitleEditTxt);
        pDesc = getActivity().findViewById(R.id.DescriptionEditTxt);
        pContact = getActivity().findViewById(R.id.ContactEditTxt);
        radioBtnYes = getActivity().findViewById(R.id.RadioButtonYes);
        RadioGrp = getActivity().findViewById(R.id.LocationRadioGroup);

        //Initialising upload button
        uploadBtn = getActivity().findViewById(R.id.UploadPost);

        //Settings for the Select Region Button
        LocationButton = (Button) getActivity().findViewById(R.id.regionSelectButton);


        //code for location button
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


        getView().findViewById(R.id.UploadImageBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);

                }
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {


                enableViews(getView(), false);


                if (uploadBtn.isEnabled()) {

                    uploadBtn.setEnabled(false);

                }

                if (pTitle.getText().length() == 0 || pDesc.getText().length() == 0 || pContact.getText().length() == 0) {

                    Toast.makeText(getContext(), "Please Fill All Fields", Toast.LENGTH_SHORT).show();
                    uploadBtn.setEnabled(true);
                    enableBottomBar(true);

                } else {
                    final ProgressBar progressBar = getActivity().findViewById(R.id.loading);
                    progressBar.setVisibility(View.VISIBLE);
                    enableBottomBar(false);

                    //take Id and inceremnt NextPostId in Realtime Database
                    RootReff.child("NextPostId").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task.isSuccessful()) {

                                PostId = Integer.parseInt(task.getResult().getValue().toString());
                                RootReff.child("NextPostId").setValue(PostId + 1);
                                if (!bitmaps.isEmpty()) {
                                    PicCounter = 0;
                                    for (final Bitmap b : bitmaps) {
                                        PicCounter++;

                                        // Turning bitmap into bitmap stream for upload
                                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                        b.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                                        byte[] bitmapdata = bos.toByteArray();
                                        ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);

                                        UploadTask uploadTask = storageRef.child(String.valueOf(PostId)).child(String.valueOf(PicCounter)).putStream(bs);
                                        uploadTask.addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                                Toast.makeText(getContext(), "Error While Uploading...Please Try Again Later", Toast.LENGTH_SHORT).show();
                                                uploadBtn.setEnabled(true);
                                                enableBottomBar(true);

                                            }
                                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                                                postsList.child(String.valueOf(PostId)).child("PostTitle").setValue(pTitle.getText().toString());
                                                postsList.child(String.valueOf(PostId)).child("PostDescription").setValue(pDesc.getText().toString());
                                                postsList.child(String.valueOf(PostId)).child("PostContactInfo").setValue(pContact.getText().toString());
                                                postsList.child(String.valueOf(PostId)).child("PostRegion").setValue(LocationButton.getText().toString());
                                                postsList.child(String.valueOf(PostId)).child("PostUploaderID").setValue(UserId);
                                                postsList.child(String.valueOf(PostId)).child("numberOfImages").setValue(String.valueOf(bitmaps.size()));


                                                if (radioBtnYes.isChecked()) {


                                                    if (ActivityCompat.checkSelfPermission(AddFragment.this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AddFragment.this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                                                        Location location = null;

                                                    if(lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)!=null){

                                                         location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                                                    }

                                                        final double longitude = location.getLongitude();
                                                        final double latitude = location.getLatitude();
                                                        postsList.child(String.valueOf(PostId)).child("PostLatLng").setValue(longitude + "," + latitude).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                RootReff.child("Latlngs").child(String.valueOf(PostId)).setValue(longitude + "," + latitude);
                                                                Toast.makeText(getContext(), "UPLOAD COMPLETE!", Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(getContext(), MainActivity.class);
                                                                startActivity(intent);
                                                                getActivity().finish();

                                                            }

                                                        });

                                                    } else {
                                                        postsList.child(String.valueOf(PostId)).child("PostLatLng").setValue("None").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Toast.makeText(getContext(), "UPLOAD COMPLETE!", Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(getContext(), ProfileFragment.class);
                                                                startActivity(intent);
                                                                getActivity().finish();

                                                            }
                                                        });


                                                    }
                                                }
                                        }

                                    });
                                    }


                                }else if(bitmaps.isEmpty() == true){
                                    Toast.makeText(getContext(), "Please Make sure to include Pictures", Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);
                                    uploadBtn.setEnabled(true);
                                    enableBottomBar(true);
                                    enableViews(getView(), true);



                                }



                            }else{

                                Toast.makeText(getContext(), "Error while uploading... Please Retry", Toast.LENGTH_SHORT).show();
                                uploadBtn.setEnabled(false);
                                enableBottomBar(true);
                            }
                        }

                    });



                }

            }
        });
    }

    private void enableBottomBar(boolean enable){
        for (int i = 0; i < navBar.getMenu().size(); i++) {
            navBar.getMenu().getItem(i).setEnabled(enable);
        }
    }

    public void setthelist(final List<Bitmap> bitmaps)
    {


       //setting the images in the list in the UI
        for( final Bitmap b : bitmaps)
        {

            final LinearLayout imageContainer = getView().findViewById(R.id.ImageContainer);
            ImageView image = new ImageView(getContext());

            //Fixing the image and its sizing and scaling
            //Bitmap bitmap = Bitmap.createScaledBitmap(b,(b.getWidth()/2)>400?400 : b.getWidth()/2,420,true);


            //image view sizing
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(400, 420) ;
            image.setLayoutParams(lp);

            // setting the image
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            image.setImageDrawable(new BitmapDrawable(getResources(),b));






            //Adding the Image to the Container
            imageContainer.addView(image);

            image.setTag(new Integer(bitmaps.indexOf(b)));
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bitmaps.remove(bitmaps.indexOf(b));
                    imageContainer.removeAllViews();
                    setthelist(bitmaps);
                }
            });
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) image.getLayoutParams();
            layoutParams.setMargins(0, 10, 10, 10);



        }

    }







    //image compress

    private Bitmap decodeImage(Uri i) throws FileNotFoundException {
        Bitmap b = null;

        //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;

        InputStream is = getActivity().getContentResolver().openInputStream(i);
        BitmapFactory.decodeStream(is,null,o);


        int scale = 1;
        if (o.outHeight > 1000 || o.outWidth > 1000) {
            scale = (int)Math.pow(2, (int) Math.ceil(Math.log(1000 /
                    (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
        }

        //Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        InputStream is2 = getActivity().getContentResolver().openInputStream(i);
        Bitmap bitmap = BitmapFactory.decodeStream(is2,null,o2);


        return bitmap;
    }
    private void enableViews(View v, boolean enabled) {
        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0;i<vg.getChildCount();i++) {
                enableViews(vg.getChildAt(i), enabled);
            }
        }
        v.setEnabled(enabled);
    }


}
