package com.example.seniorprojectjan;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.cache.memory.impl.LargestLimitedMemoryCache;

import java.util.Locale;
import java.util.Objects;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    SupportMapFragment mapFragment;
    GoogleMap mMap;
    LocationManager lManager;
    LocationListener lListener;
    Location lastKnownLocation;
    Button ViewpostBtn;
    String selectedPostID;
    LatLng userLocation;
    Intent intent;



    // Initializing the DB
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference RootReff = database.getReference();
    DatabaseReference locations = RootReff.child("Latlngs");

    FloatingActionButton fab;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, lListener);
                Location lastKnownLocation = lManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                mMap.clear();
                LatLng userLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                //  mMap.addMarker(new MarkerOptions().position(userLocation).title("YOU"));


            }


        }


    }


    @Override
    @Nullable

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        //getting the map setup

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {

            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            ft.replace(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
        return inflater.inflate(R.layout.fragment_map, container, false);


    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);




        ViewpostBtn = getActivity().findViewById(R.id.ViewpostBtn);
        ViewpostBtn.setEnabled(false);
         fab = getActivity().findViewById(R.id.chatFloatingButton);
        fab.setVisibility(View.GONE);

    }

    // Setting up the map after it's ready.
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        intent = getActivity().getIntent();
        final String postID = intent.getStringExtra("ID");
        if(postID!=null)
        {

                locations.child(postID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {

                        String sourceLoc = Objects.requireNonNull(task.getResult().getValue()).toString();
                        if(sourceLoc!="None"){


                            Log.i("Loc to focus on", sourceLoc);
                            String[] latlng = sourceLoc.split(",");
                            final double longitude = Double.parseDouble(latlng[0]);
                            final double latitude = Double.parseDouble(latlng[1]);
                            LatLng locationtofocus = new LatLng(latitude, longitude);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationtofocus, ((float) 15)));

                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Location Not Found", Toast.LENGTH_SHORT).show();
                    }
                });





        }


        mMap = googleMap;

        //Set the camera over lebanon
        LatLng lebanonLoc = new LatLng(33.8138, 35.8535);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lebanonLoc, ((float) 8.5)));

        //Gets the person location.
        lManager = (LocationManager) getContext().getSystemService((Context.LOCATION_SERVICE));
        lListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                //Making the Map track your position

                 userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                //mMap.addMarker(new MarkerOptions().position(userLocation).title("YOU"));
                //We can add this line to make the camera move with the user
                // mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }

        };

        //Permissions and initialising the map location on accept
        if (Build.VERSION.SDK_INT < 23) {

            lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, lListener);

        } else {

            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, lListener);
                Location lastKnownLocation = lManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                mMap.clear();
                // userLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
               // mMap.addMarker(new MarkerOptions().position(userLocation).title("YOU"));

                //Add this line to make the camera focus on the user Location
                // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
            }
        }



        //Setting all the post locations
        locations.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (final DataSnapshot locations : snapshot.getChildren()) {

                    String PostID = locations.getKey();
                    String sourceLoc = locations.getValue().toString();
                    String[] latlng = sourceLoc.split(",");
                    final double longitude = Double.parseDouble(latlng[0]);
                    final double latitude = Double.parseDouble(latlng[1]);
                    LatLng userLocation = new LatLng(latitude, longitude);
                    mMap.addMarker(new MarkerOptions().position(userLocation).title(PostID));
                    Log.i("Info", "ID: " + PostID + " Location: " + sourceLoc);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getTitle() != "YOU")
                    selectedPostID = marker.getTitle();
                ViewpostBtn.setEnabled(true);
                return false;
            }
        });

        mMap.setOnInfoWindowCloseListener(new GoogleMap.OnInfoWindowCloseListener() {
            @Override
            public void onInfoWindowClose(Marker marker) {
                ViewpostBtn.setEnabled(false);
            }
        });

        ViewpostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPostID != null && selectedPostID != "YOU") {
                    Intent intent = new Intent(getContext(), PropertyScreenActivity.class);
                    intent.putExtra("ID", selectedPostID);
                    startActivity(intent);
                }
            }
        });



    }

        @Override
        public void onDetach() {
            super.onDetach();
            mMap.clear();
            fab.setVisibility(View.VISIBLE);

        }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMap.clear();
        fab.setVisibility(View.VISIBLE);
    }
}





