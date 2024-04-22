package com.example.parkingadmin.Fragments;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.parkingadmin.PreferenceManager.PreferenceManager;
import com.example.parkingadmin.R;
import com.example.parkingadmin.Utilities.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class ProfileFragment extends Fragment {



    public ProfileFragment() {
        // Required empty public constructor
    }

    private boolean isimagechanged=false;

    private String enableImage = "";

    PreferenceManager preferenceManager;

    FirebaseFirestore database;

    EditText name,email,phone,companyname,location,department;

    ImageView profileImage;
    Button changepassword,editprofile;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_profile, container, false);

        preferenceManager=new PreferenceManager(getContext());
        database = FirebaseFirestore.getInstance();

        name=view.findViewById(R.id.fragment_profile_name);
        email=view.findViewById(R.id.fragment_profile_email);
        phone=view.findViewById(R.id.fragment_profile_phone);
        companyname=view.findViewById(R.id.fragment_profile_company_name);
        location=view.findViewById(R.id.fragment_profile_location);
        profileImage=view.findViewById(R.id.fragment_profile_image);

        changepassword=view.findViewById(R.id.fragment_edit_password);
        editprofile=view.findViewById(R.id.fragment_edit_profile);

        String bitmapString = preferenceManager.getString(Constants.KEY_Image);

        Bitmap bitmap = stringToBitmap(bitmapString);

        profileImage.setImageBitmap(bitmap);

        setprofileinfo();

        listeners();


        return view;
    }

    private void setprofileinfo(){

        name.setText(preferenceManager.getString(Constants.KEY_Name));
        email.setText(preferenceManager.getString(Constants.KEY_Email));
        phone.setText(preferenceManager.getString(Constants.KEY_Phone));
        companyname.setText(preferenceManager.getString(Constants.KEY_COMPANY));
        location.setText(preferenceManager.getString(Constants.KEY_LOCATION));
    }

    private void listeners(){

        editprofile.setOnClickListener(v -> {
            updateUserProfile();
        });

        changepassword.setOnClickListener(v -> {

        });

        profileImage.setOnClickListener(v -> addingimagetoprofile());

        changepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog dialog=new Dialog(requireContext());

                dialog.setContentView(R.layout.editpassworddialog);

                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


                EditText oldpassword=dialog.findViewById(R.id.fragment_current_password);
                EditText newpassword=dialog.findViewById(R.id.fragment_new_password);
                Button change=dialog.findViewById(R.id.fragment_change_password);

                change.setOnClickListener(v1 -> {

                    if(oldpassword.getText().toString().isEmpty() && newpassword.getText().toString().isEmpty()){
                        showToast("All fields are Empty!");
                    }else if(oldpassword.getText().toString().isEmpty()){
                        showToast("Current Password Fiels is Empty!");
                    }else if(newpassword.getText().toString().isEmpty()){
                        showToast("New Password Field is Empty!");
                    }else if(!oldpassword.getText().toString().equals(preferenceManager.getString(Constants.KEY_Password))){
                        showToast("Incorrect Current Password!");
                    }else{

                        FirebaseFirestore database=FirebaseFirestore.getInstance();

                        DocumentReference documentReference=database.collection(Constants.KEY_COLLECTION_ADMIN)
                                .document(preferenceManager.getString(Constants.KEY_UserId));

                        HashMap<String,Object>update=new HashMap<>();

                        update.put(Constants.KEY_Password,newpassword.getText().toString());

                        documentReference.update(update)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        preferenceManager.putString(Constants.KEY_Password,newpassword.getText().toString());
                                        showToast("Password is Updated Successfully");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        showToast("Error: "+e.toString());

                                    }
                                });

                        dialog.dismiss();

                    }

                });

                dialog.show();





            }
        });

    }

    private void updateUserProfile() {
        String newName = name.getText().toString();
        String newEmail = email.getText().toString();
        String newPhone = phone.getText().toString();
        String newLocation=location.getText().toString();
        String newCompany=companyname.getText().toString();
        String newDepartment=department.getText().toString();

        DocumentReference userRef = database.collection(Constants.KEY_COLLECTION_ADMIN)
                .document(preferenceManager.getString(Constants.KEY_UserId));

        Map<String, Object> userData = new HashMap<>();
        userData.put(Constants.KEY_Name, newName);
        userData.put(Constants.KEY_Email, newEmail);
        userData.put(Constants.KEY_Phone, newPhone);
        userData.put(Constants.KEY_LOCATION,newLocation);
        userData.put(Constants.KEY_COMPANY,newCompany);
        userData.put(Constants.KEY_DEPARTMENT,newDepartment);

        if(isimagechanged){
            userData.put(Constants.KEY_Image, enableImage);
        }else{
            userData.put(Constants.KEY_Image, preferenceManager.getString(Constants.KEY_Image));
        }

        userRef.update(userData)
                .addOnSuccessListener(aVoid -> {
                    showToast("Profile updated successfully");
                    preferenceManager.putString(Constants.KEY_Name, newName);
                    preferenceManager.putString(Constants.KEY_Email, newEmail);
                    preferenceManager.putString(Constants.KEY_Phone, newPhone);
                    preferenceManager.putString(Constants.KEY_LOCATION,newLocation);
                    preferenceManager.putString(Constants.KEY_COMPANY,newCompany);
                    preferenceManager.putString(Constants.KEY_DEPARTMENT,newDepartment);


                    if (isimagechanged) {
                        preferenceManager.putString(Constants.KEY_Image, enableImage);
                    }
                })
                .addOnFailureListener(e -> showToast("Failed to update profile: " + e.getMessage()));
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private String encodedImage(Bitmap bitmap) {
        int previewwidth = 150;
        int previewheight = bitmap.getHeight() * previewwidth / bitmap.getWidth();

        Bitmap previewbitmap = Bitmap.createScaledBitmap(bitmap, previewwidth, previewheight, false);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewbitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);

        byte[] bytes = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private void addingimagetoprofile() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 1 && data != null) {
            Uri selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), selectedImageUri);
                String encodedImage = encodedImage(bitmap);
                profileImage.setImageBitmap(bitmap);
                enableImage = encodedImage;
                isimagechanged = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
}