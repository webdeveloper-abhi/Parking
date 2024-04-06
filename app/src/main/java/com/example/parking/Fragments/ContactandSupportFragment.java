package com.example.parking.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.parking.R;

public class ContactandSupportFragment extends Fragment {

    private final String emailAddress = "kaleabhi2004@gmail.com";

    public ContactandSupportFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contactand_support, container, false);

        EditText name = view.findViewById(R.id.name);
        EditText email = view.findViewById(R.id.email);
        EditText message = view.findViewById(R.id.message);




        Button send = view.findViewById(R.id.send);
        send.setOnClickListener(v -> {

            String emailBody = "Name: " + name.getText().toString() + "\n"
                    + "Email: " + email.getText().toString() + "\n"
                    + "Message: " + message.getText().toString();

            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:" + Uri.encode(emailAddress)));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Support Request");
            emailIntent.putExtra(Intent.EXTRA_TEXT, emailBody);

            startActivity(emailIntent);

        });

        return view;
    }
}
