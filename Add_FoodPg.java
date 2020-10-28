package Task2_17001430.healthitrack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.google.gson.internal.bind.util.ISO8601Utils.format;

public class Add_FoodPg extends AppCompatActivity implements DatePickerDialog.OnDateSetListener
{

    EditText m1,m1C,m2,m2C,m3,m3C;
    TextView MealDate;
    ImageView meal3Image,meal2Image,meal1Image;
    Button save,DateOpen;
    ProgressBar pb;
    Spinner MealSpinner;
    FirebaseAuth Auth;
    FirebaseFirestore fStore;
    StorageReference mStorageRef;
    int picSelect;
    Uri img_1_Uri,img_2_Uri,img_3_Uri;
    String meal;
    Bitmap image;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__food_pg);

        MealSpinner = findViewById(R.id.MealSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.Meals, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        MealSpinner.setAdapter(adapter);
        DateOpen = findViewById(R.id.OpenDate);
        m1 = findViewById(R.id.Meal1);
        m1C = findViewById(R.id.Meal1Calorie);
        m2 = findViewById(R.id.Meal2);
        m2C = findViewById(R.id.Meal2Calorie);
        m3 = findViewById(R.id.Meal3);
        m3C = findViewById(R.id.Meal3Calorie);
        pb = findViewById(R.id.progressBar2);
        Auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        save = findViewById(R.id.saveBtn);
        MealDate = findViewById(R.id.Day);
        meal3Image = findViewById(R.id.img_Meal3);
        meal2Image = findViewById(R.id.img_Meal2);
        meal1Image = findViewById(R.id.img_Meal1);
        mStorageRef = FirebaseStorage.getInstance().getReference();


        meal3Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                picSelect = 3;
                cameraPermission();
            }
        });

        meal2Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                picSelect = 2;
                cameraPermission();
            }
        });
        meal1Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                picSelect = 1;
                cameraPermission();
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

        MealSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                meal = MealSpinner.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                if(MealSpinner.getSelectedItem().toString().equalsIgnoreCase("choose date below"))
                {
                    Toast.makeText(Add_FoodPg.this, "Please select Date", Toast.LENGTH_SHORT).show();
                }
                else{
                    meal = MealSpinner.getSelectedItem().toString();
                }

            }
        });

        save.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String date = MealDate.getText().toString();
                if (date.equals(""))
                {
                    Toast.makeText(Add_FoodPg.this, "Please Insert Date", Toast.LENGTH_SHORT).show();
                } else
                    {
                        if (img_1_Uri == null && img_2_Uri == null && img_3_Uri == null)
                        {
                            saveMeal(meal);
                        }
                        else if (img_1_Uri == null)
                        {
                            if (img_3_Uri != null && img_2_Uri != null)
                            {
                                uploadImage(img_2_Uri,"Meal 2");
                                uploadImage(img_3_Uri,"Meal 3");
                                saveMeal(meal);
                            }
                            saveMeal(meal);
                        }
                        else if (img_2_Uri == null)
                        {
                            if (img_3_Uri != null)
                            {
                                uploadImage(img_1_Uri,"Meal 1");
                                uploadImage(img_3_Uri,"Meal 3");
                                saveMeal(meal);
                            }
                            uploadImage(img_1_Uri,"Meal 1");
                            saveMeal(meal);
                        } else if (img_3_Uri == null) {
                            uploadImage(img_1_Uri,"Meal 1");
                            uploadImage(img_2_Uri,"Meal 2");
                            saveMeal(meal);
                        } else {
                            uploadImage(img_1_Uri,"Meal 1");
                            uploadImage(img_2_Uri,"Meal 2");
                            uploadImage(img_3_Uri,"Meal 3");
                            saveMeal(meal);
                        }
                    }
            }
        });

    }
    private void saveMeal(String meal)
    {
        final String meal_1;
        final String meal_2;
        final String meal_3;
        final String date;
        String Meal1_Cal;
        String Meal2_Cal;
        String Meal3_Cal;
        String uId = null;

        meal_1 = m1.getText().toString().trim();
        meal_2 = m2.getText().toString().trim();
        meal_3 = m3.getText().toString().trim();
        Meal1_Cal =  m1C.getText().toString().trim();
        Meal2_Cal = m2C.getText().toString().trim();
        Meal3_Cal = m3C.getText().toString().trim();
        date = MealDate.getText().toString().trim();

        if(Meal1_Cal.equalsIgnoreCase("")&& Meal2_Cal.equalsIgnoreCase("") && Meal3_Cal.equalsIgnoreCase(""))
        {
            Meal1_Cal = "0";
            Meal2_Cal = "0";
            Meal3_Cal = "0";
        }
        else if(Meal2_Cal.equalsIgnoreCase("") && Meal3_Cal.equalsIgnoreCase(""))
        {
            Meal2_Cal = "0";
            Meal3_Cal = "0";
        }
        else if (Meal3_Cal.equalsIgnoreCase(""))
        {
            Meal3_Cal = "0";
        }
            pb.setVisibility(View.VISIBLE);

            uId = Objects.requireNonNull(Auth.getCurrentUser()).getUid();
            DocumentReference docR = fStore.collection("User_Diets").document(uId).collection(meal).document(date);
            double cal1,cal2,cal3;
            cal1 = Double.parseDouble(Meal1_Cal);
            cal2 = Double.parseDouble(Meal2_Cal);
            cal3 = Double.parseDouble(Meal3_Cal);
            Map<String,Object> user = new HashMap<>();

            user.put("Meal 1",meal_1);
            user.put("Meal 1 Calories", cal1);
            user.put("Meal 2",meal_2);
            user.put("Meal 2 Calories",cal2);
            user.put("Meal 3",meal_3);
            user.put("Meal 3 Calories",cal3);
            docR.set(user).addOnSuccessListener(new OnSuccessListener<Void>()
            {
                @Override
                public void onSuccess(Void aVoid)
                {
                    Toast.makeText(Add_FoodPg.this, "Details have been saved", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), Dashboard.class));
                    finish();
                }
            });
    }

    private void cameraPermission()
    {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA},1);
        }else
        {
            openCamera();
        }
    }

    private void openCamera()
    {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera,2);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2)
        {
            image = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
           if(picSelect == 3)
           {
               meal3Image.setImageBitmap(image);
               assert image != null;
               img_3_Uri = getImageUri(this,image);

           }
           else if(picSelect == 2)
           {
               assert image != null;
               img_2_Uri = getImageUri(this,image);
               meal2Image.setImageBitmap(image);
           }
           else
           {
               assert image != null;
               img_1_Uri = getImageUri(this,image);
               meal1Image.setImageBitmap(image);
           }
        }
    }
    private void uploadImage(Uri ImageUri, String imgName)
    {
        String date = MealDate.getText().toString().trim();
        String uId = Objects.requireNonNull(Auth.getCurrentUser()).getUid();
        StorageReference filePath1 = mStorageRef.child("Diet_Photos").child(uId).child(meal).child(date).child("Images").child(imgName);
        filePath1.putFile(ImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Toast.makeText(Add_FoodPg.this,"Uploaded",Toast.LENGTH_LONG).show();
            }
        });
    }
    private Uri getImageUri(Context inContext, Bitmap inImage)
    {
        Date currentTime;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title"+ Calendar.getInstance().getTime(), null);
        return Uri.parse(path);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 1)
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
               openCamera();
            }
            else
            {
                Toast.makeText(this,"Permission Required!",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
    {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR,year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());

        MealDate.setText(currentDateString);
    }
}