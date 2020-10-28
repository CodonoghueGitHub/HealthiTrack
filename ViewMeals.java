package Task2_17001430.healthitrack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Objects;

public class ViewMeals extends AppCompatActivity implements DatePickerDialog.OnDateSetListener
{

    TextView dateOption,viewUsrMeal1,viewUsrMeal2,viewUsrMeal3;
    FirebaseAuth Auth;
    FirebaseFirestore fStore;
    Button dash,DateOpen,viewUMeals;
    Spinner spin;
    String meal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_meals);

        viewUsrMeal1 = findViewById(R.id.viewMeal1);
        viewUsrMeal2 = findViewById(R.id.viewMeal2);
        viewUsrMeal3 = findViewById(R.id.viewMeal3);
        DateOpen = findViewById(R.id.calopen);
        dateOption = findViewById(R.id.dateOpt);
        viewUMeals = findViewById(R.id.viewMeals);
        spin = findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.Meals, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);


        DateOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(),"Date Picker");
            }
        });

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                meal = spin.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                if(spin.getSelectedItem().toString().equalsIgnoreCase("choose date below"))
                {
                    Toast.makeText(ViewMeals.this, "Please select Date", Toast.LENGTH_SHORT).show();
                }
                else{
                    meal = spin.getSelectedItem().toString();
                }

            }
        });

        viewUMeals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String uId,date;
                date = dateOption.getText().toString().trim();
                uId = Objects.requireNonNull(Auth.getCurrentUser()).getUid();
                DocumentReference docR = fStore.collection("User_Diets").document(uId).collection(meal).document(date);

                docR.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task)
                    {
                        String m1,m2,m3;
                        m1 = Objects.requireNonNull(task.getResult()).getString("Meal 1");
                        viewUsrMeal1.setText(m1);

                        m2 = Objects.requireNonNull(task.getResult()).getString("Meal 2");
                        viewUsrMeal2.setText(m2);

                        m3 = Objects.requireNonNull(task.getResult()).getString("Meal 3");
                        viewUsrMeal3.setText(m3);
                    }
                });
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