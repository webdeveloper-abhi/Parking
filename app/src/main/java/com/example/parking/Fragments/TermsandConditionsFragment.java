package com.example.parking.Fragments;

import android.Manifest;
import android.os.Bundle;



import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.parking.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.util.List;


public class TermsandConditionsFragment extends Fragment {




    public TermsandConditionsFragment() {
        // Required empty public constructor
    }

private  String url;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_termsand_conditions, container, false);

         Button downloadpdf=view.findViewById(R.id.downloadtandc);
         Button accept=view.findViewById(R.id.accept);
         Button decline=view.findViewById(R.id.decline);





         downloadpdf.setOnClickListener(v -> {

             Dexter.withContext(getContext())
                     .withPermissions(
                             Manifest.permission.READ_EXTERNAL_STORAGE,
                             Manifest.permission.WRITE_EXTERNAL_STORAGE

                     ).withListener(new MultiplePermissionsListener() {
                         @Override public void onPermissionsChecked(MultiplePermissionsReport report) {

                             if(report.areAllPermissionsGranted()){
                                 download();
                             }
                         }
                         @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
                     }).check();

         });

        return view;
    }

    private void download() {

        File file= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);


    }
}