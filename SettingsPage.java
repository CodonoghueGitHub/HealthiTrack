package Task2_17001430.healthitrack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SettingsPage extends AppCompatActivity
{
    Switch unitConvert;
    FirebaseAuth Auth;
    FirebaseFirestore fStore;
    Button saveSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);

        String uId;
        Auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        uId = Objects.requireNonNull(Auth.getCurrentUser()).getUid();
        DocumentReference docR2  = fStore.collection("users").document(uId);
        docR2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                long check = (long) Objects.requireNonNull(task.getResult()).get("Unit Identification");

                unitConvert.setOnCheckedChangeListener(null);

                if (check == 0)
                {
                    unitConvert.setChecked(false);
                } else
                {
                    unitConvert.setChecked(true);
                }
            }
        });

        unitConvert = findViewById(R.id.switch1);
        saveSettings = findViewById(R.id.saveSettings);



        saveSettings.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ConvertUserdata();
                Toast.makeText(SettingsPage.this,"Converted Measurements",Toast.LENGTH_LONG).show();
            }
        });

    }


    private void ConvertUserdata()
    {
        if (unitConvert.isChecked())   //  Metric to Imperial
        {
            String uId;
            uId = Objects.requireNonNull(Auth.getCurrentUser()).getUid();
            DocumentReference docR = fStore.collection("users").document(uId);
            docR.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
            {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task)
                {
                    double changed_height,changed_Tw,changedCurrentWeight;
                    String db1 = (Objects.requireNonNull(Objects.requireNonNull(task.getResult()).get("Current_Weight"))).toString();
                    double currentW = Double.parseDouble(db1) ;
                    double mHeight = (double) Objects.requireNonNull(task.getResult().get("Height"));
                    double TargetWeight = (double) task.getResult().get("Target_Weight");

                    changedCurrentWeight = currentW;
                    changedCurrentWeight = (changedCurrentWeight * 2.20462d);

                    changed_height = mHeight;
                    changed_height = (changed_height /30.48d);            // CM to Feet

                    changed_Tw = TargetWeight;
                    changed_Tw = (changed_Tw * 2.20462d);                                                              //Kilograms to Pounds

                    String uId1;
                    uId1 = Objects.requireNonNull(Auth.getCurrentUser()).getUid();
                    DocumentReference docR1 = fStore.collection("users").document(uId1);
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("Height", changed_height);
                    userData.put("Target_Weight", changed_Tw);
                    userData.put("Current_Weight", Math.round(changedCurrentWeight));
                    userData.put("Unit Identification",1);
                    docR1.update(userData).addOnSuccessListener(new OnSuccessListener<Void>()
                    {
                        @Override
                        public void onSuccess(Void aVoid)
                        {
                            Toast.makeText(SettingsPage.this, "Profile has been Updated", Toast.LENGTH_LONG).show();

                        }
                    }).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            startActivity(new Intent(getApplicationContext(), Dashboard.class));
                            finish();
                        }
                    });
                }
            });

        }
        else
        {
            if(!unitConvert.isChecked())                   //Imperial to Metric
            {
                String uId;
                uId = Objects.requireNonNull(Auth.getCurrentUser()).getUid();
                DocumentReference docR = fStore.collection("users").document(uId);
                docR.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task)
                    {
                        double changed_height, changed_Tw,changedCurrentWeight;
                        String db1 = (Objects.requireNonNull(Objects.requireNonNull(task.getResult()).get("Current_Weight"))).toString();
                        double currentWeighting = Double.parseDouble(db1);
                        double mHeight = (double) Objects.requireNonNull(task.getResult()).get("Height");
                        double TargetWeight = (double) Objects.requireNonNull(task.getResult()).get("Target_Weight");


                        changedCurrentWeight = currentWeighting;
                        changedCurrentWeight = (changedCurrentWeight/2.20462);

                        changed_height = mHeight;
                        changed_height = Math.round(changed_height * 30.48);                 //Feet to cm


                        changed_Tw = TargetWeight;
                        Toast.makeText(SettingsPage.this,"Weight"+changed_Tw,Toast.LENGTH_LONG).show();
                        changed_Tw = Math.round(changed_Tw / 2.205);                           //Pounds to Kilograms


                        String uId1;
                        uId1 = Objects.requireNonNull(Auth.getCurrentUser()).getUid();
                        DocumentReference docR1 = fStore.collection("users").document(uId1);
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("Height",changed_height );
                        userData.put("Target_Weight", changed_Tw);
                        userData.put("Current_Weight", Math.round(changedCurrentWeight));
                        userData.put("Unit Identification",0);
                        docR1.update(userData).addOnSuccessListener(new OnSuccessListener<Void>()
                        {
                            @Override
                            public void onSuccess(Void aVoid)
                            {
                                Toast.makeText(SettingsPage.this, "Profile has been Updated", Toast.LENGTH_SHORT).show();

                            }
                        }).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                startActivity(new Intent(getApplicationContext(), Dashboard.class));
                                finish();
                            }
                        });
                    }
                });

            }
    }   }
}