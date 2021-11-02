package comp90018.fitness.ui.find_my_friends;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import comp90018.fitness.R;

public class FindFriendsFragment extends Fragment implements OnMapReadyCallback {

    private FusedLocationProviderClient fusedLocationProviderClient;
    public static Location currentLocation;
    private GoogleMap map;
    private static final int REQUEST_CODE = 101;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Marker marker;
    private Marker friendMarker;
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static String TAG = FindFriendsFragment.class.getSimpleName();
    private static ArrayList<String> AllNames = new ArrayList<>();
    private static ArrayList<Integer> AllImageIds = new ArrayList<>();
    private static ArrayList<String> names = new ArrayList<>();
    private static ArrayList<String> distances = new ArrayList<>();
    private static ArrayList<Integer> imageIds = new ArrayList<>();
    public static ArrayList<Map<String, Object>> friends = new ArrayList<>();
    private AvailableFriendList availableFriendList;
    private Polyline polyline;
    private static String UID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        fetchLocation();
        return inflater.inflate(R.layout.fragment_find_friends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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

        Button LocateButton = (Button) view.findViewById(R.id.locate);
        LocateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateLocation();
                updateFriends();
                if (availableFriendList != null){
                    availableFriendList.notifyDataSetChanged();
                }
                updateSharedLocation();
            }
        });

        Button shareButton = (Button) view.findViewById(R.id.shareLocation);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareLocationDialog(view);
            }
        });

        // Create a new user with a first and last name
//        Map<String, Object> user = new HashMap<>();
//        Map<String, Object> friend1 = new HashMap<>();
//        Map<String, Object> friend2 = new HashMap<>();
//        ArrayList<Map<String, Object>> friendsData = new ArrayList<>();
//        friend1.put("id", "AQHqhY7NNEb7b0KzhxHu");
//        friend1.put("name", "Bob");
//        friend1.put("lat", -37.816178);
//        friend1.put("long", 145.015345);
//        friend2.put("id", "XxuLTJ6E27szh8pVQ4N0");
//        friend2.put("name", "Emma");
//        friend2.put("lat", -37.7571);
//        friend2.put("long", 145.0307);
//        friendsData.add(friend1);
//        friendsData.add(friend2);
//        user.put("friends", friendsData);
//
//        db.collection("users").document(UID).set(user);

        // Add a new document with a generated ID
//        db.collection("users")
//                .add(user)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error adding document", e);
//                    }
//                });

        DocumentReference docRef = db.collection("users").document(UID);

        Map<String, Object> friend1 = new HashMap<>();
        Map<String, Object> friend2 = new HashMap<>();
        ArrayList<Map<String, Object>> friendsData = new ArrayList<>();
        friend1.put("id", "AQHqhY7NNEb7b0KzhxHu");
        friend1.put("name", "dummy friend 1");
        friend1.put("lat", -37.816178);
        friend1.put("long", 145.015345);
        friend2.put("id", "XxuLTJ6E27szh8pVQ4N0");
        friend2.put("name", "dummy friend 2");
        friend2.put("lat", -37.7571);
        friend2.put("long", 145.0307);
        friendsData.add(friend1);
        friendsData.add(friend2);
        docRef.update("friends", friendsData);

        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData().get("friends"));
                    friends = (ArrayList<Map<String, Object>>) snapshot.getData().get("friends");
                    updateFriends();
                    ListView l;
                    availableFriendList = new AvailableFriendList(getActivity(), names, distances, imageIds);
                    l = view.findViewById(R.id.availableFriendList);
//                        ArrayAdapter<String> arr;
//                        arr = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, names);
                    l.setAdapter(availableFriendList);
                    l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if (polyline != null) {
                                polyline.remove();
                            }
                            LatLng latLng = new LatLng((Double) friends.get(position).get("lat"), (Double) friends.get(position).get("long"));
                            LatLng start = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            if (friendMarker == null) {
                                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Your friend is here!");
                                friendMarker = map.addMarker(markerOptions);
                            }
                            map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
                            friendMarker.setPosition(latLng);
                            if (currentLocation != null){
                                new FindNavigationRoute(start, latLng, new RouteListener() {
                                    @Override
                                    public void drawPath(PolylineOptions polyLine) {
                                        polyline = map.addPolyline(polyLine);
                                    }
                                }).execute();
                            }
                        }
                    });
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });

    }

    private static void updateFriends(){
        names.clear();
        distances.clear();
        imageIds.clear();
        AllNames.clear();
        AllImageIds.clear();
        for (int i = 0; i<friends.size(); i++) {
            AllNames.add((String) friends.get(i).get("name"));
            AllImageIds.add(R.drawable.ic_baseline_account_circle_24);
            if (friends.get(i).get("lat") != null && friends.get(i).get("long") != null){
                names.add((String) friends.get(i).get("name"));
                float[] results = new float[1];
                if (currentLocation != null) {
                    Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(),
                            (Double) friends.get(i).get("lat"), (Double) friends.get(i).get("long"),
                            results);
                    distances.add(String.format("%.1f", results[0] / 1000) + "km");
                } else {
                    distances.add("--km");
                }
                imageIds.add(R.drawable.ic_baseline_account_circle_24);
            }
        }
    }


    public static void shareLocation(int position){
        DocumentReference docRef = db.collection("users").document((String) friends.get(position).get("id"));
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        ArrayList<Map<String, Object>> friends = (ArrayList<Map<String, Object>>) document.get("friends");
                        for (Map<String, Object> friend : friends){
                            if (((String) friend.get("id")).equals(UID)){
                                Log.d(TAG, "12");
                                friend.put("lat", currentLocation.getLatitude());
                                friend.put("long", currentLocation.getLongitude());
                            }
                        }
                        docRef.update("friends", friends);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        DocumentReference docRef2 = db.collection("users").document(UID);
        friends.get(position).put("locationShared", true);
        docRef2.update("friends", friends);
    }

    public static void stopSharingLocation(int position){
        DocumentReference docRef = db.collection("users").document((String) friends.get(position).get("id"));
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        ArrayList<Map<String, Object>> friends = (ArrayList<Map<String, Object>>) document.get("friends");
                        for (Map<String, Object> friend : friends){
                            if (((String) friend.get("id")).equals(UID)){
                                friend.remove("lat");
                                friend.remove("long");
                            }
                        }
                        docRef.update("friends", friends);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        DocumentReference docRef2 = db.collection("users").document(UID);
        friends.get(position).put("locationShared", false);
        docRef2.update("friends", friends);

    }

    private void shareLocationDialog(View view) {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity());
        bottomSheetDialog.setContentView(R.layout.share_location_bottom_sheet);

        ListView l;
        l = bottomSheetDialog.findViewById(R.id.myFriendList);
        ShareLocationList shareLocationList = new ShareLocationList(getActivity(), AllNames, AllImageIds);
        l.setAdapter(shareLocationList);

        bottomSheetDialog.show();
    }

    private void updateSharedLocation(){
        for (Map<String, Object> friend : friends){
            if (friend.get("locationShared") != null && (Boolean) friend.get("locationShared")){
                DocumentReference docRef = db.collection("users").document((String) friend.get("id"));
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                ArrayList<Map<String, Object>> friends2 = (ArrayList<Map<String, Object>>) document.get("friends");
                                for (Map<String, Object> friend2 : friends2){
                                    if (((String) friend2.get("id")).equals(UID)){
                                        friend2.put("lat", currentLocation.getLatitude());
                                        friend2.put("long", currentLocation.getLongitude());
                                    }
                                }
                                docRef.update("friends", friends2);
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
            }
        }
    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }
//        fusedLocationProviderClient.requestLocationUpdates(null,null);
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
//                    Toast.makeText(getActivity().getApplicationContext(), currentLocation.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
                    assert supportMapFragment != null;
                    supportMapFragment.getMapAsync(FindFriendsFragment.this);
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
//        updateLocation();

    }

    public void updateLocation(){
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                currentLocation = locationResult.getLastLocation();
            }
        };
        if (fusedLocationProviderClient != null) {
            if (ActivityCompat.checkSelfPermission(
                    getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            }
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

        if (marker == null) {
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("I am here!");
            marker = map.addMarker(markerOptions);
        }
        map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
        marker.setPosition(latLng);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLocation();
                }
                break;
        }
    }
}