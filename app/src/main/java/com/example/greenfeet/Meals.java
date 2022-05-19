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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Meals extends AppCompatActivity {
    public DrawerLayout drawLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    public ListView listView;
    public HashMap<String, Double> food;
    public  String foodAmount;
    public ArrayList<String> foodChoice;
    public ArrayAdapter adapter; // store on screen food items.
    public ArrayAdapter<String> fAdapter;
    public Spinner spinner;
    public EditText Quantity;
    public Button AddFood;
    public Button AddMeal;
    public String SelectedItem;
    public String amount;
    public Double amountNumber;
    public EditText mealName;
    public String mealname;
    public String key;
    public LocalDate today;
    public Double values;
    public Double Co2eq;
    public Double Co2eqTemp;

    public ArrayList<String> MealList;

    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final DocumentReference foodRef = db.collection("FoodEmissions").document("Food");
    private DatabaseReference fDbRef;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meals);
        Toolbar toolbar = findViewById(R.id.GreenFeetToolBar);




        NavigationView navigationView = findViewById(R.id.navi_view);
        navigationView.getMenu().findItem(R.id.nav_logout).setOnMenuItemClickListener(menuItem -> {

            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(Meals.this, MainActivity.class));
            return true;

        });

        navigationView.getMenu().findItem((R.id.nav_Home)).setOnMenuItemClickListener(menuItem -> {
            startActivity( new Intent( Meals.this, UserProfile.class));
            return true;

        });

        navigationView.getMenu().findItem((R.id.nav_MealHistory)).setOnMenuItemClickListener(menuItem -> {
            startActivity( new Intent( Meals.this, MealHistory.class));
            return true;

        });


        drawLayout = findViewById(R.id.DrawerLayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawLayout, R.string.nav_open, R.string.nav_close);

        drawLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true); // Make toolbar show navigation button (i.e back button with arrow icon)

        toolbar.setNavigationIcon(R.drawable.ic_menu); // Replace arrow icon with our custom icon

        MealList = new ArrayList<>();
        listView = findViewById(R.id.mealLV);
        food = new HashMap<>();
        foodChoice = new ArrayList<>();
        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, MealList);
        spinner = findViewById(R.id.spinner3);
        fAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, foodChoice);
        fAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(fAdapter);
        foodRef.get().addOnCompleteListener(task -> {
           if(task.isSuccessful()){
               Map<String, Object> map = task.getResult().getData();

               for(Map.Entry<String, Object> entry : map.entrySet()){
                   foodChoice.add(entry.getKey());
               }
               fAdapter.notifyDataSetChanged();
           }
        });
        Quantity = findViewById(R.id.EtQuantity);
        AddFood = findViewById(R.id.AddFoodbtn);
        AddFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                SelectedItem = spinner.getSelectedItem().toString();

                amount = Quantity.getText().toString().trim();

                if((!amount.isEmpty()) && (!SelectedItem.isEmpty())){
                    addItem();

                }
                else{
                    Quantity.setError("Please Select a food item and a quantity");
                    Quantity.requestFocus();
                }






                }

            private void addItem() {

                Quantity = findViewById(R.id.EtQuantity);
                AddFood = findViewById(R.id.AddFoodbtn);
                amount = Quantity.getText().toString().trim();
                amountNumber = Double.parseDouble(amount);
                SelectedItem = spinner.getSelectedItem().toString();
                food.put(SelectedItem, amountNumber);


                for(Map.Entry<String, Double> entry: food.entrySet()){
                    key = entry.getKey();
                    values = entry.getValue() ;

                    if(!MealList.contains(key + "\t" + values + "g")) {
                        MealList.add(key + "\t" + values + "g");
                    }




                }

                adapter.notifyDataSetChanged();
                listView.setAdapter(adapter);




            }



        });

        mealName = findViewById(R.id.mealName);
        AddMeal = findViewById(R.id.SubmitMeal);
        AddMeal.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                addMeal();
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            private void addMeal() {
                Co2eq = 0.0;
                mealname =  mealName.getText().toString();
                if(mealname.isEmpty()){
                    mealName.setError("Please name your meal!");
                    mealName.requestFocus();
                    return;
                }

                String UserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseDatabase fDatabase = FirebaseDatabase.getInstance();
                fDbRef = fDatabase.getReference("MealLists");

                for(Map.Entry<String,Double> entry: food.entrySet()){

                    key = entry.getKey();
                    values = entry.getValue();

                    foodAmount = values.toString();


                    fDbRef.child(UserID).child(mealname).child(key).setValue(values);

                    foodRef.get().addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            Map<String, Object> map = task.getResult().getData();
                            for(Map.Entry<String, Object> entry1 : map.entrySet()){
                                if(entry1.getKey().equals(key)) {
                                    Double co2 = Double.parseDouble(entry1.getValue().toString());
                                    Co2eqTemp = (co2 * values)/1000 ; //co2eq was calculated based on per kg of food so need to divide by 1000.
                                }


                            }
                            Co2eq = Co2eq + Co2eqTemp;
                            storeMeal( Co2eq);
                       }
                    });


                }
            }
        });



        listView.setOnItemLongClickListener((adapterView, view, i, l) -> {
            makeToast("Removed: " + MealList.get(i));
            removeItem(i);
            return false;
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void storeMeal(Double co2eq) {
        String UserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase fDatabase = FirebaseDatabase.getInstance();
        DatabaseReference feDBref = fDatabase.getReference("MealEmissions");
        mealname =  mealName.getText().toString();
        makeToast("Meal Added");
        feDBref.child(UserID).child(mealname).setValue(co2eq);

        today= LocalDate.now();

        DatabaseReference mealHistory = fDatabase.getReference("MealHistory");
        mealHistory.child(UserID).child(today.toString()).child(mealname).setValue(co2eq);
        MealList.clear();

        setEmissions(co2eq);

    }

    private void setEmissions(Double co2eq) {

        String UserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase fDatabase = FirebaseDatabase.getInstance();
        DatabaseReference feDBref = fDatabase.getReference("Emissions").child(UserID);
        feDBref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue()==null){
                    feDBref.setValue(co2eq);
                }
                else{
                    Double Co2;
                    Co2 = (Double) snapshot.getValue();
                    Co2 = Co2+Co2eq;
                    feDBref.setValue(Co2);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public void removeItem(int remove) {
        MealList.remove(remove);
        listView.setAdapter(adapter);

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
