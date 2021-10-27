package com.example.a2;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {


    private RecyclerView msgRecyclerView;
    private EditText inputText;
    private Button send;
    private LinearLayoutManager layoutManager;
    private List<MessageContent> messageContentList = new ArrayList<>();
    private MsgAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);
        this.msgRecyclerView = findViewById(R.id.msg_recycler_view);
        this.inputText = findViewById(R.id.input_text);

        this.layoutManager = new LinearLayoutManager(this);
        this.messageContentList = initialChat();
        this.adapter = new MsgAdapter(messageContentList);
        this.msgRecyclerView.setAdapter(adapter);
        this.msgRecyclerView.setLayoutManager(layoutManager);
        this.send = findViewById(R.id.send);

            //1获取内容，将需要发送的消息添加到 List 当中去。
           // 2调用适配器的notifyItemInserted方法，通知有新的数据加入了，赶紧将这个数据加到 RecyclerView 上面去。
           // 3调用RecyclerView的scrollToPosition方法，以保证一定可以看的到最后发出的一条消息。
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = inputText.getText().toString();
                if(!content.equals("")) {
                    messageContentList.add(new MessageContent(content, true));
                    adapter.notifyItemInserted(messageContentList.size()-1);
                    // ensue to scroll to the current position
                    msgRecyclerView.scrollToPosition(messageContentList.size()-1);
                    inputText.setText("");
                }

                    messageContentList.add(new MessageContent("Aoto response ", false));
                    adapter.notifyItemInserted(messageContentList.size()-1);
                    msgRecyclerView.scrollToPosition(messageContentList.size()-1);

            }
        });
    }

    private List<MessageContent> initialChat(){
        List<MessageContent> list = new ArrayList<>();
        list.add(new MessageContent("Hi, I am "+getIntent().getStringExtra("name")+",let's chat~", false));
        return list;
    }


}
