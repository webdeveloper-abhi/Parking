package com.example.parking.Fragments;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parking.Databaseconstant.Constants;
import com.example.parking.PreferanceManager.PreferenceManager;
import com.example.parking.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;


public class ViewProfileFragment extends Fragment {

    public ViewProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        PreferenceManager preferenceManager=new PreferenceManager(requireContext());

        View view= inflater.inflate(R.layout.fragment_view_profile, container, false);

        ImageView image=view.findViewById(R.id.fragment_profile_image);
        TextView  name=view.findViewById(R.id.fragment_profile_name);
        TextView  email=view.findViewById(R.id.fragment_profile_email);
        TextView  phone=view.findViewById(R.id.fragment_profile_phone);
        Button editpassword=view.findViewById(R.id.fragment_edit_password);
    

        name.setText(preferenceManager.getString(Constants.KEY_Name));
        email.setText(preferenceManager.getString(Constants.KEY_Email));

        String bitmapString = preferenceManager.getString(Constants.KEY_Image);

        Bitmap bitmap = stringToBitmap(bitmapString);

        image.setImageBitmap(bitmap);


        phone.setText(preferenceManager.getString(Constants.KEY_Phone));


        //Listeners For Buttons

        editpassword.setOnClickListener(new View.OnClickListener() {
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

                        DocumentReference documentReference=database.collection(Constants.KEY_COLLECTION_USERS)
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

        return view;
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

    private void showToast(String message){
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}