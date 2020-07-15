package com.habraham.abes_car_dealership.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.habraham.abes_car_dealership.R;
import com.habraham.abes_car_dealership.models.Listing;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class CreationFragment extends Fragment {
    private static final String TAG = "CreationFragment";

    TextInputLayout titleInputLayout;
    TextInputEditText titleEditText;
    TextInputLayout descriptionInputLayout;
    TextInputEditText descriptionEditText;
    MaterialButton btnCreateListing;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_creation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        titleInputLayout = view.findViewById(R.id.titleTextInput);
        titleEditText = view.findViewById(R.id.titleEditText);
        descriptionInputLayout = view.findViewById(R.id.descriptionTextInput);
        descriptionEditText = view.findViewById(R.id.descriptionEditText);
        btnCreateListing = view.findViewById(R.id.btnCreateListing);

        btnCreateListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean error = false;

                String title = titleEditText.getText().toString();
                if (title.isEmpty()) {
                    titleInputLayout.setError("Title of listing cannot be empty.");
                    error = true;
                }
                String description = descriptionEditText.getText().toString();
                if (description.isEmpty()) {
                    descriptionInputLayout.setError("Description of listing cannot be empty");
                    error = true;
                }
                if (!error) createListing(title, description);
            }
        });
    }

    public void createListing(String title, String description) {
        titleInputLayout.setError(null);
        descriptionInputLayout.setError(null);

        Listing listing = new Listing();
        listing.setTitle(title);
        listing.setDescription(description);
        listing.setSeller(ParseUser.getCurrentUser());

        listing.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(CreationFragment.this).commit();
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }
}