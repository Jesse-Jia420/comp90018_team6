package comp90018.fitness.ui.exercise;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

import comp90018.fitness.R;

public class SecondActivity_exercise extends AppCompatActivity{

    TextView stepDetect;
    TextView distance;
    TextView odistance;
    TextView calorie;
    Pedometer pedometer;

    private String gender;
    private Double height;
    private Double weight;
    private String id = "mingtiand";
    ThirdActivity_info info;

    Chronometer chronometer;
    private long time;
    private long startTime;

    Double runTime;
    int mStepDetect;
    double miDistance;
    double moDistance;
    double cal;




    private String TAG = "Second Exercise";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);
        initData();
        info = new ThirdActivity_info();
        weight = info.weight;
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

    private void initData(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("user_test")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(String.valueOf(document.getData().get("id")).equals(id)) {


                                    height = Double.valueOf(document.getData().get("height").toString());
                                    weight = Double.valueOf(document.getData().get("weight").toString());
                                    gender = String.valueOf(document.getData().get("gender"));

                                }

                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());

                        }

                    }
                });

    }

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
        mStepDetect = 10;
        miDistance = getStepDistance(gender,height)*mStepDetect;
        moDistance = event.getOutDistance();
        cal = getCal(weight,runTime,miDistance, moDistance);

        stepDetect.setText(String.valueOf(mStepDetect));
        distance.setText(String.valueOf(miDistance) + "m");
        odistance.setText(String.format("%.2f",moDistance) + "m");
        calorie.setText(String.format("%.2f",cal) + "kcal");

       // calorie.setText(String.valueOf(height.toString().trim()));
    }

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

    public static Double getStepDistance(String gender, Double height){
        Double stepDistance = 0.0;
        if(gender.equals("MALE")){
            stepDistance = 0.415*height;
        }
        else{
            stepDistance = 0.413*height;
        }
        return stepDistance;
    }

    @Override
    public void onBackPressed() {
        addData();

        super.onBackPressed();
    }

    private void addData(){
        double velocity = miDistance/runTime;
        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("step", String.valueOf(mStepDetect));
        data.put("time", String.valueOf(runTime));
        data.put("indoor Distance", String.valueOf(miDistance));
        data.put("outdoor Distance", String.valueOf(moDistance));
        data.put("calorie",String.format("%.2f",cal));
        data.put("velocity", String.valueOf(velocity));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("exercise_test")
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
