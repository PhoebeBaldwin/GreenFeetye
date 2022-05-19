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
import java.util.Objects;

public class Predictions extends AppCompatActivity {
    public DrawerLayout drawLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predictions);
        Toolbar toolbar = findViewById(R.id.GreenFeetToolBar);


        NavigationView navigationView = findViewById(R.id.navi_view);
        navigationView.getMenu().findItem(R.id.nav_logout).setOnMenuItemClickListener(menuItem -> {

            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(Predictions.this, MainActivity.class));
            return true;

        });
        navigationView.getMenu().findItem(R.id.nav_Meals).setOnMenuItemClickListener(MenuItem -> {

            startActivity( new Intent( Predictions.this, Meals.class));
            return true;
        });
        navigationView.getMenu().findItem(R.id.nav_Home).setOnMenuItemClickListener(MenuItem -> {

            startActivity( new Intent( Predictions.this, UserProfile.class));
            return true;
        });
        navigationView.getMenu().findItem(R.id.nav_Friends).setOnMenuItemClickListener(MenuItem -> {

            startActivity( new Intent( Predictions.this, Friends.class));
            return true;
        });
        navigationView.getMenu().findItem(R.id.nav_AddFriend).setOnMenuItemClickListener(MenuItem -> {

            startActivity( new Intent( Predictions.this, AddFriend.class));
            return true;
        });
        navigationView.getMenu().findItem(R.id.nav_MealHistory).setOnMenuItemClickListener(MenuItem -> {

            startActivity( new Intent( Predictions.this, MealHistory.class));
            return true;
        });



        drawLayout = findViewById(R.id.DrawerLayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawLayout, R.string.nav_open, R.string.nav_close);

        drawLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true); // Make toolbar show navigation button (i.e back button with arrow icon)

        toolbar.setNavigationIcon(R.drawable.ic_menu); // Replace arrow icon with our custom icon




        String UserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

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
