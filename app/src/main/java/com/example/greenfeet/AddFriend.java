package com.example.greenfeet;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class AddFriend extends AppCompatActivity {
    public DrawerLayout drawLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    public ListView friendLV;
    public ArrayAdapter adapter; // store on screen friends.
    public Button AddFriend;
    public ArrayList<String> friendList;
    public EditText ETFriendID;
    public String FriendID;
    public HashMap<String, Double> userData;
    public HashMap<String, Double> Results;
    public String FriendResult;
    public String FriendName;



    private DatabaseReference fDbRef;
    private DatabaseReference UIDbRef;
    private DatabaseReference FnDbRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriend);
        Toolbar toolbar = findViewById(R.id.GreenFeetToolBar);


        NavigationView navigationView = findViewById(R.id.navi_view);
        navigationView.getMenu().findItem(R.id.nav_logout).setOnMenuItemClickListener(menuItem -> {

            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(AddFriend.this, MainActivity.class));
            return true;

        });

        navigationView.getMenu().findItem((R.id.nav_Home)).setOnMenuItemClickListener(menuItem -> {
            startActivity(new Intent(AddFriend.this, UserProfile.class));
            return true;

        });

        navigationView.getMenu().findItem((R.id.nav_MealHistory)).setOnMenuItemClickListener(menuItem -> {
            startActivity(new Intent(AddFriend.this, MealHistory.class));
            return true;

        });


        drawLayout = findViewById(R.id.DrawerLayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawLayout, R.string.nav_open, R.string.nav_close);

        drawLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true); // Make toolbar show navigation button (i.e back button with arrow icon)

        toolbar.setNavigationIcon(R.drawable.ic_menu); // Replace arrow icon with our custom icon

        friendList = new ArrayList<>();
        friendLV = findViewById(R.id.friendLV);
        AddFriend = findViewById(R.id.AddFriendbtn);
        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, friendList);
        FirebaseDatabase fDatabase = FirebaseDatabase.getInstance();
        String UserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        fDbRef = fDatabase.getReference("Friends").child(UserID);

        fDbRef.get().addOnCompleteListener(task -> {
            Results = null;
            Results = (HashMap<String, Double>) task.getResult().getValue();
            if (Results == null) {
                String Message = "Please add a friend!";
                friendList.add(Message);
            } else {
                for (Map.Entry<String, Double> entry : Results.entrySet()) {
                    FriendResult = entry.getKey();
                    FnDbRef = fDatabase.getReference("UserData").child(FriendResult).child("FullName");
                    FnDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            FriendName = Objects.requireNonNull(snapshot.getValue()).toString();
                            SetUpListView(FriendName);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });










        adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, friendList);
        AddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ETFriendID = (EditText)findViewById(R.id.EtFriendID);
                FriendID = ETFriendID.getText().toString().trim();
                if(!FriendID.isEmpty()){
                    addFriend();

                }
                else{
                    ETFriendID.setError("Enter an ID");
                    ETFriendID.requestFocus();
                    return;
                }
            }

            private void addFriend() {
                ETFriendID = (EditText)findViewById(R.id.EtFriendID);
                FriendID = ETFriendID.getText().toString().trim();
                FirebaseDatabase fDatabase = FirebaseDatabase.getInstance();
                String UserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                fDbRef = fDatabase.getReference("Friends").child(UserID);
                UIDbRef = fDatabase.getReference("UserData");
                userData = new HashMap<>();
                makeToast(userData.toString());
                UIDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild(FriendID)) {
                            fDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.hasChild(FriendID)) {
                                        makeToast("Friend already exists");

                                    }
                                    else{

                                        fDbRef = fDatabase.getReference("Friends").child(UserID).child(FriendID);
                                        DatabaseReference Emission = fDatabase.getReference("Emissions").child(FriendID);
                                        Emission.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                Double FriendCO2 = (Double) snapshot.getValue();
                                                fDbRef.setValue(FriendCO2);
                                                makeToast("Friend Added");
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            }
                        else{
                            makeToast("User doesn't exist!");
                        }


                        }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }

                    });



            }
        });

    }

    private void SetUpListView(String friendName) {
        friendList.add(friendName);
        adapter.notifyDataSetChanged();
        friendLV.setAdapter(adapter);
    }


    public Toast toast;

    private void makeToast(String s){
        if(toast != null) toast.cancel();
        toast = Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }




    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
