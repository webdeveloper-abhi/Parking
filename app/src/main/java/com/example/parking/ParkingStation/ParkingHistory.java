package com.example.parking.ParkingStation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.widget.Toast;

import com.example.parking.Adapters.ParkingHistoryAdapter;
import com.example.parking.Databaseconstant.Constants;
import com.example.parking.PreferanceManager.PreferenceManager;
import com.example.parking.RecycleciewStructures.ParkingHistoryStructure;
import com.example.parking.databinding.ActivityParkingHistoryBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ParkingHistory extends AppCompatActivity {

    ActivityParkingHistoryBinding binding;

    PreferenceManager preferenceManager;

    ArrayList<ParkingHistoryStructure> parkinghistory;
    ParkingHistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityParkingHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(this);

        binding.recycleview.setLayoutManager(new LinearLayoutManager(this));

        parkinghistory = new ArrayList<>();
        adapter = new ParkingHistoryAdapter(this, parkinghistory);

        binding.recycleview.setAdapter(adapter);

        addtoparkinghistoryarraylist();
    }

    private void addtoparkinghistoryarraylist() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        database.collection(Constants.KEY_COLLECTION_PARKING_HISTORY)
                .document(preferenceManager.getString(Constants.KEY_Email))
                .collection(Constants.KET_SUBCOLLECTION_History)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    parkinghistory.clear();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {

                        parkinghistory.add(new ParkingHistoryStructure(documentSnapshot.getString("parking_station_name"),documentSnapshot.getString("date"),documentSnapshot.getString("amount_paid")));
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> showToast("Failed to retrieve parking history: " + e.getMessage()));
    }

    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
