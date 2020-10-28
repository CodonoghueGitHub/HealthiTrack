package Task2_17001430.healthitrack;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Objects;

public class View_Weight extends AppCompatActivity implements DatePickerDialog.OnDateSetListener
{

    TextView dateOption,dateResult,viewUsrWeight;
    FirebaseAuth Auth;
    FirebaseFirestore fStore;
    Button dash,DateOpen,viewUWeight;
    String Date,Weight,uId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view__weight);

        DateOpen = findViewById(R.id.CalendarOpen);
        viewUWeight = findViewById(R.id.viewWeight);
        dateOption = findViewById(R.id.ChooseDate);
        dateResult = findViewById(R.id.ViewDate);
        dash = findViewById(R.id.ToDash);
        Auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        viewUsrWeight = findViewById(R.id.UserWeight);


        DateOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(),"Date Picker");
            }
        });

        viewUWeight.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                uId = Objects.requireNonNull(Auth.getCurrentUser()).getUid();
                Date = dateOption.getText().toString().trim();
                DocumentReference docR;
                docR = fStore.collection("User_Daily_Weights").document(uId).collection(Date).document("Weight");

                docR.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
                {
                    long unitId;
                    String unit;

                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task)
                    {

                            if (Objects.requireNonNull(task.getResult()).exists())
                            {
                                DocumentReference docR3 = fStore.collection("User_Daily_Weights").document(uId).collection(Date).document("Weight");
                                docR3.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task)
                                    {
                                        unitId = (long) Objects.requireNonNull(task.getResult()).get("Unit_Id");
                                        if(unitId == 0)
                                        {
                                            unit = "Kg";
                                        }
                                        else
                                        {
                                            unit = "Pounds";
                                        }
                                    }
                                });

                                dateResult.setText(Date);
                                double displayString = Double.parseDouble(Objects.requireNonNull(Objects.requireNonNull(task.getResult().get("Weight")).toString()));
                                String ch = displayString + unit;
                                viewUsrWeight.setText(ch);
                            }
                            else
                                {
                                    Toast.makeText(View_Weight.this,"Document Does not exist", Toast.LENGTH_LONG).show();
                            }
                    }
                });

            }
        });

        dash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(), Dashboard.class));
                finish();
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

        dateOption.setText(currentDateString);
    }
}