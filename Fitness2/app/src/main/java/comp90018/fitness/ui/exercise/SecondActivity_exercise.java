package comp90018.fitness.ui.exercise;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Chronometer;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

import comp90018.fitness.R;

public class SecondActivity_exercise extends AppCompatActivity{

    private TextView stepDetect;
    private TextView distance;
    private TextView odistance;
    private TextView calorie;
    private Pedometer pedometer;

    private String gender;
    private Double height;
    private Double weight;

    private Chronometer chronometer;
    private long time;
    private long startTime;

    private Double runTime;
    private int mStepDetect;
    private double miDistance;
    private double moDistance;
    private double cal = 0;
    private String UID;
    private String TAG = "Second Exercise";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);
        Intent intent = getIntent();
        UID = intent.getStringExtra("UID");
        initData();
        stepDetect = findViewById(R.id.pedometer_stepDetect);
        distance = findViewById(R.id.pedometer_distance);
        odistance = findViewById(R.id.pedometer_gps);
        calorie = findViewById(R.id.pedometer_calorie);
        chronometer = findViewById(R.id.timer);
        chronometer.setBase(SystemClock.elapsedRealtime());
        startTime = SystemClock.elapsedRealtime();
        pedometer = new Pedometer(this);
        EventBus.getDefault().register(this);

    }

    // Initial data from firestore
    private void initData(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(UID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if(document.exists()) {

                                height = Double.valueOf(document.getData().get("height").toString());
                                weight = Double.valueOf(document.getData().get("weight").toString());
                                gender = String.valueOf(document.getData().get("gender"));


                            }
                            else{
                                Log.d("error",String.valueOf(document));
                            }

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });


    }

    // Timer
    @Override
    protected void onPause() {
        time  = SystemClock.elapsedRealtime() - chronometer.getBase();//退出程序加载的时间等于第二次进入程序的时间减去退出时的时间
        chronometer.stop();
        super.onPause();
    }

    @Override
    protected void onResume() {
        chronometer.setBase(SystemClock.elapsedRealtime() - time);
        chronometer.start();
        super.onResume();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventServer(PedometerMessage event) {
        runTime = (double)(SystemClock.elapsedRealtime() - startTime); //unit:ms
        mStepDetect = event.getStepDetect();
        miDistance = getStepDistance(gender,height)*mStepDetect;
        moDistance = event.getOutDistance();
        cal = getCal(weight,runTime,miDistance, moDistance);

        stepDetect.setText(String.valueOf(mStepDetect));
        distance.setText(String.format("%.2f",miDistance) + "m");
        odistance.setText(String.format("%.2f",moDistance) + "m");
        calorie.setText(String.format("%.2f",cal) + "kcal");

    }

    // Calorie algorithm
    public double getCal(Double weight, Double time, Double miDistance, Double moDistance) {
        double cal;
        double runDistance;
        if(moDistance != 0){
            runDistance = moDistance;
        }
        else{
            runDistance = miDistance; //demo
        }
        if(runDistance > 0)
        {
            double K = 30/((time/1000/60)/(runDistance/400));
            cal = weight*(time/3600000)*K;
        }
        else{
            cal = 0;
        }
        return cal;

    }

    // Step distance based on gender and height
    public static Double getStepDistance(String gender, Double height){
        Double stepDistance = 0.0;
        if(gender != null && gender.length() > 0 && gender.equals("MALE")){
            stepDistance = 0.415*height;
        }
        else{
            stepDistance = 0.413*height;
        }
        return stepDistance;
    }

    // Once finish exercising, save data
    @Override
    public void onBackPressed() {
        addData();
        super.onBackPressed();
    }

    // record exercise in firestore
    private void addData(){
        double velocity = miDistance/runTime;
        Map<String, Object> data = new HashMap<>();
        data.put("id", UID);
        data.put("step", String.valueOf(mStepDetect));
        data.put("time", String.valueOf(runTime));
        data.put("indoor Distance", String.valueOf(miDistance));
        data.put("outdoor Distance", String.valueOf(moDistance));
        data.put("calorie",String.format("%.2f",cal));
        data.put("velocity", String.valueOf(velocity));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("exercise")
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

    }

}
