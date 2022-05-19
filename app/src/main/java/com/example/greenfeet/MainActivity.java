package com.example.greenfeet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;



public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView registration,passwordReset;
    private EditText EtEmail, EtPass;
    private Button Login;

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registration = (TextView) findViewById(R.id.CreateAccount);
        registration.setOnClickListener(this);

        Login = (Button) findViewById(R.id.LoginButton);
        Login.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        passwordReset = (TextView) findViewById(R.id.ForgotPassword);
        passwordReset.setOnClickListener(this);


        EtEmail = (EditText) findViewById(R.id.EtEmail);
        EtPass = (EditText) findViewById(R.id.EtPass);
        progressBar = (ProgressBar) findViewById(R.id.progressBarMain);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.CreateAccount:
                startActivity(new Intent( this, Registration.class));
                break;

            case R.id.ForgotPassword:
                startActivity(new  Intent(this, PasswordReset.class));
                break;

            case R.id.LoginButton:
                verifyLogin();
                break;
                
        }
    }

    private void verifyLogin() {
        String userEmail = EtEmail.getText().toString().trim();
        String userPass = EtPass.getText().toString().trim();

        if(userEmail.isEmpty()){
            EtEmail.setError("Please enter your email!");
            EtEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
            EtEmail.setError("A valid email is required!");
            EtEmail.requestFocus();
            return;
        }

        if(userPass.isEmpty()){
            EtPass.setError("Please enter your password!");
            EtPass.requestFocus();
            return;
        }
        if(userPass.length()<6){
            EtPass.setError("Password must be at least 6 characters!");
            EtPass.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(userEmail, userPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser userAuth = FirebaseAuth.getInstance().getCurrentUser();

                    if(userAuth.isEmailVerified()){
                        startActivity(new Intent(MainActivity.this, UserProfile.class));
                    }
                    else{
                        userAuth.sendEmailVerification();
                        Toast toast = Toast.makeText(MainActivity.this,"Check email for verification!", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        progressBar.setVisibility(View.GONE);

                    }

                }

                else{
                    Toast toast = Toast.makeText(MainActivity.this,"Login failed! Check your login details.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    progressBar.setVisibility(View.GONE);
                }

            }
        });
    }

}