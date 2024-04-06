package com.example.parking.ParkingStation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;

import com.example.parking.Databaseconstant.Constants;
import com.example.parking.PreferanceManager.PreferenceManager;
import com.example.parking.R;
import com.example.parking.databinding.ActivityParkingStationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class ParkingStation extends AppCompatActivity {

    ActivityParkingStationBinding binding;

    String phone,email,name;

    PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityParkingStationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager=new PreferenceManager(ParkingStation.this);

        Intent intent=getIntent();
        String adminnumber=intent.getStringExtra("adminnumber");


        FirebaseFirestore database=FirebaseFirestore.getInstance();

        database.collection(Constants.KEY_COLLECTION_ADMIN)
                .whereEqualTo(Constants.KEY_ADMIN_NUMBER,adminnumber)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        DocumentSnapshot documentSnapshot=task.getResult().getDocuments().get(0);

                        phone=documentSnapshot.getString(Constants.KEY_Phone);

                        email=documentSnapshot.getString(Constants.KEY_Email);

                        name=documentSnapshot.getString(Constants.KEY_COMPANY);

                        String bitmapString = documentSnapshot.getString(Constants.KEY_Image);

                        Bitmap bitmap = stringToBitmap(bitmapString);



                        binding.parkingStationName.setText(documentSnapshot.getString(Constants.KEY_COMPANY));
                        binding.parkingStationAddress.setText(documentSnapshot.getString(Constants.KEY_LOCATION));
                        binding.parkingStationPhoto.setImageBitmap(bitmap);
                        binding.parkinglocation.setText(documentSnapshot.getString(Constants.KEY_LOCATION));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ParkingStation.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

        //OnCLick Listeners

        listeners();

    }

    private void listeners(){
        binding.call.setOnClickListener(v->{

            Intent intent1=new Intent(Intent.ACTION_DIAL);

            intent1.setData(Uri.parse("tel:" + phone));

            startActivity(intent1);

        });

        binding.message.setOnClickListener(v -> {



            String emailBody = "Name: " + "\n"
                    + "Email: " + email ;

            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:" + Uri.encode(email)));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Support Request");
            emailIntent.putExtra(Intent.EXTRA_TEXT, emailBody);

            startActivity(emailIntent);



        });

        binding.btnpickaspot.setOnClickListener(v -> {


        });
    }



    private Bitmap stringToBitmap(String imageString) {
        try {
            byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}