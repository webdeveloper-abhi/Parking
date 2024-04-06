package com.example.parkingadmin.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.parkingadmin.PreferenceManager.PreferenceManager;
import com.example.parkingadmin.R;
import com.example.parkingadmin.Utilities.Constants;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class ParkingSlots extends Fragment {

    PreferenceManager preferenceManager;

    EditText totalslots, availableslots, bookedslots;
    Button btnmodifyslot;
    boolean toadd = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parking_slots, container, false);

        preferenceManager=new PreferenceManager(getContext());

        totalslots = view.findViewById(R.id.totalparkingslots);
        availableslots = view.findViewById(R.id.availableparkingslots);
        bookedslots = view.findViewById(R.id.bookedparkingslots);
        btnmodifyslot = view.findViewById(R.id.modifyslots);

        btnmodifyslot.setOnClickListener(v -> modifySlots());

        return view;
    }

    private void modifySlots() {
        if (totalslots.getText().toString().isEmpty() ||
                availableslots.getText().toString().isEmpty() ||
                bookedslots.getText().toString().isEmpty()) {
            showToast("Please Fill all the fields...");
            return;
        }

        int total = Integer.parseInt(totalslots.getText().toString());
        int available = Integer.parseInt(availableslots.getText().toString());
        int booked = Integer.parseInt(bookedslots.getText().toString());

        if (total != (available + booked)) {
            showToast("Invalid Parking Slots Information is given...");
            return;
        }

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference adminRef = database.collection(Constants.KEY_COLLECTION_ADMIN)
                .document(preferenceManager.getString(Constants.KEY_UserId));

        // Check if adminRef is not null
        if (adminRef == null) {
            showToast("Admin document reference is null.");
            return;
        }

        // Construct the reference to the parking slots document
        String parkingSlotsDocumentId = preferenceManager.getString(Constants.KEY_PARKING_SLOTS);
        if (parkingSlotsDocumentId == null) {
            showToast("Parking slots document ID is null.");
            return;
        }

        DocumentReference parkingslots = adminRef.collection("slots").document(parkingSlotsDocumentId);

        // Check if parkingslots is not null
        if (parkingslots == null) {
            showToast("Parking slots document reference is null.");
            return;
        }

        // Update the admin document with parking slots information
        Map<String, Object> userData = new HashMap<>();
        userData.put(Constants.KEY_TOTALSLOTS, total);
        userData.put(Constants.KEY_AVAILABLESLOTS, available);
        userData.put(Constants.KEY_BOOKEDSLOTS, booked);

        adminRef.update(userData)
                .addOnFailureListener(e -> showToast("Failed to update Parking slots number : " + e.toString()));

        // Add parking slots to the "parking_slots" subcollection
        for (int i = 1; i <= total; i++) {
            Map<String, Object> parkingSlot = new HashMap<>();
            parkingSlot.put("Id", "parking_slot_" + i);
            parkingSlot.put("status", "Available");

            parkingslots.collection("parking_slots").document("parking_slot_" + i)
                    .set(parkingSlot)
                    .addOnFailureListener(e -> showToast("Failed to update parking slot: " + e.toString()));
        }
    }





    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
