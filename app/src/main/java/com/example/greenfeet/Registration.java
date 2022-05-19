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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


public class Registration extends AppCompatActivity implements View.OnClickListener {

    private TextView banner, registration;
    public String FullName;
    private EditText EditTextFirstname, EditTextLastname, EditTextAge, EditTextEmail, EditTextPassword;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();

        banner = (TextView) findViewById(R.id.Banner);
        banner.setOnClickListener(this);

        registration = (Button) findViewById(R.id.RegistrationButton);
        registration.setOnClickListener(this);

        EditTextFirstname = (EditText)  findViewById(R.id.Fname);
        EditTextLastname = (EditText)  findViewById(R.id.Lname);
        EditTextEmail = (EditText)  findViewById(R.id.Email);
        EditTextPassword = (EditText)  findViewById(R.id.Password);
        EditTextAge  = (EditText)  findViewById(R.id.Age);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

    }

    @Override
    public void onClick(View t) {
        switch (t.getId()) {
            case R.id.Banner:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.RegistrationButton:
                userRegistration();
                break;
        }
    }

    private void userRegistration() {
        String Email = EditTextEmail.getText().toString().trim();
        String Pass = EditTextPassword.getText().toString().trim();
        String Firstname = EditTextFirstname.getText().toString().trim();
        String Lastname = EditTextLastname.getText().toString().trim();
        String Age = EditTextAge.getText().toString().trim();

        FullName = (Firstname + " " + Lastname);


         if(Age.isEmpty()){
             EditTextAge.setError("Enter your age!");
             EditTextAge.requestFocus();
             return;
         }
        if(Firstname.isEmpty()){
            EditTextFirstname.setError("Enter your first name!");
            EditTextFirstname.requestFocus();
            return;
        }

        if(Lastname.isEmpty()){
            EditTextLastname.setError("Enter your last name!");
            EditTextLastname.requestFocus();
            return;
        }

        if(Pass.isEmpty()){
            EditTextPassword.setError("Enter your password!");
            EditTextPassword.requestFocus();
            return;
        }

        if(Pass.length() < 6){
            EditTextPassword.setError("Password must be at least 6 characters!");
            EditTextPassword.requestFocus();
            return;
        }

        if(Email.isEmpty()){
            EditTextEmail.setError("Enter your email!");
            EditTextEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(Email).matches()){
            EditTextEmail.setError("Not valid email!");
            EditTextEmail.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(Email, Pass).addOnCompleteListener(new OnCompleteListener<AuthResult>(){
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    UserData user = new UserData(FullName, Age, Email );

                    FirebaseDatabase.getInstance().getReference("UserData").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast toast = Toast.makeText(Registration.this,"User registered!", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                progressBar.setVisibility(View.VISIBLE);
                            }
                            else{
                                Toast toast = Toast.makeText(Registration.this,"User registration failed!", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                progressBar.setVisibility(View.GONE);
                            }

                        }

                    });


                }
                else{
                    Toast.makeText( Registration.this, "User registration failed!", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);

                }
            }




        });






    }


}