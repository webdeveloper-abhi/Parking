package com.example.parking.ParkingStation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.parking.Databaseconstant.Constants;
import com.example.parking.PreferanceManager.PreferenceManager;
import com.example.parking.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class BookParking extends AppCompatActivity {

    PreferenceManager preferenceManager;

    TextView totalslots;

    String companyId;

    Boolean isAvailable=false;
    String adminnumber;

    TextView parkingstationname;



    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_parking);

        preferenceManager = new PreferenceManager(this);

        layout = findViewById(R.id.layoutParkingSlots);

        Intent intent=getIntent();
        adminnumber=intent.getStringExtra("adminnumber");

        totalslots = findViewById(R.id.totalparkingslots);

        parkingstationname=findViewById(R.id.parkingstationname);


        retrieveTotalParkingSlots();
    }


    private void retrieveTotalParkingSlots() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();


        database.collection(Constants.KEY_COLLECTION_ADMIN)
                .whereEqualTo("adminnumber", adminnumber)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {

                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        String totalSlotsString = documentSnapshot.getString(Constants.KEY_TOTALSLOTS);
                        companyId=documentSnapshot.getString(Constants.KEY_COMPANY);

                        int totalSlots = 0;
                        if (totalSlotsString != null) {
                            totalSlots = Integer.parseInt(totalSlotsString);
                            parkingstationname.setText(companyId);
                        }

                        int numRows = (totalSlots + 1) / 2;
                        totalslots.setText(totalSlotsString != null ? totalSlotsString : "");

                        createParkingSlotsGrid(numRows);
                        parkingstationdatabase(totalSlots);
                    } else {
                        showToast("Document does not exist");
                    }
                })
                .addOnFailureListener(e -> showToast("Error: " + e.toString()));
    }



    private void createParkingSlotsGrid(int numRows) {
        int slotNumber = 1;

        for (int i = 0; i < numRows; i++) {
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);

            for (int j = 0; j < 2; j++) {
                CardView cardView = new CardView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                );
                params.setMargins(8, 8, 8, 8);
                cardView.setLayoutParams(params);
                cardView.setMinimumWidth(100);
                cardView.setMinimumHeight(200);

                TextView textView = new TextView(this);
                textView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                textView.setTextAppearance(this, android.R.style.TextAppearance_Large);
                textView.setTextColor(Color.WHITE);
                textView.setGravity(Gravity.CENTER);
                cardView.addView(textView);

                final int finalSlotNumber = slotNumber;
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String slotname="slot"+finalSlotNumber;

                        checkspotAvailability(slotname);

                    }
                });

                fetchAndSetSlotStatus(slotNumber, cardView);

                rowLayout.addView(cardView);
                slotNumber++;
            }

            layout.addView(rowLayout);
        }
    }

    private void fetchAndSetSlotStatus(int slotNumber, CardView cardView) {

        if (companyId != null && !companyId.isEmpty()) {
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            DocumentReference companyReference = database.collection(Constants.KEY_COLLECTION_PARKING)
                    .document(companyId);

            String slotName = "slot" + slotNumber;


            companyReference.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String slotStatus = documentSnapshot.getString(slotName);
                    if (slotStatus != null) {
                        if (slotStatus.equals("Available")) {
                            cardView.setBackgroundResource(R.drawable.parkingsolid);
                        } else {
                            cardView.setBackgroundResource(R.drawable.noparking);
                        }
                    }
                } else {
                    showToast("Document does not exist");
                }
            }).addOnFailureListener(e -> showToast("Error: " + e.getMessage()));
        } else {
            showToast("Company ID is null or empty");
        }
    }

    private void parkingstationdatabase(int totalSlots) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        DocumentReference parkingSlotsReference = database.collection(Constants.KEY_COLLECTION_PARKING)
                .document(companyId);

        parkingSlotsReference.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        Map<String, Object> data = new HashMap<>();
                        for (int i = 1; i <= totalSlots; i++) {
                            String slotName = "slot" + i;
                            data.put(slotName, "Available");
                        }
                        parkingSlotsReference.set(data)
                                .addOnSuccessListener(aVoid -> {})
                                .addOnFailureListener(e -> showToast("Failed to create parking slots document: " + e.getMessage()));
                    }
                })
                .addOnFailureListener(e -> showToast("Failed to check parking slots document existence: " + e.getMessage()));
    }

    private void checkspotAvailability(String slotname) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_PARKING)
                .document(companyId);

        documentReference.get()
                .addOnSuccessListener(documentSnapshot -> {
                    String value = documentSnapshot.getString(slotname);
                    if (value != null && value.equals("Available")) {
                        isAvailable = true;
                        // Show the alert dialog or toast message here
                        AlertDialog.Builder alert = new AlertDialog.Builder(BookParking.this)
                                .setTitle("Alert")
                                .setMessage("Do you want to Book the parking slot")
                                .setIcon(R.drawable.aleart)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Intent intent=new Intent(BookParking.this, Payment.class);

                                        intent.putExtra("slotname",slotname);
                                        intent.putExtra("parkingstation",companyId);
                                        intent.putExtra("adminnumber",adminnumber);

                                        startActivity(intent);
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        showToast("The parking slot booking is cancelled");
                                    }
                                });
                        alert.show();
                    } else {
                        isAvailable = false;
                        showToast("Sorry! The Parking Slot is Already Booked");
                    }
                })
                .addOnFailureListener(e -> showToast("Error: " + e.toString()));
    }





    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
