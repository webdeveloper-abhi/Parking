package com.example.parking.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.parking.Databaseconstant.Constants;
import com.example.parking.PreferanceManager.PreferenceManager;
import com.example.parking.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class SignUp extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private int Gallart_code=1;
    String enableImage;

    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager=new PreferenceManager(getApplicationContext());

        binding.signuptosignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isvalidinput()){
                    signUp();
                }
            }
        });

        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addingimagetoprofile();
            }
        });

        binding.termsandconditions.setOnClickListener(v -> {

            Intent intent=new Intent(SignUp.this, Terms.class);

            startActivity(intent);
        });





    }

    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private boolean isvalidinput(){

        if(enableImage==null){
            showToast("Select Your Profile Image");
            return false;
        } else if (binding.signupname.getText().toString().isEmpty()) {
            showToast("Enter your name");
            return false;
        }else if(binding.signupemail.getText().toString().isEmpty()){
            showToast("Enter your Email");
            return false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(binding.signupemail.getText().toString()).matches()){
            showToast("Enter Valid email Format");
            return false;
        }else if(binding.signuppassword.getText().toString().isEmpty()){
            showToast("Enter Your Password");
            return false;
        }else if(binding.signuppassword.getText().toString().length()<8){
            showToast("Password Should be greater than or equals to 8");
            return false;
        }else if(binding.signupconfirmpassword.getText().toString().isEmpty()){
            showToast("Enter Confirm Password");
            return false;
        }else if(!binding.signuppassword.getText().toString().equals(binding.signupconfirmpassword.getText().toString())){
            showToast("Password and Confirm Password Not Matched");
            return false;
        }else if(!binding.termsCheckbox.isChecked()){
            showToast("Agree Terms and Conditions");
            return false;
        }
        else return true;
    }

    private  void signUp(){

        loader(true);

        FirebaseFirestore database=FirebaseFirestore.getInstance();

        HashMap<String, Object> user=new HashMap<>();

        user.put(Constants.KEY_Name,binding.signupname.getText().toString());
        user.put(Constants.KEY_Email,binding.signupemail.getText().toString());
        user.put(Constants.KEY_Password,binding.signuppassword.getText().toString());
        user.put(Constants.KEY_Image,enableImage);


        database.collection(Constants.KEY_COLLECTION_USERS).add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        loader(true);
                        preferenceManager.putboolean(Constants.KEY_Is_Signed_In,true);
                        preferenceManager.putString(Constants.KEY_UserId,documentReference.getId());
                        preferenceManager.putString(Constants.KEY_Image,enableImage);
                        preferenceManager.putString(Constants.KEY_Name, binding.signupname.getText().toString());
                        preferenceManager.putString(Constants.KEY_Email,binding.signupemail.getText().toString());
                        preferenceManager.putString(Constants.KEY_Password,binding.signuppassword.getText().toString());

                        Intent intent=new Intent(SignUp.this, MainActivity.class);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        loader(false);
                        showToast(e.toString());
                    }
                });


    }

    private void loader(Boolean isloading){

        if(isloading){
            binding.btnsignup.setVisibility(View.INVISIBLE);
            binding.signupprogressbar.setVisibility(View.VISIBLE);
        }else{
            binding.btnsignup.setVisibility(View.VISIBLE);
            binding.signupprogressbar.setVisibility(View.INVISIBLE);
        }
    }

    private String encodedImage(Bitmap bitmap){

        int previewwidth=150;
        int previewheight=bitmap.getHeight()*previewwidth/bitmap.getWidth();

        Bitmap previewbitmap=Bitmap.createScaledBitmap(bitmap,previewwidth,previewheight,false);

        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        previewbitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);

        byte[] bytes=byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(bytes,Base64.DEFAULT);

    }

    private void addingimagetoprofile(){

        Intent intent=new Intent(Intent.ACTION_PICK);

        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent,Gallart_code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == Gallart_code && data != null) {

                Uri selectedImageUri = data.getData();

                try {

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);


                    String encodedImage = encodedImage(bitmap);


                    enableImage = encodedImage;


                    binding.profileImage.setImageBitmap(bitmap);
                    binding.addImage.setVisibility(View.GONE);
                    enableImage=encodedImage(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}