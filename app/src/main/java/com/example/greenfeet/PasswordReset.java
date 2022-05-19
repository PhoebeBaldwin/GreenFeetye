package com.example.greenfeet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordReset extends AppCompatActivity {

    private TextView banner;
    private EditText EtEmailReset;
    private Button PasswordResetBtn;
    private ProgressBar progressBarReset;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        EtEmailReset = (EditText)  findViewById(R.id.EtRememberEmail);
        PasswordResetBtn = (Button) findViewById(R.id.PasswordResetBtn);
        progressBarReset = (ProgressBar) findViewById(R.id.progressBarReset);
        banner = (TextView) findViewById(R.id.BannerReset);
        banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnHome();
            }

            private void returnHome() {
                startActivity(new Intent(PasswordReset.this, MainActivity.class));

            }
        });

        auth= FirebaseAuth.getInstance();

        PasswordResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }

            private void resetPassword() {
                String email = EtEmailReset.getText().toString().trim();

                if(email.isEmpty()){
                    EtEmailReset.setError("Please enter your email!");
                    EtEmailReset.requestFocus();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    EtEmailReset.setError("Please enter a correct email address!");
                    EtEmailReset.requestFocus();
                    return;
                }

                progressBarReset.setVisibility(View.VISIBLE);
                auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast toast = Toast.makeText(PasswordReset.this,"Check your email!", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            PasswordResetBtn.setVisibility(View.GONE);

                        }
                        else{
                            Toast toast = Toast.makeText(PasswordReset.this,"Something went wrong! Try again.", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            PasswordResetBtn.setVisibility(View.GONE);


                        }
                    }
                });
            }
        });

    }
}