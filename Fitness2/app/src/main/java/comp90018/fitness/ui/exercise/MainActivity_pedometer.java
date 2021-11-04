package comp90018.fitness.ui.exercise;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import comp90018.fitness.R;
import android.content.SharedPreferences;

public class MainActivity_pedometer extends Fragment {
    private TextView stepCount;
    private Pedometer pedometer;
    private FirebaseFirestore db;

    private String TAG = "Exercise Home";
    public static String MESSAGE = "Message";
    public static int MESSAGE_RECEIVE = 1;
    public String UID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_home, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getActivity().getSharedPreferences("MyPrefs", getActivity().MODE_PRIVATE);
        UID = prefs.getString("UID", "none"); // get user ID
        Log.d(TAG, "onViewCreated: " + UID);

        stepCount = view.findViewById(R.id.pedometer_stepCount);

        pedometer = new Pedometer(view.getContext());
        EventBus.getDefault().register(this);

        Button exercise_button = view.findViewById(R.id.exercise_button);
        Button exercise_button1 = view.findViewById(R.id.exercise_button1);
        // Start exercise activity
        exercise_button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(view.getContext(), SecondActivity_exercise.class);
                        intent.putExtra("UID",UID); // Pass UID value to the second activity
                        startActivity(intent);

                    }
                });

        // Start personal information activity
        exercise_button1.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(view.getContext(), ThirdActivity_info.class);
                        intent.putExtra("UID",UID); // Pass UID value to the third activity
                        startActivity(intent);

                    }
                });
        initData();
    }

    public String getUID(){
        return UID;
    }

    // initial database, modification avalible
    private void initData(){
        db = FirebaseFirestore.getInstance();

        db.collection("user_test")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d(TAG, document.getId() + " => " + document.getData());

                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });



    }

    // Step Counter Sensor working
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(PedometerMessage event) {
        if (stepCount != null) {
            stepCount.setText(String.valueOf(event.getStepCount()));
        }

    }


}
