package com.example.greenfeet;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UserProfile extends AppCompatActivity {
    public DrawerLayout drawLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    public BarData Bardata;
    public TextView Welcome;
    public TextView EmissionTotalTV;
    public String WelcomeMessage;
    public String EmissionMessage;
    public Toast toast;
    public HashMap<String, Double> Matches;
    public ArrayList<Float> MealEmissions;
    public HashMap<String, Double> value;
    public HashMap<String, Map<String, Double>> Results;
    public String key;
    public Double Co2Eq;
    public ArrayList<Entry> entries;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Toolbar toolbar = findViewById(R.id.GreenFeetToolBar);
        Welcome = findViewById(R.id.WelcomeTV);
        EmissionTotalTV = findViewById(R.id.EmissionTotal);


        String UserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        FirebaseDatabase userDatabase = FirebaseDatabase.getInstance();
        DatabaseReference udDbref = userDatabase.getReference("UserData");
        DatabaseReference Emission= userDatabase.getReference("Emissions").child(UserID);
        udDbref.child(UserID).child("FullName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                WelcomeMessage =("Welcome " + snapshot.getValue());
                Welcome.setText(WelcomeMessage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });
        Emission.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                EmissionMessage = ("Your total emissions are " + snapshot.getValue() +"KG" );
                EmissionTotalTV.setText(EmissionMessage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });








        NavigationView navigationView = findViewById(R.id.navi_view);
        navigationView.getMenu().findItem(R.id.nav_logout).setOnMenuItemClickListener(menuItem -> {

                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(UserProfile.this, MainActivity.class ));
                        return true;

        });

        navigationView.getMenu().findItem(R.id.nav_Meals).setOnMenuItemClickListener(MenuItem -> {

            startActivity( new Intent( UserProfile.this, Meals.class));
            return true;
        });
        navigationView.getMenu().findItem(R.id.nav_MealHistory).setOnMenuItemClickListener(MenuItem -> {

            startActivity( new Intent( UserProfile.this, MealHistory.class));
            return true;
        });
        navigationView.getMenu().findItem(R.id.nav_Friends).setOnMenuItemClickListener(MenuItem -> {

            startActivity( new Intent( UserProfile.this, Friends.class));
            return true;
        });

        navigationView.getMenu().findItem(R.id.nav_Prediction).setOnMenuItemClickListener(MenuItem -> {

            startActivity( new Intent( UserProfile.this, Predictions.class));
            return true;
        });
        navigationView.getMenu().findItem(R.id.nav_AddFriend).setOnMenuItemClickListener(MenuItem -> {

            startActivity( new Intent( UserProfile.this, AddFriend.class));
            return true;
        });



        drawLayout = findViewById(R.id.DrawerLayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawLayout, R.string.nav_open, R.string.nav_close);

        drawLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true); // Make toolbar show navigation button (i.e back button with arrow icon)

        toolbar.setNavigationIcon(R.drawable.ic_menu); // Replace arrow icon with our custom icon

        BarChart chart = findViewById(R.id.barChart);
        chart.setTouchEnabled(true);
        chart.setPinchZoom(true);

        List<String> xAxisLabel = new ArrayList<>(Arrays.asList("Mon", "Tue", "Wed","Thu","Fri", "Sat", "Sun"));
        List<BarEntry> GraphData = new ArrayList<>();
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setTextSize(10);
        xAxis.setTextColor(ContextCompat.getColor(this, R.color.black));
        xAxis.setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(xAxisLabel));
        xAxis.setLabelCount(7);

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        switch(c.get(Calendar.DAY_OF_WEEK)){
            case Calendar.MONDAY:
                c.add(Calendar.DATE, -1);
                break;

            case Calendar.TUESDAY:
                c.add(Calendar.DATE,-2);
                break;

            case Calendar.WEDNESDAY:
                c.add(Calendar.DATE,-3);
                break;

            case Calendar.THURSDAY:
                c.add(Calendar.DATE,-4);
                break;

            case Calendar.FRIDAY:
                c.add(Calendar.DATE,-5);
                break;

            case Calendar.SATURDAY:
                c.add(Calendar.DATE,-6);
                break;

            case Calendar.SUNDAY:
                c.add(Calendar.DATE,-7);
                break;
        }
        ArrayList<Date> output = new ArrayList<>();

        ArrayList<String> Dates = new ArrayList<>();

        for(int x = 0; x<6; x++){
            c.add(Calendar.DATE,1);
            output.add(c.getTime());
        }
        for(int i = 0; i<output.toArray().length; i++){
            LocalDate temp = (output.get(i)).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            Dates.add(temp.toString());

        }


        DatabaseReference mhDbRef = userDatabase.getReference("MealHistory").child(UserID);
        MealEmissions= new ArrayList<>();
        Matches = new HashMap<>();
        entries = new ArrayList<>();
        Co2Eq = 0.0;



        mhDbRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Results = null;
                Results = (HashMap<String, Map<String, Double>>) task.getResult().getValue();
                if(Results == null){
                    GraphData.add(new BarEntry(1, 1));
                }
                else{
                    for (int x = 0; x < Dates.size(); x++) {
                        for (Map.Entry<String, Map<String, Double>> entry : Results.entrySet()) {
                            key = entry.getKey();
                            value = (HashMap<String, Double>) entry.getValue();
                            String TempDay = Dates.get(x);
                            if (key.equals(TempDay)) {
                                for (Map.Entry<String, Double> entry2 : value.entrySet()) {
                                    Double value2 = entry2.getValue();
                                    Co2Eq = Co2Eq + value2;


                                }

                                float Data = Co2Eq.floatValue();
                                GraphData.add( new BarEntry(x, Data));

                                BarDataSet BarDataSet = new BarDataSet(GraphData, "CO2eq Emission");


                                BarDataSet.setColors(ColorTemplate.PASTEL_COLORS);
                                Bardata = new BarData(BarDataSet);
                                chart.setData(Bardata);
                                chart.setVisibleXRangeMaximum(7);
                                chart.invalidate();


                            }
                        }


                        }
                    }

                }



            });









    }




    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void makeToast(String s){
        if(toast != null) toast.cancel();
        toast = Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }







}