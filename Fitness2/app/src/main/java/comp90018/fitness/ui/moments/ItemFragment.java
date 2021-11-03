package comp90018.fitness.ui.moments;

import android.Manifest;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.CharArrayWriter;
import java.util.ArrayList;
import java.util.List;

import comp90018.fitness.R;
import comp90018.fitness.ui.login.loginActivity;
import comp90018.fitness.ui.login.loginFrame;
import comp90018.fitness.ui.moments.placeholder.PlaceholderContent;

import static android.content.ContentValues.TAG;
import static comp90018.fitness.ui.moments.placeholder.PlaceholderContent.addItem;

/**
 * A fragment representing a list of Items.
 */
public class ItemFragment extends Fragment implements MyItemRecyclerViewAdapter.ViewHolder.OnItemListener {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 2;

    private Button mButton;
    private String UID = "";
    private FusedLocationProviderClient fusedLocationClient;

    private double LATITUDE = 0;
    private double LONGITUDE = 0;
    private double EARTH_R = 6378100;

    public double getLATITUDE(){
        return this.LATITUDE;
    }
    public double getLONGITUDE(){
        return this.LONGITUDE;
    }


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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        getUserPosition();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        // Set the adapter
        if (view.findViewById(R.id.list) instanceof RecyclerView) {

            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);

            //get user info
            SharedPreferences prefs = getActivity().getSharedPreferences("MyPrefs", getActivity().MODE_PRIVATE);
            UID = prefs.getString("UID", "none");
            Log.d("hello", UID);
            if (UID.equals("none")){
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("You haven't logged in, please log in first.");
                builder.setCancelable(true);
                builder.setPositiveButton(
                        "ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();

            }


            mButton =  view.findViewById(R.id.addMoment);
            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Add onclick function to add moment button
                    Intent intent = new Intent(ItemFragment.this.getActivity(), AddMomentActivity.class);
                    intent.putExtra("UID", UID);
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
                                    String distance = calcDistance((double) document.get("mPosition_Latitude"),(double) document.get("mPosition_Longitude"));
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


    public String calcDistance(double lati, double longi){

        double lat1 = lati  * Math.PI / 180;
        double lat2 = LATITUDE  * Math.PI / 180;
        double lng1 = longi  * Math.PI / 180;
        double lng2 = LONGITUDE  * Math.PI / 180;

        double cos = Math.cos(lat2) * Math.cos(lat1) * Math.cos(lng2 -lng1) + Math.sin(lat1) * Math.sin(lat2);
        return Math.round(EARTH_R * Math.acos(cos)/1000) + " km";
    }

    public String calcTime() {
        return "27 min ago";
    }

    @Override
    public void onItemClick(int position) {
        Log.d(TAG,"onItemClick: clicked."+position);
        Intent intent = new Intent(ItemFragment.this.getActivity(), MomentDetailsActivity.class);
        intent.putExtra("some_content", (Parcelable) PlaceholderContent.ITEMS.get(position));
        intent.putExtra("UID", UID);
        startActivity(intent);
    }

    public void getUserPosition() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if(location != null){
                            LATITUDE = location.getLatitude();
                            LONGITUDE = location.getLongitude();
                        }
                    }

                });
    }

}