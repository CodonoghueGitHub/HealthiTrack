package Task2_17001430.healthitrack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class User_Weight extends AppCompatActivity implements DatePickerDialog.OnDateSetListener
{
    Button save,DateOpen;
    TextView weightDate,uWeight;
    String Date,uId;
    double Weight;
    ProgressBar pb;
    FirebaseAuth Auth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__weight);

        DateOpen = findViewById(R.id.openCalendar);
        save = findViewById(R.id.saveUweight);
        weightDate = findViewById(R.id.weightingDate);
        uWeight = findViewById(R.id.uDailyWeight);
        pb = findViewById(R.id.pbWeight);
        pb.setVisibility(View.INVISIBLE);
        Auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        save.setOnClickListener(new View.OnClickListener()
        {
            long unitId;

            @Override
            public void onClick(View v)
            {
                uId = Objects.requireNonNull(Auth.getCurrentUser()).getUid();
                DocumentReference docR3 = fStore.collection("users").document(uId);
                docR3.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task)
                    {
                        unitId = (long) Objects.requireNonNull(task.getResult()).get("Unit Identification");
                    }
                });
                pb.setVisibility(View.VISIBLE);
                Weight = Double.parseDouble(uWeight.getText().toString().trim());
                Date = weightDate.getText().toString();
                if(!(Date.equals("")))
                {
                    String w ="Weight";
                    uId = Objects.requireNonNull(Auth.getCurrentUser()).getUid();
                    DocumentReference docR = fStore.collection("User_Daily_Weights").document(uId).collection(Date).document(w);
                    Map<String, Object> user = new HashMap<>();
                    user.put("Weight", Weight);
                    user.put("Unit_Id",unitId);
                    docR.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(User_Weight.this, "Weight has been saved for the day!", Toast.LENGTH_SHORT).show();
                        }
                    });

                    DocumentReference docR2 = fStore.collection("users").document(uId);
                    Map<String, Object> user1 = new HashMap<>();
                    user1.put("Current_Weight",Weight);
                    docR2.update(user1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            Toast.makeText(User_Weight.this,"Current Weight Updated",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), Dashboard.class));
                            finish();
                        }
                    });

                }
                else
                {
                    pb.setVisibility(View.INVISIBLE);
                    Toast.makeText(User_Weight.this,"Please enter a date",Toast.LENGTH_LONG).show();
                }

            }
        });

        DateOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(),"Date Picker");
            }
        });
    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
    {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR,year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());

        weightDate.setText(currentDateString);
    }
}