package com.example.a2;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder>{
    private  Context context;

    public FriendAdapter(List<Friend>  list,Context context){
        this.list = list;
        this.context = context;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout layout_recived;
        TextView recived_msg;


        public ViewHolder(View view){
            super(view);
            layout_recived = view.findViewById(R.id.layout1);
            recived_msg = view.findViewById(R.id.msg1);
        }
    }
    private List<Friend> list;




    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder( ViewHolder  holder, int position) {
        Friend friend= list.get(position);
        StringBuilder str = new StringBuilder(friend.getName());
        holder.layout_recived.setVisibility(View.VISIBLE);
        holder.recived_msg.setText( str.toString());
        holder.layout_recived.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent intent = new Intent();
                 intent.setClass(context ,ChatActivity.class);
                 intent.putExtra("name",str.toString());
                 context.startActivity(intent);
             }
         });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    ////////























}