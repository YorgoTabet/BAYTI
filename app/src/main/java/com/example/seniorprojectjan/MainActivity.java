package com.example.seniorprojectjan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Testing the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference RootReff = database.getReference();








        //initialize firebase auth
        mAuth = FirebaseAuth.getInstance();
        UserInfo user = mAuth.getCurrentUser();

        //Check if user is logged in  or not to know where to navigate him

        if(user != null){

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();

        }else {

            Intent SignInIntent = new Intent(getBaseContext(), LoginPage.class);
            startActivity(SignInIntent);
            finish();

        }




            BottomNavigationView bottomNav = findViewById(R.id.Bottom_Nav);
        bottomNav.setOnNavigationItemSelectedListener(navListener);


        // Floating button assignment with onClick listener

        FloatingActionButton fab = findViewById(R.id.chatFloatingButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ChatActivity.class);
             startActivity(intent);
            }
        });




        // move the fragment to the home on create

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeFragment()).commit();



       ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);



        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);




    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    String selectedfragmenttag ="";





                    switch (item.getItemId()){
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            selectedfragmenttag = "home";
                            break;

                        case R.id.nav_add:
                            selectedFragment = new AddFragment();
                            selectedfragmenttag = "add";
                            break;

                        case R.id.nav_map:
                            selectedFragment = new MapFragment();
                            selectedfragmenttag = "map";
                            break;

                        case R.id.nav_profile:
                            selectedFragment = new ProfileFragment();
                            selectedfragmenttag = "profile";
                            break;

                        case R.id.nav_search:
                            selectedFragment = new SearchFragment();
                            selectedfragmenttag = "search";
                            break;
                    }


                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment,selectedfragmenttag).commit();
                return true;
                }
            };
}