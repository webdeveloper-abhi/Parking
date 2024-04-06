package com.example.parking.Fragments;

import static android.app.Activity.RESULT_OK;

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

import com.example.parking.Databaseconstant.Constants;
import com.example.parking.PreferanceManager.PreferenceManager;
import com.example.parking.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EditProfileFragment extends Fragment {

    private ImageView profileImage;
    private EditText name, email, phone;
    private Button editProfileButton;

    private boolean isimagechanged=false;

    private String enableImage = "";

    PreferenceManager preferenceManager;

    FirebaseFirestore database;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        database = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getContext());

        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        profileImage = view.findViewById(R.id.profile_image);
        name = view.findViewById(R.id.text_name);
        email = view.findViewById(R.id.text_email);
        phone = view.findViewById(R.id.text_phone);
        editProfileButton = view.findViewById(R.id.btn_edit_profile);

        name.setText(preferenceManager.getString(Constants.KEY_Name));
        email.setText(preferenceManager.getString(Constants.KEY_Email));
        phone.setText(preferenceManager.getString(Constants.KEY_Phone));

        String bitmapString = preferenceManager.getString(Constants.KEY_Image);

        Bitmap bitmap = stringToBitmap(bitmapString);

        profileImage.setImageBitmap(bitmap);

        profileImage.setOnClickListener(v -> addingimagetoprofile());

        editProfileButton.setOnClickListener(v -> updateUserProfile());

        return view;
    }

    private void updateUserProfile() {
        String newName = name.getText().toString();
        String newEmail = email.getText().toString();
        String newPhone = phone.getText().toString();

        DocumentReference userRef = database.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_UserId));

        Map<String, Object> userData = new HashMap<>();
        userData.put(Constants.KEY_Name, newName);
        userData.put(Constants.KEY_Email, newEmail);
        userData.put(Constants.KEY_Phone, newPhone);

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

                    // Update the stored image string if it's changed
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
