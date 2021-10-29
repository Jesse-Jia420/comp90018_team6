package comp90018.fitness.ui.moments;

import static android.content.ContentValues.TAG;

import static comp90018.fitness.ui.moments.placeholder.PlaceholderContent.addItem;
import static comp90018.fitness.ui.moments.placeholder.PlaceholderContent.calcDistance;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import comp90018.fitness.MainActivity;
import comp90018.fitness.R;
import comp90018.fitness.ui.moments.placeholder.PlaceholderContent;

/**
 * A fragment representing a list of Items.
 */
public class ItemFragment extends Fragment implements MyItemRecyclerViewAdapter.ViewHolder.OnItemListener {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 2;

    ActionBar actionBar;
    private Button mButton;



    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ItemFragment newInstance(int columnCount) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        // Set the adapter
//        if (view instanceof RecyclerView) {
        if (view.findViewById(R.id.list) instanceof RecyclerView) {

            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
            mButton =  view.findViewById(R.id.addMoment);

            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 给bnt1添加点击响应事件
                    Intent intent = new Intent(ItemFragment.this.getActivity(), AddMomentActivity.class);
                    //启动
                    startActivity(intent);
                }
            });
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            PlaceholderContent.ITEMS.clear();
            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("post_test")
                    .orderBy("mTime", Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        private String TAG;
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String distance = calcDistance();
                                    ArrayList<String> imgList = (ArrayList<String>) document.get("mImageUrl");
                                    String imgUrlTemp = "";
                                    if (imgList.size() != 0) {
                                        imgUrlTemp = imgList.get(0).toString();
                                    }
                                    PlaceholderContent.PlaceholderItem tempItem = new PlaceholderContent.PlaceholderItem(document.getId(), document.get("mContent").toString(), document.get("mTitle").toString(), imgUrlTemp, document.get("mTime").toString(), document.get("mAuthorName").toString(), document.get("mAuthorAvatarUrl").toString(), distance, imgList);
                                    addItem(tempItem);
                                }
                            } else {
                                Log.w(TAG, "Error getting documents.", task.getException());
                            }
                            MyItemRecyclerViewAdapter mAdapter;
                            mAdapter = new MyItemRecyclerViewAdapter(PlaceholderContent.ITEMS, getContext(), ItemFragment.this);
                            recyclerView.setAdapter(mAdapter);

                        }
                    });

        }


        return view;
    }


    @Override
    public void onItemClick(int position) {
        Log.d(TAG,"onItemClick: clicked."+position);
        Intent intent = new Intent(ItemFragment.this.getActivity(), MomentDetailsActivity.class);
        intent.putExtra("some_content", (Parcelable) PlaceholderContent.ITEMS.get(position));
        startActivity(intent);
    }

//        public static void getFirebaseData() {
//        PlaceholderContent.ITEMS.clear();
//        final FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection("post_test")
//                .orderBy("mTime", Query.Direction.DESCENDING)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    private String TAG;
//
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                String distance = calcDistance();
//                                ArrayList<String> imgList = (ArrayList<String>) document.get("mImageUrl");
//                                String imgUrlTemp = "";
//                                if (imgList.size() != 0) {
//                                    imgUrlTemp = imgList.get(0).toString();
//                                }
//                                PlaceholderContent.PlaceholderItem tempItem = new PlaceholderContent.PlaceholderItem(document.getId(), document.get("mContent").toString(), document.get("mTitle").toString(), imgUrlTemp, document.get("mTime").toString(), document.get("mAuthorName").toString(), document.get("mAuthorAvatarUrl").toString(), distance, imgList);
//                                addItem(tempItem);
//                            }
//                        } else {
//                            Log.w(TAG, "Error getting documents.", task.getException());
//                        }
//                    }
//                });
//    }
}