package comp90018.fitness.ui.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import comp90018.fitness.R;

public class RegisterActivity extends AppCompatActivity {
    EditText inputFullName, inputEmail, inputPassword, inputConfirmPassword, inputGender, inputHeight, inputWeight;
    Button RegisterBtn;
    TextView alreadyHaveAccount;
    FirebaseAuth fAuth;
    FirebaseUser myUser;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;
    String DEFAULT_AVATAR_URL_M = "https://www.clipartmax.com/png/full/405-4050774_avatar-icon-flat-icon-shop-download-free-icons-for-avatar-icon-flat.png";
    String DEFAULT_AVATAR_URL_F = "https://www.clipartmax.com/png/small/319-3191408_female-avatar-illustration.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        inputFullName = findViewById(R.id.inputFullName);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);
        inputGender = findViewById(R.id.inputGender);
        inputHeight = findViewById(R.id.inputHeight);
        inputWeight = findViewById(R.id.inputWeight);
        RegisterBtn = findViewById(R.id.registerBtn);

        //get information from firebase
        fAuth = FirebaseAuth.getInstance();
        myUser =fAuth.getCurrentUser();
        progressDialog = new ProgressDialog(this);

        alreadyHaveAccount = findViewById(R.id.alreadyHaveAccount);
        alreadyHaveAccount.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(RegisterActivity.this, loginFrame.class));
            }
        });

        RegisterBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                PerformAuth();
            }
        });


    }

    private void PerformAuth() {
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        String confirmPassword =inputConfirmPassword.getText().toString();

        if (!email.matches(emailPattern)){
            inputEmail.setError("Please Enter correct Email Address.");
        }else if(password.isEmpty()|| password.length() < 6){
            inputPassword.setError("Please enter password that more than 6 characters");
        }else if(!password.equals(confirmPassword)){
            inputConfirmPassword.setError("The password doesn't match");
        }else {
            progressDialog.setMessage("Please Wait...");
            progressDialog.setTitle("Registration");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        Map<String, Object> user = new HashMap<>();
                        ArrayList<Map<String, Object>> friends = new ArrayList<>();
                        user.put("friends", friends);
                        user.put("name", inputFullName.getText().toString());
                        user.put("gender", inputGender.getText().toString());
                        user.put("height", inputHeight.getText().toString());
                        user.put("weight", inputWeight.getText().toString());
                        if (inputGender.getText().toString().contains("f") || inputGender.getText().toString().contains("F")){
                            user.put("avatarUrl", DEFAULT_AVATAR_URL_F);
                        }else {
                            user.put("avatarUrl", DEFAULT_AVATAR_URL_M);
                        }
                        db.collection("users").document(fAuth.getUid()).set(user);

                        progressDialog.dismiss();
                        sendUserToNextActivity();
                        Toast.makeText(RegisterActivity.this,"Login Successful",Toast.LENGTH_SHORT).show();
                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this,""+ task.getException(),Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendUserToNextActivity() {
        Intent intent= new Intent(RegisterActivity.this, loginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
