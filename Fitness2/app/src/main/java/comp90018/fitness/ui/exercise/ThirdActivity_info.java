package comp90018.fitness.ui.exercise;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import comp90018.fitness.R;

public class ThirdActivity_info extends AppCompatActivity implements View.OnClickListener{
    private TextView info_gender;
    private TextView info_height;
    private TextView info_weight;
    private String gender;
    private Double height;
    private Double weight;
    private EditText edit_gender;
    private EditText edit_height;
    private EditText edit_weight;
    private Button button_info;
    private String TAG = "Third Activity";
    private String id = "mingtiand";
    private String UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        Intent intent = getIntent();
        UID = intent.getStringExtra("UID");
        getId();
        initData();

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
                        try {
                            info_gender.setText(gender);
                            info_height.setText(height.toString() + "m");
                            info_weight.setText(weight.toString() + "kg");
                        } catch (Exception e) {
                            Log.d(TAG, "Please enter valid information!");
                        }
                    }
                });

    }

    // Get id from layout
    private void getId(){
        info_gender = findViewById(R.id.info_gender);
        info_height = findViewById(R.id.info_height);
        info_weight = findViewById(R.id.info_weight);

        edit_gender = findViewById(R.id.edit_gender);
        edit_height = findViewById(R.id.edit_height);
        edit_weight = findViewById(R.id.edit_weight);

        button_info = findViewById(R.id.button_info);
        button_info.setOnClickListener(this);
    }

    // Save information on screen and in firestore
    public void onClick(View view){
        switch (view.getId()){
            case R.id.button_info:
                try {
                    gender = edit_gender.getText().toString();
                    height = Double.valueOf(edit_height.getText().toString());
                    weight = Double.valueOf(edit_weight.getText().toString());
                    info_gender.setText(gender);
                    info_height.setText(height.toString().trim() + "m");
                    info_weight.setText(weight.toString().trim() + "kg");
                } catch (Exception e){
                    Log.d(TAG, "Please enter valid information!");
            }


        }


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference washingtonRef = db.collection("users").document(UID);
        washingtonRef
                .update("height", height,"weight",weight,"gender",gender)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }

}




