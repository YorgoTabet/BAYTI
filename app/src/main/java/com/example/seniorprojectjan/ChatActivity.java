package com.example.seniorprojectjan;



import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ChatActivity extends AppCompatActivity {


    FirebaseDatabase Database = FirebaseDatabase.getInstance();
    DatabaseReference root = Database.getReference();

    FirebaseUser currentuser = FirebaseAuth.getInstance().getCurrentUser();

    ArrayList<String> userChats;


    RecyclerView listofchats;
    RecyclerViewAdapterChat recyclerViewAdapterChat;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);




       userChats = new ArrayList<>();
       listofchats = findViewById(R.id.recycler_View);

       //Adapter
       recyclerViewAdapterChat = new RecyclerViewAdapterChat(this,userChats);
       listofchats.setAdapter(recyclerViewAdapterChat);
        LinearLayoutManager ll =  new LinearLayoutManager(this);
       listofchats.setLayoutManager(new LinearLayoutManager(this));



       String userId = currentuser.getUid();


       //getting the user chats
        root.child("userChats").child(userId).orderByChild("epoch").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                userChats.clear();

                for (DataSnapshot s: snapshot.getChildren()){

                    userChats.add(s.getKey());
                    Collections.reverse(userChats);
                    recyclerViewAdapterChat.notifyDataSetChanged();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });








    }



}