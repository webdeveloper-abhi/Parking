package com.example.parkingadmin.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.parkingadmin.PreferenceManager.PreferenceManager;

import androidx.annotation.NonNull;
import android.content.Intent;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.parkingadmin.Utilities.Constants;
import com.example.parkingadmin.databinding.ActivitySignInBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class SignIn extends AppCompatActivity {

    private ActivitySignInBinding binding;

    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager=new PreferenceManager(getApplicationContext());

        if(preferenceManager.getboolean(Constants.KEY_Is_Signed_In)){
            Intent intent=new Intent(SignIn.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        binding.signincreateaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignIn.this, SignUp.class);
                startActivity(intent);
            }
        });

        binding.btnsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isvalidInput()){
                    signIn();
                }
            }
        });
    }

    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private boolean isvalidInput(){

        if(binding.signinemail.getText().toString().trim().isEmpty()){
            showToast("Enter Your Email");
            return false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(binding.signinemail.getText().toString()).matches()){
            showToast("Enter Valid Email Address");
            return false;
        }else if(binding.signinpassword.getText().toString().trim().isEmpty()){
            showToast("Enter Your Passwprd");
            return false;
        }else{
            return true;
        }
    }

    private void loader(boolean loading){

        if(loading){
            binding.btnsignin.setVisibility(View.INVISIBLE);
            binding.signinprogressbar.setVisibility(View.VISIBLE);
        }else{
            binding.btnsignin.setVisibility(View.VISIBLE);
            binding.signinprogressbar.setVisibility(View.INVISIBLE);
        }
    }

    private void signIn(){

        loader(true);

        FirebaseFirestore database =FirebaseFirestore.getInstance();

        database.collection(Constants.KEY_COLLECTION_ADMIN)
                .whereEqualTo(Constants.KEY_Email,binding.signinemail.getText().toString())
                .whereEqualTo(Constants.KEY_Password,binding.signinpassword.getText().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if(task.isSuccessful() && task.getResult()!=null && task.getResult().getDocuments().size()>0){

                            //Add the login details to shared preferance

                            DocumentSnapshot documentSnapshot=task.getResult().getDocuments().get(0);

                            preferenceManager.putboolean(Constants.KEY_Is_Signed_In,true);
                            preferenceManager.putString(Constants.KEY_Image,documentSnapshot.getString(Constants.KEY_Image));
                            preferenceManager.putString(Constants.KEY_UserId,documentSnapshot.getId());
                            preferenceManager.putString(Constants.KEY_Name,documentSnapshot.getString(Constants.KEY_Name));
                            preferenceManager.putString(Constants.KEY_Email,documentSnapshot.getString(Constants.KEY_Email));
                            preferenceManager.putString(Constants.KEY_Password,documentSnapshot.getString(Constants.KEY_Password));
                            preferenceManager.putString(Constants.KEY_COMPANY,documentSnapshot.getString(Constants.KEY_COMPANY));
                            preferenceManager.putString(Constants.KEY_Phone,documentSnapshot.getString(Constants.KEY_Phone));
                            preferenceManager.putString(Constants.KEY_LOCATION,documentSnapshot.getString(Constants.KEY_LOCATION));

                            Intent intent =new Intent(SignIn.this, MainActivity.class);
                            startActivity(intent);

                        }else{
                            loader(false);
                            showToast("Account with this Mail and password not exist");
                        }
                    }
                });


    }

}


