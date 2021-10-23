package comp90018.fitness.ui.moments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.AccessController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import comp90018.fitness.R;
import comp90018.fitness.ui.moments.placeholder.PlaceholderContent;

public class MomentDetailsActivity extends AppCompatActivity {


    private static final String TAG = "MomentDetailsActivity";

    private ViewPager mLoopPager;
    private LooperPagerAdapter mLoopPagerAdapter;
    private ArrayList<String> imgUrlList = new ArrayList<String>();
    private TextView mTitle;
    private TextView mContent;
    private TextView mTime;
    private ImageView mAuthorAvatar;
    private TextView mAuthorName;

    private ListView mCommentsList;
    private ArrayList<CommentsItem> data;


    private ListViewAdapter mCommentsListAdapter;
    private RecyclerView mRecycleView;

    /**
     * An array of sample (placeholder) items.
     */
    public static final ArrayList<CommentsItem> ITEMS = new ArrayList<CommentsItem>();

    /**
     * A map of sample (placeholder) items, by ID.
     */
    public static final Map<String, CommentsItem> ITEM_MAP = new HashMap<String, CommentsItem>();


    private static void addItem(CommentsItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moment_details);

        initView();
        Log.d(TAG,"onCreate: called.");
//        TextView userName = (TextView)findViewById(R.id.textView2);
        Bundle bundle = getIntent().getExtras();
        if(bundle.getParcelable("some_content") != null)
        {
            //TODO here get the string stored in the string variable and do
            // setText() on userName
            PlaceholderContent.PlaceholderItem item = bundle.getParcelable("some_content");
//            userName.setText(item.content);
            imgUrlList = item.imgList;
            mTitle.setText(item.title);
            mContent.setText(item.content);
            mTime.setText(item.time);
            mAuthorName.setText(item.authorName);
            Glide.with(this)
                    .load(item.authorAvatarUrl)
                    .placeholder(R.drawable.ic_menu_moments)
                    .centerCrop()
                    .into(mAuthorAvatar);
            getComments(item.id);

        }

        mLoopPagerAdapter.setData(imgUrlList);


        mLoopPagerAdapter.notifyDataSetChanged();
    }

    private void getComments(String id) {
        String postId = id;
        ArrayList<CommentsItem> commentList = new ArrayList<CommentsItem>();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("comment_test")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    private String TAG;
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.get("postId").toString().equals(postId)){
                                    CommentsItem tempItem = new CommentsItem(document.getId(), document.get("commentContent").toString(), document.get("time").toString(), document.get("postId").toString(),document.get("userId").toString());
                                    commentList.add(tempItem);
                                    addItem(tempItem);
                                }
                            }
                            mCommentsListAdapter.setData(commentList);
                            mCommentsListAdapter.notifyDataSetChanged();
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void initView() {
        mLoopPager = (ViewPager) this.findViewById(R.id.loop_pager);
        mLoopPagerAdapter = new LooperPagerAdapter();
        mLoopPager.setAdapter(mLoopPagerAdapter);

        mTitle = this.findViewById(R.id.moment_detail_title);
        mContent = this.findViewById(R.id.moment_detail_content);
        mTime = this.findViewById(R.id.moment_detail_time);
        mAuthorAvatar = this.findViewById(R.id.author_avatar);
        mAuthorName = this.findViewById(R.id.author_name);

        mRecycleView = this.findViewById(R.id.moment_comments_list);
        mCommentsListAdapter = new ListViewAdapter(this);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        mRecycleView.setAdapter(mCommentsListAdapter);
    }

    public static class CommentsItem{
        public String id;
        public String content;
        public String time;
        public String postId;
        public String userId;

        public CommentsItem() {
        }

        public CommentsItem(String id, String content, String time, String post_id, String user_id){
            this.id = id;
            this.content = content;
            this.time = time;
            this.postId = post_id;
            this.userId = user_id;
        }

        public CommentsItem[] newArray(int i) {
            return new CommentsItem[i];
        }

    }


    private class MyViewHolder extends RecyclerView.ViewHolder{

        public final TextView mCommentUserName;
        public final TextView mCommentContent;
        public final TextView mCommentTime;
        private int mPosition;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mCommentUserName = itemView.findViewById(R.id.comment_user_name);
            mCommentContent = itemView.findViewById(R.id.comment_content);
            mCommentTime = itemView.findViewById(R.id.comment_time);
        }

        public void setPosition(int position) {
            this.mPosition = position;
        }

        public void setUserName(String name) {
            mCommentUserName.setText(name);
        }
        public void setCommentContent(String content) {
            mCommentUserName.setText(content);
        }
        public void setCommentTime(String time) {
            mCommentTime.setText(time);
        }


    }


}