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


public class MainActivity_pedometer extends Fragment {
    TextView stepCount;

    Pedometer pedometer;

    FirebaseFirestore db;


    private String TAG = "First Demo";
    public static String MESSAGE = "Message";
    public static int MESSAGE_RECEIVE = 1;

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

        stepCount = view.findViewById(R.id.pedometer_stepCount);


        pedometer = new Pedometer(view.getContext());
        EventBus.getDefault().register(this);

        Button exercise_button = view.findViewById(R.id.exercise_button);
        Button exercise_button1 = view.findViewById(R.id.exercise_button1);
        exercise_button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(view.getContext(), SecondActivity_exercise.class);
                        startActivity(intent);

                    }
                });
        exercise_button1.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(view.getContext(), ThirdActivity_info.class);
                        startActivity(intent);

                    }
                });
        initData();
    }


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



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(PedometerMessage event) {
        if (stepCount != null) {
            stepCount.setText(String.valueOf(event.getStepCount()));
        }

    }


}
