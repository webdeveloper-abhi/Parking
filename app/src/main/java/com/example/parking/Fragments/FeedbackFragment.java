package com.example.parking.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.parking.R;


public class FeedbackFragment extends Fragment {


    public FeedbackFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_feedback, container, false);

        RadioButton issue=view.findViewById(R.id.issues);
        RadioButton suggessions=view.findViewById(R.id.Suggestions);

        EditText feedback=view.findViewById(R.id.feedback);

        Button btn_feedback=view.findViewById(R.id.submitfeedback);

        btn_feedback.setOnClickListener(v -> {

            if(feedback.getText().toString().isEmpty()){
                showToast("Feedback is missing");
            }else{
                showToast("Your feedback is submitted . We will definately work on it");
            }



        });

        return view;
    }

    private void showToast(String message){
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}