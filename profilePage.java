package Task2_17001430.healthitrack;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.rpc.context.AttributeContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class profilePage extends AppCompatActivity
{
    TextView email,name,height,dailyCalTarget,targetWeight;
    Button update,home;
    String uId;
    FirebaseAuth Auth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        email = findViewById(R.id.showEmail);
        name = findViewById(R.id.showName);
        height = findViewById(R.id.showHeight);
        dailyCalTarget = findViewById(R.id.showTargetCal);
        targetWeight = findViewById(R.id.showTargetWeight);
        update = findViewById(R.id.updateBtn);
        Auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        home =  findViewById(R.id.goToDash);

        showUserData();

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                UpdateProfile();
            }
        });

        home.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(), Dashboard.class));
            }
        });

    }

    private void UpdateProfile()
    {
        String uEmail,uName,uId;
        double uHeight,dailyCalIntake,targetW;

        uEmail = email.getText().toString().trim();
        uName = name.getText().toString().trim();
        uHeight = Double.parseDouble(height.getText().toString().trim());
        dailyCalIntake = Double.parseDouble(dailyCalTarget.getText().toString().trim());
        targetW = Double.parseDouble(targetWeight.getText().toString().trim());
        uId = Objects.requireNonNull(Auth.getCurrentUser()).getUid();
        FirebaseUser user = Auth.getCurrentUser();
        user.updateEmail(uEmail);

        DocumentReference docR = fStore.collection("users").document(uId);
        Map<String,Object> userData = new HashMap<>();
        userData.put("Email", uEmail);
        userData.put("Full_Name",uName);
        userData.put("Height",uHeight);
        userData.put("Target_Calorie_Intake",dailyCalIntake);
        userData.put("Target_Weight",targetW);
        docR.update(userData).addOnSuccessListener(new OnSuccessListener<Void>()
        {
            @Override
            public void onSuccess(Void aVoid)
            {
                Toast.makeText(profilePage.this, "Profile has been Updated", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), Dashboard.class));
                finish();
            }
        });
    }

    private void showUserData()
    {
        uId = Objects.requireNonNull(Auth.getCurrentUser()).getUid();
        DocumentReference docR;
        docR = fStore.collection("users").document(uId);

        docR.addSnapshotListener(this, new EventListener<DocumentSnapshot>()
        {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error)
            {
                assert value != null;
                email.setText(value.getString("Email"));
                name.setText(value.getString("Full_Name"));
                height.setText(Objects.requireNonNull(value.get("Height")).toString());
                dailyCalTarget.setText(Objects.requireNonNull(value.get("Target_Calorie_Intake")).toString());
                targetWeight.setText(Objects.requireNonNull(value.get("Target_Weight")).toString());
            }
        });

    }
}