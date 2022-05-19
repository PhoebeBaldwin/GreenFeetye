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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MealHistory extends AppCompatActivity {
    public DrawerLayout drawLayout;
    public ListView MealList;
    public ArrayList<String> Meals;
    public String key;
    public ArrayAdapter<String> adapter; // store on screen food items.
    public LocalDate today;
    public DayOfWeek weekDay;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    public FirebaseDatabase db = FirebaseDatabase.getInstance();
    public HashMap<String, Map<String, Double>> Results;
    public HashMap<String, Double> Matches;
    public HashMap<String, Double> value;


    private DatabaseReference MhDbRef;



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mealhistory);
        Toolbar toolbar = findViewById(R.id.GreenFeetToolBar);


        NavigationView navigationView = findViewById(R.id.navi_view);
        navigationView.getMenu().findItem(R.id.nav_logout).setOnMenuItemClickListener(menuItem -> {

            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MealHistory.this, MainActivity.class));
            return true;

        });
        navigationView.getMenu().findItem(R.id.nav_Meals).setOnMenuItemClickListener(MenuItem -> {

            startActivity( new Intent( MealHistory.this, Meals.class));
            return true;
        });
        navigationView.getMenu().findItem(R.id.nav_Home).setOnMenuItemClickListener(MenuItem -> {

            startActivity( new Intent( MealHistory.this, UserProfile.class));
            return true;
        });
        navigationView.getMenu().findItem(R.id.nav_Prediction).setOnMenuItemClickListener(MenuItem -> {

            startActivity( new Intent( MealHistory.this, Predictions.class));
            return true;
        });
        navigationView.getMenu().findItem(R.id.nav_AddFriend).setOnMenuItemClickListener(MenuItem -> {

            startActivity( new Intent( MealHistory.this, AddFriend.class));
            return true;
        });
        navigationView.getMenu().findItem(R.id.nav_Friends).setOnMenuItemClickListener(MenuItem -> {

            startActivity( new Intent( MealHistory.this, Friends.class));
            return true;
        });


        drawLayout = findViewById(R.id.DrawerLayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawLayout, R.string.nav_open, R.string.nav_close);

        drawLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true); // Make toolbar show navigation button (i.e back button with arrow icon)

        toolbar.setNavigationIcon(R.drawable.ic_menu); // Replace arrow icon with our custom icon

       today= LocalDate.now();
       weekDay = today.getDayOfWeek();
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        switch(c.get(Calendar.DAY_OF_WEEK)){
           case Calendar.MONDAY:
               break;

           case Calendar.TUESDAY:
               c.add(Calendar.DATE,-1);
               break;

           case Calendar.WEDNESDAY:
               c.add(Calendar.DATE,-2);
               break;

           case Calendar.THURSDAY:
               c.add(Calendar.DATE,-3);
               break;

           case Calendar.FRIDAY:
               c.add(Calendar.DATE,-4);
               break;

           case Calendar.SATURDAY:
               c.add(Calendar.DATE,-5);
               break;

           case Calendar.SUNDAY:
               c.add(Calendar.DATE,-6);
               break;
       }
        ArrayList<Date> output = new ArrayList<>();
        ArrayList<String> Dates = new ArrayList<>();

        LocalDate Temp;
       output.add(c.getTime());
       for(int x = 0; x<6; x++){
           c.add(Calendar.DATE,1);
           output.add(c.getTime());
       }
        for(int i = 0; i<output.toArray().length; i++){
            Temp = (output.get(i)).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            Dates.add(Temp.toString());

        }





        String UserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        MhDbRef = db.getReference("MealHistory").child(UserID);

        Meals = new ArrayList<>();
        MealList = findViewById(R.id.MealHistoryLV);
        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, Meals);

        Matches = new HashMap<>();
            MhDbRef.get().addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    Results = null;
                    Results = (HashMap<String, Map<String, Double>>) task.getResult().getValue();
                    if (Results == null) {
                        String Message = "Please add a meal!";
                        Meals.add(Message);

                    } else {
                        for (int y = 0; y < Dates.size(); y++) {
                            for (Map.Entry<String, Map<String, Double>> entry : Results.entrySet()) {
                                key = entry.getKey();
                                value = (HashMap<String, Double>) entry.getValue();
                                String TempDay = Dates.get(y);

                                if (key.equals(TempDay)) {
                                    for (Map.Entry<String, Double> entry2 : value.entrySet()) {
                                        String key2 = entry2.getKey();
                                        Double value2 = entry2.getValue();
                                        if (!Meals.contains(key2)) {
                                            Matches.put(key2, value2);
                                            Meals.add(key2 + " " + TempDay);
                                            makeToast(Meals.toString());
                                        }


                                    }
                                }

                            }
                        }


                    }
                }
                adapter.notifyDataSetChanged();

            });
        MealList.setAdapter(adapter);

        ;
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




