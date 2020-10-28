package Task2_17001430.healthitrack;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity
{

    EditText uEmail,uFullName,uPassword,rePass,uHeight,uWeight,tW,tC;
    Button Register;
    ProgressBar lBar;
    FirebaseAuth Auth;
    FirebaseFirestore fStore;
    Spinner unitSpinner;
    int unitSaveValue;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        Register = findViewById(R.id.Register);
        uPassword = findViewById(R.id.PasswordBox);
        uEmail = findViewById(R.id.userEmail);
        rePass = findViewById(R.id.RepeatPassword);
        uFullName = findViewById(R.id.Username);
        lBar = findViewById(R.id.progressBar);
        Auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        uHeight = findViewById(R.id.usrHeight);
        uWeight = findViewById(R.id.usrWeight);
        tW = findViewById(R.id.usrTargetW);
        tC = findViewById(R.id.usrTargetC);
        unitSpinner = findViewById(R.id.Metric_Imperial_Spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.Units, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(adapter);



//        if(Auth.getCurrentUser() != null)
//        {
//            startActivity(new Intent(getApplicationContext(),LoginPage.class));
//            finish();
//        }

        Register.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final String email = uEmail.getText().toString().trim();
                final String name = uFullName.getText().toString().trim();
                String password = uPassword.getText().toString().trim();
                String Pass2 = rePass.getText().toString().trim();
                final double sHeight = Double.parseDouble(uHeight.getText().toString());
                final double sWeight = Double.parseDouble(uWeight.getText().toString().trim());
                final double sTarW = Double.parseDouble(tW.getText().toString().trim());
                final double sTarC = Double.parseDouble(tC.getText().toString().trim());


                unitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                    {
                        String unit;
                        unit = unitSpinner.getSelectedItem().toString().trim();
                        if(unit.equals("Kg/cm"))
                        {
                            unitSaveValue = 0;
                        }
                        else
                        {
                            unitSaveValue = 1;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent)
                    {
                       unitSaveValue = 0;
                    }
                });

                if(!TextUtils.equals(password,Pass2))
                {
                   rePass.setError("Passwords Do Not Match!");
                    lBar.setVisibility(View.INVISIBLE);                                                     //Start of validation
                   return;
                }

                if(TextUtils.isEmpty(email))
                {
                    uEmail.setError("Email is Required");
                    lBar.setVisibility(View.INVISIBLE);
                    return;
                }
                if(TextUtils.isEmpty(name))
                {
                    uEmail.setError("Name is Required");
                    lBar.setVisibility(View.INVISIBLE);
                    return;
                }
                if(TextUtils.isEmpty(password))
                {
                    uPassword.setError("Password is Required");
                    lBar.setVisibility(View.INVISIBLE);
                    return;
                }

                if(TextUtils.isEmpty(Pass2))
                {
                    uEmail.setError("Please repeat password");
                    lBar.setVisibility(View.INVISIBLE);
                    return;
                }

                if (uHeight.getText().toString().equals(""))
                {
                    uHeight.setError("Height is Required");
                    lBar.setVisibility(View.INVISIBLE);
                    return;
                }
                if (uWeight.getText().toString().equals(""))
                {
                    uWeight.setError("Weight is Required");
                    lBar.setVisibility(View.INVISIBLE);
                    return;
                }

                if(tW.getText().toString().equals(""))
                {
                    tW.setError("Target Weight is Required");
                    lBar.setVisibility(View.INVISIBLE);
                    return;
                }
                if(tC.getText().toString().equals(""))
                {
                    tC.setError("Daily Target Calories are Required");
                    lBar.setVisibility(View.INVISIBLE);
                    return;
                }


                    lBar.setVisibility(View.VISIBLE);

                Auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                            if(task.isSuccessful())
                            {
                                String uId;
                                uId = Objects.requireNonNull(Auth.getCurrentUser()).getUid();
                                DocumentReference docR = fStore.collection("users").document(uId);
                                Map<String,Object> user = new HashMap<>();
                                user.put("Full_Name",name);
                                user.put("Email", email);
                                user.put("Height",sHeight);
                                user.put("Current_Weight",sWeight);
                                user.put("Target_Weight",sTarW);
                                user.put("Target_Calorie_Intake",sTarC);
                                user.put("Unit Identification",unitSaveValue);
                                docR.set(user).addOnSuccessListener(new OnSuccessListener<Void>()
                               {
                                   @Override
                                   public void onSuccess(Void aVoid)
                                   {
                                       Toast.makeText(RegisterActivity.this, "Profile has been Created", Toast.LENGTH_SHORT).show();
                                       startActivity(new Intent(getApplicationContext(), LoginPage.class));
                                   }
                               });
                            }
                            else
                            {
                                Toast.makeText(RegisterActivity.this, "Error" + Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_SHORT).show();
                                lBar.setVisibility(View.INVISIBLE);
                            }
                    }


                });
                }
            });
        }
    }

