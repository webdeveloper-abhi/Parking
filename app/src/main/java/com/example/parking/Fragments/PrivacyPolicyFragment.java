package com.example.parking.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.parking.R;

public class PrivacyPolicyFragment extends Fragment {

    public PrivacyPolicyFragment() {
        // Required empty public constructor
    }

    TextView Answer1, Answer2, Answer3, Answer4;

    ImageView Button1,Button2,Button3,Button4;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_privacy_policy, container, false);

        Answer1 = view.findViewById(R.id.answer1);
        Answer2 = view.findViewById(R.id.answer2);
        Answer3 = view.findViewById(R.id.answer3);
        Answer4 = view.findViewById(R.id.answer4);

        Button1=view.findViewById(R.id.addButton1);
        Button2=view.findViewById(R.id.addButton2);
        Button3=view.findViewById(R.id.addButton3);
        Button4=view.findViewById(R.id.addButton4);

        listeners();

        return view;
    }

    private void listeners(){
        Button1.setOnClickListener(v->{

            if(Answer1.getVisibility()==View.VISIBLE){
                Answer1.setVisibility(View.GONE);
            }else{
                Answer1.setVisibility(View.VISIBLE);
            }
        });

        Button2.setOnClickListener(v->{

            if(Answer2.getVisibility()==View.VISIBLE){
                Answer2.setVisibility(View.GONE);
            }else{
                Answer2.setVisibility(View.VISIBLE);
            }
        });

        Button3.setOnClickListener(v->{

            if(Answer3.getVisibility()==View.VISIBLE){
                Answer3.setVisibility(View.GONE);
            }else{
                Answer3.setVisibility(View.VISIBLE);
            }
        });

        Button4.setOnClickListener(v->{

            if(Answer4.getVisibility()==View.VISIBLE){
                Answer4.setVisibility(View.GONE);
            }else{
                Answer4.setVisibility(View.VISIBLE);
            }
        });
    }



}
