package com.example.greenfeet;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Friends extends AppCompatActivity {
    public FirebaseDatabase db = FirebaseDatabase.getInstance();
    public DrawerLayout drawLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    public ListView EmissionList;
    public String key;
    public ArrayList<String> Emissions;
    public ArrayAdapter<String> adapter; // store on screen friends
    public HashMap<String, Double> Results;
    public String FriendName;
    public String FriendEmission;
    public ArrayList<String> ResultsNames;
    public ArrayList<Double> value;
    public int count;


    private DatabaseReference FnDbRef;




    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        Toolbar toolbar = findViewById(R.id.GreenFeetToolBar);


        NavigationView navigationView = findViewById(R.id.navi_view);
        navigationView.getMenu().findItem(R.id.nav_logout).setOnMenuItemClickListener(menuItem -> {

            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(Friends.this, MainActivity.class));
            return true;

        });
        navigationView.getMenu().findItem(R.id.nav_Meals).setOnMenuItemClickListener(MenuItem -> {

            startActivity(new Intent(Friends.this, Meals.class));
            return true;
        });
        navigationView.getMenu().findItem(R.id.nav_Home).setOnMenuItemClickListener(MenuItem -> {

            startActivity(new Intent(Friends.this, UserProfile.class));
            return true;
        });
        navigationView.getMenu().findItem(R.id.nav_AddFriend).setOnMenuItemClickListener(MenuItem -> {

            startActivity(new Intent(Friends.this, AddFriend.class));
            return true;
        });
        navigationView.getMenu().findItem(R.id.nav_Prediction).setOnMenuItemClickListener(MenuItem -> {

            startActivity(new Intent(Friends.this, Predictions.class));
            return true;
        });
        navigationView.getMenu().findItem(R.id.nav_MealHistory).setOnMenuItemClickListener(MenuItem -> {

            startActivity(new Intent(Friends.this, MealHistory.class));
            return true;
        });



        drawLayout = findViewById(R.id.DrawerLayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawLayout, R.string.nav_open, R.string.nav_close);

        drawLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true); // Make toolbar show navigation button (i.e back button with arrow icon)

        toolbar.setNavigationIcon(R.drawable.ic_menu); // Replace arrow icon with our custom icon


        EmissionList = findViewById(R.id.EmissionLV);
        Emissions = new ArrayList<>();
        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, Emissions);

        String UserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference mhDbRef = db.getReference("Friends").child(UserID);
        Results = new HashMap<>();
        ResultsNames = new ArrayList<>();
        count = 0;

        mhDbRef.get().addOnCompleteListener(task -> {
            Results = null;
            Results = (HashMap<String, Double>) task.getResult().getValue();
            if (Results == null) {
                String Message = "Please add a friend!";
                Emissions.add(Message);
            }
            else{
            for (Map.Entry<String,Double> entry: Results.entrySet()) {
                key = entry.getKey();
                value = new ArrayList<>(Results.values());
                FnDbRef = db.getReference("UserData").child(key).child("FullName");
                FnDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        FriendName = Objects.requireNonNull(snapshot.getValue()).toString();
                        FriendEmission = ((count+1) +". " + FriendName + " " + value.get(count) + "KG");
                        SetUpListView(FriendEmission);
                        count++;


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }

                });

            }


            }


        });


    }

    private void SetUpListView(String friendEmission) {
        Emissions.add(friendEmission);
        adapter.notifyDataSetChanged();
        EmissionList.setAdapter(adapter);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public Toast toast;

    private void makeToast(String s){
        if(toast != null) toast.cancel();
        toast = Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

}
