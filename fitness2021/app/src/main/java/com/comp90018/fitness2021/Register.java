package com.comp90018.fitness2021;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {
    EditText myFullName, myEmail, myPassword, myConfirmPassword, myGender, myHeight, myWeight;
    Button mySignUpBtn;
    TextView myLoginBtn;
    FirebaseAuth fAuth;
    FirebaseUser myUser;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        myFullName = findViewById(R.id.fullName);
        myEmail = findViewById(R.id.email);
        myPassword = findViewById(R.id.password);
        myConfirmPassword = findViewById(R.id.confirmPassword);
        myGender = findViewById(R.id.gender);
        myHeight = findViewById(R.id.height);
        myWeight = findViewById(R.id.weight);
        mySignUpBtn = findViewById(R.id.signUpBtn);

        //get information from firebase
        fAuth = FirebaseAuth.getInstance();
        myUser =fAuth.getCurrentUser();
        progressDialog = new ProgressDialog(this);
        
        myLoginBtn = findViewById(R.id.existMember);

        myLoginBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(Register.this,login.class));
            }
        });

        mySignUpBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                PerforAuth();
            }
        });


    }

    private void PerforAuth() {
        String email = myEmail.getText().toString();
        String password = myPassword.getText().toString();
        String confirmPassword =myConfirmPassword.getText().toString();

        if (!email.matches(emailPattern)){
            myEmail.setError("Please Enter correct Email Address.");
        }else if(password.isEmpty()|| password.length() < 6){
            myPassword.setError("Please enter password that more than 6 characters");
        }else if(!password.equals(confirmPassword)){
            myConfirmPassword.setError("The password doesn't match");
        }else {
            progressDialog.setMessage("Please Wait...");
            progressDialog.setTitle("Registered");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        progressDialog.dismiss();
                        sendUserToNextActivity();
                        Toast.makeText(Register.this,"Login Successful",Toast.LENGTH_SHORT).show();
                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(Register.this,""+ task.getException(),Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendUserToNextActivity() {
        Intent intent= new Intent(Register.this,login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
