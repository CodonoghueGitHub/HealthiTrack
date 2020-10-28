package Task2_17001430.healthitrack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;


import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Objects;

public class Dashboard extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener
{
    Toolbar toolbar;
    FirebaseAuth Auth;
    FirebaseFirestore fStore;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggleOnAndOff;
    NavigationView navigationView;                                       //Declare all GUI elements
    FloatingActionButton mainBtn,secondBtn,thirdBtn;
    Float translationY = 100f;
    Boolean isFabMenu = false;
    OvershootInterpolator interpolator = new OvershootInterpolator();
    TextView calView,weightDisplay,weightUnit,showBMI;
    double totalCal;
    double BMI;
    long UnitId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        navigationView = findViewById(R.id.nav_view);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);             //Link GUI Elements to variables
        Auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        calView = findViewById(R.id.calorieView);
        weightDisplay = findViewById(R.id.showCurrentWeight);
        weightUnit = findViewById(R.id.showUnit);
        showBMI = findViewById(R.id.showBMI);

        toolbar = findViewById(R.id.nav_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);                  //Create Action bar
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerLayout = findViewById(R.id.navDrawer);

        toggleOnAndOff = new ActionBarDrawerToggle(this,drawerLayout,toolbar,(R.string.drawerOpen),(R.string.closeDrawer));     //Menu drawer show and hide

        drawerLayout.addDrawerListener(toggleOnAndOff);
        toggleOnAndOff.syncState();
        fabMenu();
        CalculateCalories();
        CalculateBMI(getUnitId());
        showProgress();
    }

    private void fabMenu()
    {
        mainBtn = findViewById(R.id.fabMain);
        secondBtn = findViewById(R.id.fabOne);
        thirdBtn = findViewById(R.id.fabTwo);

        secondBtn.setAlpha(0f);
        thirdBtn.setAlpha(0f);

        secondBtn.setTranslationY(translationY);
        thirdBtn.setTranslationY(translationY);

        mainBtn.setOnClickListener(this);
        secondBtn.setOnClickListener(this);
        thirdBtn.setOnClickListener(this);
    }

    private void openMenu()
    {
        isFabMenu = !isFabMenu;

        mainBtn.animate().setInterpolator(interpolator).rotation(45f).setDuration(300).start();
        secondBtn.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        thirdBtn.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
    }

    private void closeMenu()
    {
        mainBtn.animate().setInterpolator(interpolator).rotation(0f).setDuration(300).start();
        isFabMenu = !isFabMenu;
        secondBtn.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        thirdBtn.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case (R.id.fabMain):
                if(isFabMenu)
                {
                    closeMenu();
                }
                else
                    openMenu();
                break;
            case (R.id.fabTwo):
                Intent in4 = new Intent(Dashboard.this,User_Weight.class);
                startActivity(in4);
                finish();
                break;
            case (R.id.fabOne):
                Intent in3 = new Intent(Dashboard.this,Add_FoodPg.class);
                startActivity(in3);
                finish();
                break;


        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
    {
        switch (menuItem.getItemId())
        {
            case R.id.nav_profile:
                Intent in = new Intent(Dashboard.this,profilePage.class);
                startActivity(in);
                finish();
                break;
            case R.id.nav_Settings:
                Intent in1 = new Intent(Dashboard.this,SettingsPage.class);
                startActivity(in1);
                finish();
                break;
            case R.id.Weight:
                Intent in2 = new Intent(Dashboard.this,View_Weight.class);
                startActivity(in2);
                finish();
                break;
            case R.id.ViewImages:
                Intent in3 = new Intent(Dashboard.this,viewImages.class);
                startActivity(in3);
                finish();
                break;
            case R.id.viewMeals:
                Intent in4 = new Intent(Dashboard.this,ViewMeals.class);
                startActivity(in4);
                finish();

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else
            {
            super.onBackPressed();
            }
    }

    private void CalculateCalories()
    {
        String uId;
        String date;
        Calendar c = Calendar.getInstance();
        date = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());                                                              //Get the current date to show relevant data on the dashboard
        uId = Objects.requireNonNull(Auth.getCurrentUser()).getUid();
        DocumentReference breakfast =  fStore.collection("User_Diets").document(uId).collection("Breakfast").document(date);
        DocumentReference lunch =  fStore.collection("User_Diets").document(uId).collection("Lunch").document(date);
        DocumentReference dinner =  fStore.collection("User_Diets").document(uId).collection("Dinner").document(date);
        DocumentReference snacks =  fStore.collection("User_Diets").document(uId).collection("Snack").document(date);

       breakfast.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if(Objects.requireNonNull(task.getResult()).exists()) {
                    String cal1 = Objects.requireNonNull(Objects.requireNonNull(task.getResult()).get("Meal 1 Calories")).toString();
                    String cal2 = Objects.requireNonNull(Objects.requireNonNull(task.getResult()).get("Meal 2 Calories")).toString();
                    String cal3 = Objects.requireNonNull(Objects.requireNonNull(task.getResult()).get("Meal 3 Calories")).toString();

                    double add1 = Double.parseDouble(cal1);
                    double add2 = Double.parseDouble(cal2);
                    double add3 = Double.parseDouble(cal3);

                    totalCal = totalCal + (add1 + add2 + add3);
                }
            }
        });
        lunch.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if(Objects.requireNonNull(task.getResult()).exists()) {
                    double cal1 = (double) Objects.requireNonNull(task.getResult()).get("Meal 1 Calories");
                    double cal2 = (double) Objects.requireNonNull(task.getResult()).get("Meal 2 Calories");
                    double cal3 = (double) Objects.requireNonNull(task.getResult()).get("Meal 3 Calories");

                    totalCal = totalCal + cal1 + cal2 + cal3;
                }
            }
        });
        dinner.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
               if(Objects.requireNonNull(task.getResult()).exists())
               {
                   double cal1 = (double) Objects.requireNonNull(task.getResult()).get("Meal 1 Calories");
                   double cal2 = (double) Objects.requireNonNull(task.getResult()).get("Meal 2 Calories");
                   double cal3 = (double) Objects.requireNonNull(task.getResult()).get("Meal 3 Calories");

                   totalCal = totalCal + (cal1 + cal2 + cal3);
               }
            }
        });
        snacks.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if(Objects.requireNonNull(task.getResult()).exists()) {
                    double cal1 = (double) Objects.requireNonNull(task.getResult()).get("Meal 1 Calories");
                    double cal2 = (double) Objects.requireNonNull(task.getResult()).get("Meal 2 Calories");
                    double cal3 = (double) Objects.requireNonNull(task.getResult()).get("Meal 3 Calories");
                    totalCal = totalCal + (cal1 + cal2 + cal3);
                }
            }
        });
    }

    public void CalculateBMI(double i)
    {
        String uId;
        uId = Objects.requireNonNull(Auth.getCurrentUser()).getUid();
        DocumentReference docR = fStore.collection("users").document(uId);

        if(i == 0)
        {
            docR.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task)
                {
                        double weight, height;
                        weight = Double.parseDouble(Objects.requireNonNull(Objects.requireNonNull(task.getResult()).get("Current_Weight")).toString());
                        height = Double.parseDouble(Objects.requireNonNull(Objects.requireNonNull(task.getResult()).get("Height")).toString());

                        double adjHeight = height / 100;                                                      //For BMI Height needs to be in meters

                        adjHeight = adjHeight * adjHeight;                                                   // Square the height for BMI formula
                        BMI = (weight / adjHeight);

                }
            });
        }
        else
        {           //For Imperial
              docR.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
              {
                  @Override
                  public void onComplete(@NonNull Task<DocumentSnapshot> task)
                  {
                      double weight,height;
                      weight = Double.parseDouble(Objects.requireNonNull(Objects.requireNonNull(task.getResult()).get("Current_Weight")).toString());
                      height = Double.parseDouble(Objects.requireNonNull(Objects.requireNonNull(task.getResult()).get("Height")).toString());

                      double adjHeight = (height * 30.48d);
                      adjHeight =  adjHeight/100d;                                                                                     //To get to metric for the BMI formula
                      double adjWeight =(weight/2.205d);
                      adjHeight = adjHeight * adjHeight;
                      BMI = (adjWeight/adjHeight);                                                           //BMI measured in kg/m squared, therefore pounds/inches squared converted to kg/m squared
                  }
              });
        }
    }

    private void showProgress()
    {
        String uId;
        uId = Objects.requireNonNull(Auth.getCurrentUser()).getUid();
        DocumentReference docR = fStore.collection("users").document(uId);

        docR.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if(Objects.requireNonNull(task.getResult()).exists())
                {
                    String displayString1 = totalCal + " / " + Objects.requireNonNull(task.getResult().get("Target_Calorie_Intake")).toString();    //Displays the number of calories recorded in the day
                    calView.setText(displayString1);

                    String displayString2 = Objects.requireNonNull(task.getResult().get("Current_Weight")).toString();
                    weightDisplay.setText(displayString2);

                    if(UnitId == 0)
                    {
                        String u = "Kg";
                        weightUnit.setText(u);
                    }
                    else
                        {
                            String p = "lbs";
                            weightUnit.setText(p);
                        }
                    String sBmi = ""+ Math.round(BMI);
                    showBMI.setText(sBmi);
                }
                else
                {
                    String displayString1 = totalCal + " / " + Objects.requireNonNull(task.getResult().get("Target_Calorie_Intake")).toString();    //Displays the number of calories recorded in the day
                    calView.setText(displayString1);

                    String displayString2 = Objects.requireNonNull(task.getResult().get("Current_Weight")).toString();
                    weightDisplay.setText(displayString2);
                    String sBmi = ""+ Math.round(BMI);
                    showBMI.setText(sBmi);
                }
            }
        });
    }

    private long getUnitId()
    {
        String uId;
        uId = Objects.requireNonNull(Auth.getCurrentUser()).getUid();
        DocumentReference docR = fStore.collection("users").document(uId);

        docR.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                UnitId = (long) Objects.requireNonNull(task.getResult()).get("Unit Identification");
            }
        });
        return UnitId;
    }
}