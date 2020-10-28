package Task2_17001430.healthitrack;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.rpc.context.AttributeContext;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button RegisterBtn = findViewById(R.id.RegisterBtn);
        Button LoginBtn = findViewById(R.id.LoginBtn);
        FirebaseAuth Auth = FirebaseAuth.getInstance();


        if(Auth.getCurrentUser() != null)
        {
            startActivity(new Intent(getApplicationContext(),LoginPage.class));
            finish();
        }

        RegisterBtn.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent in =  new Intent(v.getContext(), RegisterActivity.class);        //Links The next Activity where user will Register for an account
                startActivity(in);
            }

        });

        LoginBtn.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent in2 =  new Intent(v.getContext(), LoginPage.class);      //Links the next Activity For users to Login
                startActivity(in2);
            }
        });

    }
}