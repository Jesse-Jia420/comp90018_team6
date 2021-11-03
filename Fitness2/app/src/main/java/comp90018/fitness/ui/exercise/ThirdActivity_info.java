package comp90018.fitness.ui.exercise;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import comp90018.fitness.R;

public class ThirdActivity_info extends AppCompatActivity implements View.OnClickListener{
    TextView info_gender;
    TextView info_height;
    TextView info_weight;
    String gender = "MALE";
    Double height = 180.00;
    Double weight = 80.00;
    private EditText edit_gender;
    private EditText edit_height;
    private EditText edit_weight;
    private Button button_info;
    private String TAG = "Third Activity";
    private String id = "mingtiand";
    private String documentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        getId();
        initData();

    }


    private void initData(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //documentID = "Iz6Xecv5HW5hLjzUhfLP";
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
                                    documentID = String.valueOf(document.getId());

                                }

                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                            info_height.setText("cmb");
                        }
                        info_gender.setText(gender);
                        info_height.setText(height.toString() + "m");
                        info_weight.setText(weight.toString() + "kg");
                    }
                });

    }






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

    public void onClick(View view){
        switch (view.getId()){
            case R.id.button_info:
                gender = edit_gender.getText().toString();
                height = Double.valueOf(edit_height.getText().toString());
                weight = Double.valueOf(edit_weight.getText().toString());
                info_gender.setText(gender);
                info_height.setText(height.toString().trim() + "m");
                info_weight.setText(weight.toString().trim() + "kg");

        }


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference washingtonRef = db.collection("user_test").document(documentID);
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




