package com.example.a2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ChatListActivity extends AppCompatActivity {
    private RecyclerView friRecyclerView;
    private Button refresh;
    private LinearLayoutManager layoutManager;
    private List<Friend> friendContentList = new ArrayList<>();
    private FriendAdapter adapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatlist_activity);
        this.friRecyclerView = findViewById(R.id.msg_recycler_view);
        this.layoutManager = new LinearLayoutManager(this);
        this.adapter = new FriendAdapter(friendContentList = this.getData(),this);
        this.friRecyclerView.setAdapter(adapter);
        this.friRecyclerView.setLayoutManager(layoutManager);
        this.refresh = findViewById(R.id.send);

        this.refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });


    }

    private List<Friend> getData(){
        List<Friend> list = new ArrayList<>();
        list.add(new Friend(1,"zhang"));
        list.add(new Friend(1,"zhang"));

        list.add(new Friend(1,"zhang"));
        list.add(new Friend(1,"zhang"));
        list.add(new Friend(2,"wang"));
        list.add(new Friend(3,"fang"));
        list.add(new Friend(3,"fang"));
        list.add(new Friend(3,"fang"));
        list.add(new Friend(3,"fang"));
        list.add(new Friend(3,"fang"));


        return list;
    }
}
