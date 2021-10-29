package comp90018.fitness.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import comp90018.fitness.R;

public class ChatListFragment extends Fragment {
    private RecyclerView friRecyclerView;
    private Button refresh;
    private LinearLayoutManager layoutManager;
    private List<Friend> friendContentList = new ArrayList<>();
    private FriendAdapter adapter ;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.chatlist_activity, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.friRecyclerView = view.findViewById(R.id.msg_recycler_view);
        this.layoutManager = new LinearLayoutManager(view.getContext());
        this.adapter = new FriendAdapter(friendContentList = this.getData(),view.getContext());
        this.friRecyclerView.setAdapter(adapter);
        this.friRecyclerView.setLayoutManager(layoutManager);
        this.refresh = view.findViewById(R.id.send);

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
