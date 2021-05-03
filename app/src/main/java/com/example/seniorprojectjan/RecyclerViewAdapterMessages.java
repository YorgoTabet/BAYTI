package com.example.seniorprojectjan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerViewAdapterMessages extends  RecyclerView.Adapter<RecyclerViewAdapterMessages.CustomViewHolder> {


        class CustomViewHolder extends RecyclerView.ViewHolder{


            TextView textView;
    public CustomViewHolder(View itemView){

        super(itemView);

        textView = itemView.findViewById(R.id.show_message);



    }

}
    List<ResponseMessage> responseMessageList;


    public RecyclerViewAdapterMessages(List<ResponseMessage> responseMessageList){

        this.responseMessageList = responseMessageList;

  }

    @Override
    public int getItemViewType(int position) {
        if(responseMessageList.get(position).isMe){

            return R.layout.chat_item_right;

        }
        return R.layout.chat_item_left;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return  new CustomViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapterMessages.CustomViewHolder holder, int position) {

        holder.textView.setText(responseMessageList.get(position).getTextMessage());

    }

    @Override
    public int getItemCount() {
        return responseMessageList.size();
    }
}
