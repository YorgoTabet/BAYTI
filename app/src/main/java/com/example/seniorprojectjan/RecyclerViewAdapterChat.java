package com.example.seniorprojectjan;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

public class RecyclerViewAdapterChat extends RecyclerView.Adapter<RecyclerViewAdapterChat.MyViewHolder> {

    ArrayList<String> userChats;
    Context context;
    String myID;
    String hisID;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference root = db.getReference();


    public RecyclerViewAdapterChat(Context ct, ArrayList<String> listOfchats)
    {
        context = ct;
        userChats = listOfchats;


    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);


        View view = inflater.inflate(R.layout.chat_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {


        String chatId = userChats.get(position);
        String ids[] = chatId.split("_");
        for(String id:ids){

            if(id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){

                myID = id;

            }else
            {
                hisID = id;
            }

        }
        root.child("users").child(hisID).child("Username").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

               if(task.getResult().getValue()!=null)
                holder.username.setText(task.getResult().getValue().toString().toUpperCase());


            }
        });
        final String roomId = hisID.compareTo(myID)<0?hisID+"_"+myID:myID+"_"+hisID;
        root.child("chats").child(roomId).child("messages").orderByValue().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int unreadMessages=0;

                for(DataSnapshot s : snapshot.getChildren()){
                    if(s.child("message").getValue()!=null) {
                        if (s.child("sender").getValue().toString().equals(myID)) {

                            holder.lastmessage.setText("YOU: " + s.child("message").getValue().toString());

                        } else {

                            holder.lastmessage.setText(s.child("message").getValue().toString());

                            if(s.child("read").getValue()!=null){
                                if(Objects.requireNonNull(s.child("read").getValue()).toString().equals("false")){
                                holder.unreadmsgs.setVisibility(View.VISIBLE);
                                unreadMessages++;
                                holder.unreadmsgs.setText(String.valueOf(unreadMessages));

                            }

                            }


                        }
                    }

                }if(unreadMessages == 0)
                    holder.unreadmsgs.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.mainlayout.setTag(hisID);

        holder.mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,messagingActivity.class);
                intent.putExtra("pUploaderID",holder.mainlayout.getTag().toString());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return userChats.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView username;
        TextView lastmessage;
        RelativeLayout mainlayout;
        TextView unreadmsgs;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.usernametxtView);
            lastmessage = itemView.findViewById(R.id.lastMessage);
            mainlayout = itemView.findViewById(R.id.mainLayout);
            unreadmsgs = itemView.findViewById(R.id.unreadmsgs);
        }
    }


}
