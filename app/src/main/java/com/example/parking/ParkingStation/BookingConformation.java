package com.example.parking.ParkingStation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;


import com.example.parking.Activities.MainActivity;

import com.example.parking.Databaseconstant.Constants;
import com.example.parking.PreferanceManager.PreferenceManager;
import com.example.parking.databinding.ActivityBookingConformationBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class BookingConformation extends AppCompatActivity {

    ActivityBookingConformationBinding binding;

    PreferenceManager preferenceManager;

    String companyId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityBookingConformationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager=new PreferenceManager(getApplicationContext());

        Intent intent=getIntent();

        String amount=intent.getStringExtra("amount");

        String slotname=intent.getStringExtra("slotname");

        companyId=intent.getStringExtra("parkingstation");

        binding.amountpaid.setText(amount);

        updateSlotStatus(slotname);

        addtoparkinghistory();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent home=new Intent(BookingConformation.this, MainActivity.class);
                startActivity(home);
                finish();

            }
        },4000);
    }

    private void updateSlotStatus(String slotName) {

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference companyReference = database.collection(Constants.KEY_COLLECTION_PARKING)
                .document(companyId);





        companyReference.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String currentStatus = documentSnapshot.getString(slotName);

                if (currentStatus != null) {

                    String newStatus = currentStatus.equals("Available") ? "Unavailable" : "Available";

                    companyReference.update(slotName, newStatus)
                            .addOnFailureListener(e -> showToast("Failed to update slot status: " + e.getMessage()));
                } else {
                    showToast("Slot status not found");
                }
            } else {
                showToast("Document does not exist");
            }
        }).addOnFailureListener(e -> showToast("Error: " + e.getMessage()));
    }


    private void addtoparkinghistory() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        CollectionReference userParkingHistoryRef = database.collection(Constants.KEY_COLLECTION_PARKING_HISTORY)
                .document(preferenceManager.getString(Constants.KEY_Email))
                .collection(Constants.KET_SUBCOLLECTION_History);


        Map<String, Object> parkingSession = new HashMap<>();
        parkingSession.put("parking_station_name", companyId); // Assuming companyId is the parking station name
        parkingSession.put("date", getCurrentDate()); // Assuming you have a method to get the current date and time
        parkingSession.put("amount_paid", getIntent().getStringExtra("amount")); // Get amount from intent


        userParkingHistoryRef.add(parkingSession)
                .addOnFailureListener(e -> showToast("Failed to add parking session to history: " + e.getMessage()));
    }


    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }



    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}