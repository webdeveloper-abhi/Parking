package com.example.parkingadmin.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.parkingadmin.PreferenceManager.PreferenceManager;
import com.example.parkingadmin.R;
import com.example.parkingadmin.Utilities.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class ParkingSlots extends Fragment {

    PreferenceManager preferenceManager;

    EditText totalslots, availableslots, bookedslots;
    Button btnmodifyslot;

    LinearLayout layout;
    boolean toadd = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parking_slots, container, false);

        preferenceManager=new PreferenceManager(getContext());

        totalslots = view.findViewById(R.id.totalparkingslots);

        layout=view.findViewById(R.id.layoutParkingSlots);

        btnmodifyslot = view.findViewById(R.id.modifyslots);

        btnmodifyslot.setOnClickListener(v -> modifySlots());

        retrieveTotalParkingSlots();

        return view;
    }


    private void modifySlots() {
        if (totalslots.getText().toString().isEmpty()) {
            showToast("Please Fill all the fields...");
            return;
        }

        int total = Integer.parseInt(totalslots.getText().toString());

        totalslots.setText(String.valueOf(total));

        FirebaseFirestore database = FirebaseFirestore.getInstance();

        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_ADMIN)
                        .document(preferenceManager.getString(Constants.KEY_UserId));

        Map<String, Object> update = new HashMap<>();

        update.put(Constants.KEY_TOTALSLOTS, String.valueOf(total));

        documentReference.update(update)
                .addOnSuccessListener(aVoid -> {
                    showToast("Total slots count updated successfully");
                    // Call retrieveTotalParkingSlots after successful update
                    retrieveTotalParkingSlots();
                })
                .addOnFailureListener(e -> showToast("Error: " + e.toString()));
    }


    private void retrieveTotalParkingSlots() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_ADMIN)
                .document(preferenceManager.getString(Constants.KEY_UserId));

        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String totalSlotsString = documentSnapshot.getString(Constants.KEY_TOTALSLOTS);
                    String availableSlots = documentSnapshot.getString(Constants.KEY_AVAILABLESLOTS);
                    String bookedSlots = documentSnapshot.getString(Constants.KEY_BOOKEDSLOTS);

                    int totalSlots = 0;
                    if (totalSlotsString != null) {
                        totalSlots = Integer.parseInt(totalSlotsString);
                    }

                    int numRows = (totalSlots + 1) / 2;
                    totalslots.setText(totalSlotsString != null ? totalSlotsString : "");


                    createParkingSlotsGrid(numRows);
                    parkingstationdatabase(totalSlots);
                } else {
                    showToast("Document does not exist");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showToast("Error: " + e.toString());
            }
        });
    }

    private void createParkingSlotsGrid(int numRows) {
        int slotNumber = 1;

        for (int i = 0; i < numRows; i++) {
            LinearLayout rowLayout = new LinearLayout(getContext());
            rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);

            for (int j = 0; j < 2; j++) {
                CardView cardView = new CardView(getContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                );
                params.setMargins(8, 8, 8, 8);
                cardView.setLayoutParams(params);
                cardView.setMinimumWidth(100);
                cardView.setMinimumHeight(200);

                TextView textView = new TextView(getContext());
                textView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                textView.setTextAppearance(getContext(), android.R.style.TextAppearance_Large);
                textView.setTextColor(Color.WHITE);
                textView.setGravity(Gravity.CENTER);
                cardView.addView(textView);

                final int finalSlotNumber = slotNumber; // to use in inner class
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder alert=new AlertDialog.Builder(getContext())
                                .setTitle("Alert")
                                .setMessage("Do you want to  modify parking Slot")
                                .setIcon(R.drawable.aleart)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Update slot status and change background image here
                                        updateSlotStatus(finalSlotNumber, cardView);
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        showToast("The parking slot status is resumed");

                                    }
                                });

                        alert.show();

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
        String companyId = preferenceManager.getString(Constants.KEY_COMPANY);

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference companyReference = database.collection(Constants.KEY_COLLECTION_PARKING)
                .document(companyId);

        // Reference to the specific slot within the company document
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
    }


    private void parkingstationdatabase(int totalSlots) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        String companyId = preferenceManager.getString(Constants.KEY_COMPANY);

        DocumentReference parkingSlotsReference = database.collection(Constants.KEY_COLLECTION_PARKING)
                .document(companyId);

        parkingSlotsReference.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {

                    } else {
                        // Parking slots document doesn't exist, create it and then update slot statuses
                        Map<String, Object> data = new HashMap<>();
                        for (int i = 1; i <= totalSlots; i++) {
                            String slotName = "slot" + i;
                            data.put(slotName, "Available");
                        }
                        parkingSlotsReference.set(data)
                                .addOnSuccessListener(aVoid -> {
                                    // Parking slots document created successfully, now update slot statuses

                                })
                                .addOnFailureListener(e -> showToast("Failed to create parking slots document: " + e.getMessage()));
                    }
                })
                .addOnFailureListener(e -> showToast("Failed to check parking slots document existence: " + e.getMessage()));
    }



    private void updateSlotStatus(int slotNumber, CardView cardView) {
        String companyId = preferenceManager.getString(Constants.KEY_COMPANY);

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference companyReference = database.collection(Constants.KEY_COLLECTION_PARKING)
                .document(companyId);

        // Reference to the specific slot within the company document
        String slotName = "slot" + slotNumber;

        // Check the current status of the slot
        companyReference.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String currentStatus = documentSnapshot.getString(slotName);
                if (currentStatus != null) {
                    // Toggle the status
                    String newStatus = currentStatus.equals("Available") ? "Unavailable" : "Available";

                    // Set background based on the new status
                    if (newStatus.equals("Available")) {
                        cardView.setBackgroundResource(R.drawable.parkingsolid);
                    } else {
                        cardView.setBackgroundResource(R.drawable.noparking);
                    }

                    // Update slot status in the database
                    companyReference.update(slotName, newStatus)
                            .addOnSuccessListener(aVoid -> showToast("Slot status updated"))
                            .addOnFailureListener(e -> showToast("Failed to update slot status: " + e.getMessage()));
                } else {
                    showToast("Slot status not found");
                }
            } else {
                showToast("Document does not exist");
            }
        }).addOnFailureListener(e -> showToast("Error: " + e.getMessage()));
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}