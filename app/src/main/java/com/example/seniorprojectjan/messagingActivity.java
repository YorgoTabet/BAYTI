package com.example.seniorprojectjan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class messagingActivity extends AppCompatActivity {

    TextView title;
    TextView description;

    FirebaseUser firebaseUser;
    DatabaseReference reff;

    String pTitle ;
    Intent intent;

    ImageButton btn_send;
    EditText text_send;
    RecyclerView recyclerView;
    RecyclerViewAdapterMessages recyclerViewAdapterMessages;
    List<ResponseMessage> responseMessageList;
    String chatID;

    String message = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);
        recyclerView = findViewById(R.id.recycler_View);


         responseMessageList = new ArrayList<>();
        recyclerViewAdapterMessages= new RecyclerViewAdapterMessages(responseMessageList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerViewAdapterMessages);


        intent = getIntent();
        final String pUploaderID = intent.getStringExtra("pUploaderID");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reff = FirebaseDatabase.getInstance().getReference();


        final String roomId = pUploaderID.compareTo(firebaseUser.getUid())<0?pUploaderID+"_"+firebaseUser.getUid():firebaseUser.getUid()+"_"+pUploaderID;

        reff.child("chats").child(roomId).child("messages").orderByValue().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //Clear the list to avoid duplicates
                responseMessageList.clear();
                //get the messages and display them
                for(DataSnapshot s : snapshot.getChildren()){

                    String epoche = s.getKey();
                    Log.i("I am epoche", epoche);


                    if(s.child("message").getValue()!=null) {
                        message = s.child("message").getValue().toString();
                    }




                    if(s.child("sender").getValue().toString().equals(firebaseUser.getUid())){

                        ResponseMessage chatmessage = new ResponseMessage(message,true);
                        responseMessageList.add(chatmessage);
                        recyclerViewAdapterMessages.notifyDataSetChanged();

                    }else{

                        ResponseMessage chatmessage = new ResponseMessage(message,false);
                        responseMessageList.add(chatmessage);
                        recyclerViewAdapterMessages.notifyDataSetChanged();
                    }


                }

                recyclerView.scrollToPosition(recyclerViewAdapterMessages.getItemCount()-1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = text_send.getText().toString();
                if(!msg.equals(" ") && !equals("") && !msg.isEmpty()){
                    String timeofsend = String.valueOf(System.currentTimeMillis()/1000);
                    reff.child("userChats").child(pUploaderID).child(roomId).setValue("true");
                    reff.child("userChats").child(firebaseUser.getUid()).child(roomId).setValue("true");
                    reff.child("chats").child(roomId).child("messages").child(timeofsend).child("sender").setValue(firebaseUser.getUid());
                    reff.child("chats").child(roomId).child("messages").child(timeofsend).child("message").setValue(text_send.getText().toString());

                }else{

                    Toast.makeText(messagingActivity.this, "You can't send empty messages", Toast.LENGTH_SHORT).show();

                }
                text_send.setText("");
            }
        });

        reff.child("users").child(pUploaderID).child("Username").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                if(task.getResult().getValue()!=null)
                setTitle(task.getResult().getValue().toString());
            }
        });


    }

}