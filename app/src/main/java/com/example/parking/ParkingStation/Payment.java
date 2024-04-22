package com.example.parking.ParkingStation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.parking.Databaseconstant.Constants;
import com.example.parking.PreferanceManager.PreferenceManager;
import com.example.parking.R;
import com.example.parking.databinding.ActivityPaymentBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class Payment extends AppCompatActivity {

    ActivityPaymentBinding binding;

    String slotname,parkingstation,adminnumber;

    String Amount;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager=new PreferenceManager(getApplicationContext());

        Intent intent=getIntent();

        slotname=intent.getStringExtra("slotname");

        parkingstation=intent.getStringExtra("parkingstation");

        adminnumber=intent.getStringExtra("adminnumber");

        setpaymentdetails();


    }

    private void setpaymentdetails(){

        binding.companyname.setText(parkingstation);

        binding.slot.setText(slotname);

        extractParkingAmount();



        binding.pay.setOnClickListener(v -> {

            paymentoperation();
        });

    }

    private void paymentoperation(){

        Intent intent=new Intent(Payment.this, BookingConformation.class);

        intent.putExtra("amount",Amount);
        intent.putExtra("slotname",slotname);
        intent.putExtra("parkingstation",parkingstation);

        startActivity(intent);

    }

    private void extractParkingAmount() {

        FirebaseFirestore database = FirebaseFirestore.getInstance();


        database.collection(Constants.KEY_COLLECTION_ADMIN)
                .whereEqualTo("adminnumber", adminnumber)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                        binding.amount.setText(documentSnapshot.getString("amount"));

                        Amount=documentSnapshot.getString("amount");


                    } else {
                        showToast("Document does not exist");
                    }
                })
                .addOnFailureListener(e -> showToast("Error: " + e.toString()));
    }



    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}