package Task2_17001430.healthitrack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Objects;

public class viewImages extends AppCompatActivity  implements DatePickerDialog.OnDateSetListener
{
    FirebaseAuth Auth;
    Spinner mealChoice;
    Button Calen,vImg,db;
    String mealString,imgDate;
    TextView imgDay;
    ImageView img1,img2,img3;
    StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_images);

        img1 = findViewById(R.id.imageView1);
        img2 = findViewById(R.id.imageView2);           //Images to display user diet images
        img3 = findViewById(R.id.imageView3);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        db = findViewById(R.id.dashBtn);
        imgDay = findViewById(R.id.imgDate);
        Auth = FirebaseAuth.getInstance();
        Calen = findViewById(R.id.calendar);
        vImg = findViewById(R.id.viewImg);
        mealChoice = findViewById(R.id.MealChoice);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.Meals, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);                                                                 //Linking text items to drop down list
        mealChoice.setAdapter(adapter);


        mealChoice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                mealString= mealChoice.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                mealString= mealChoice.getSelectedItem().toString();
            }
        });

        Calen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(),"Date Picker");                         //Open calendar to select date
            }
        });

       vImg.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v)
           {
               imgDate= imgDay.getText().toString().trim();
               if(imgDate.equals(""))                                                           //Making sure the user does not forget to enter date
               {
                   imgDay.setError("Date is required");
               }
               String uId = Objects.requireNonNull(Auth.getCurrentUser()).getUid();
               StorageReference filepath1 = mStorageRef.child("Diet_Photos").child(uId).child(mealString).child(imgDate).child("Images/Meal 1");            //Retrieving images from firebase
               StorageReference filepath2 = mStorageRef.child("Diet_Photos").child(uId).child(mealString).child(imgDate).child("Images/Meal 2");
               StorageReference filepath3 = mStorageRef.child("Diet_Photos").child(uId).child(mealString).child(imgDate).child("Images/Meal 3");

               try
               {
                   final File localFile1 = File.createTempFile("Meal 1","jpeg");                                            //Try and Catch block to retrieve file from firebase and catch exception if no image was saved
                   filepath1.getFile(localFile1).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>()
                   {
                       @Override
                       public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot)
                       {
                           Bitmap bitmap1 = BitmapFactory.decodeFile(localFile1.getAbsolutePath());
                           img1.setImageBitmap(bitmap1);
                       }
                   }).addOnFailureListener(new OnFailureListener()
                   {
                       @Override
                       public void onFailure(@NonNull Exception e)
                       {
                           img1.setImageDrawable(Drawable.createFromPath("@mipmap/ic_launcher"));
                           Toast.makeText(viewImages.this,"No picture for this item", Toast.LENGTH_SHORT).show();
                       }
                   });

               } catch (IOException e)
               {
                   e.printStackTrace();
               }
               try {                                                                                                                                       //Try and Catch block to retrieve file from firebase and catch exception if no image was saved
                   final File localFile2 = File.createTempFile("Meal 2","jpeg");
                   filepath2.getFile(localFile2).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>()
                   {
                       @Override
                       public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot)
                       {
                           Bitmap bitmap1 = BitmapFactory.decodeFile(localFile2.getAbsolutePath());                 //Converting image file to bitmap
                           img3.setImageBitmap(bitmap1);                                                    //Set image for user to view
                       }
                   }).addOnFailureListener(new OnFailureListener()
                   {
                       @Override
                       public void onFailure(@NonNull Exception e)
                       {
                           img2.setImageDrawable(Drawable.createFromPath("@mipmap/ic_launcher"));
                           Toast.makeText(viewImages.this,"All Pictures displayed", Toast.LENGTH_SHORT).show();
                       }
                   });

               } catch (IOException e)
               {
                   e.printStackTrace();                 // Show problem if occurs
               }

               try {
                   final File localFile3 = File.createTempFile("Meal 3","jpeg");
                   filepath3.getFile(localFile3).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>()
                   {
                       @Override
                       public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot)
                       {
                           Bitmap bitmap1 = BitmapFactory.decodeFile(localFile3.getAbsolutePath());
                           img3.setImageBitmap(bitmap1);
                       }
                   }).addOnFailureListener(new OnFailureListener()
                   {
                       @Override
                       public void onFailure(@NonNull Exception e)
                       {
                           img3.setImageDrawable(Drawable.createFromPath("@mipmap/ic_launcher"));
                           Toast.makeText(viewImages.this,"No picture for this item", Toast.LENGTH_SHORT).show();
                       }
                   });
               } catch (IOException e)
               {
                   e.printStackTrace();
               }

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

        imgDay.setText(currentDateString);
    }

}
