package com.example.parkingadmin.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parkingadmin.Fragments.Home;
import com.example.parkingadmin.Fragments.ParkingSlots;
import com.example.parkingadmin.Fragments.ProfileFragment;
import com.example.parkingadmin.PreferenceManager.PreferenceManager;
import com.example.parkingadmin.R;
import com.example.parkingadmin.Utilities.Constants;
import com.example.parkingadmin.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    DrawerLayout drawerLayout;
    PreferenceManager preferenceManager;
    ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseApp.initializeApp(this);

        preferenceManager = new PreferenceManager(MainActivity.this);

        drawerLayout = binding.drawerlayout;

        setSupportActionBar(binding.toolbar);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, binding.toolbar, R.string.opendrawer, R.string.closedrawer);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        setAdminDetails();
        manageNavigationDrawer();
        loadFragment(new Home());

    }



    private void manageNavigationDrawer() {
        binding.navigationdrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemId = item.getItemId();

                if(itemId==R.id.profile){

                    loadFragment(new ProfileFragment());
                }else if(itemId==R.id.home){
                    loadFragment(new Home());
                }else if(itemId==R.id.parkingslots){
                    loadFragment(new ParkingSlots());
                }

                binding.drawerlayout.closeDrawer(GravityCompat.START);

                return true;
            }
        });
    }

    private void loadFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.framelayout, fragment);
        fragmentTransaction.commit();
    }

    private void setAdminDetails() {
        View headerLayout = binding.navigationdrawer.getHeaderView(0);

        TextView name = headerLayout.findViewById(R.id.adminname);
        TextView email = headerLayout.findViewById(R.id.adminemail);
        ImageView profileImage = headerLayout.findViewById(R.id.header_profile_image);

        name.setText(preferenceManager.getString(Constants.KEY_Name));
        email.setText(preferenceManager.getString(Constants.KEY_Email));

        String preferenceImgString = preferenceManager.getString(Constants.KEY_Image);
        Bitmap image = convertStringToBitmap(preferenceImgString);

        if (image != null) {
            profileImage.setImageBitmap(image);
        } else {
            showToast("Image is not found for profile");
        }
    }

    private Bitmap convertStringToBitmap(String preferenceImgString) {
        if (preferenceImgString != null && !preferenceImgString.isEmpty()) {
            byte[] decodedString = Base64.decode(preferenceImgString, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        }
        return null;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    private void signOut() {
        showToast("Signed Out....");
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_ADMIN)
                        .document(preferenceManager.getString(Constants.KEY_UserId));

        HashMap<String, Object> updates = new HashMap<>();
        documentReference.update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        preferenceManager.clear();
                        Intent intent = new Intent(MainActivity.this, SignIn.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast("Unable to SignOut...");
                    }
                });
    }

    @Override
    public void onBackPressed() {

        if(binding.drawerlayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerlayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }

    }

}
