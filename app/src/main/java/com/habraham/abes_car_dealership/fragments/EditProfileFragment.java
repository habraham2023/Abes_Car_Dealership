package com.habraham.abes_car_dealership.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.habraham.abes_car_dealership.R;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class EditProfileFragment extends Fragment {
    ImageView ivProfileImage;
    Toolbar toolbar;

    TextInputLayout screenNameInputLayout;
    TextInputEditText screenNameEditText;
    TextInputLayout bioInputLayout;
    TextInputEditText bioEditText;

    MaterialButton btnTakeNew;
    MaterialButton btnSave;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = view.findViewById(R.id.toolbar);
        ivProfileImage = view.findViewById(R.id.ivProfilePicture);
        btnTakeNew = view.findViewById(R.id.btnTakeNew);
        btnSave = view.findViewById(R.id.btnSave);

        screenNameInputLayout = view.findViewById(R.id.screenNameTextInput);
        screenNameEditText = view.findViewById(R.id.screenNameEditText);
        bioInputLayout = view.findViewById(R.id.bioTextInput);
        bioEditText = view.findViewById(R.id.bioEditText);

        toolbar.setNavigationIcon(R.drawable.back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(EditProfileFragment.this).commit();
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        screenNameEditText.setText(ParseUser.getCurrentUser().getString("screenName"));
        bioEditText.setText(ParseUser.getCurrentUser().getString("bio"));

        Glide.with(getContext()).load(ParseUser.getCurrentUser()
                .getParseFile("profilePicture")
                .getUrl())
                .transform(new CircleCrop())
                .into(ivProfileImage);

        btnTakeNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                launchCamera();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newScreenName = screenNameEditText.getText().toString();
                String newBio = bioEditText.getText().toString();
                boolean error = false;
                if (newScreenName.isEmpty()) {
                    screenNameInputLayout.setError("Screen name cannot be empty.");
                    error = true;
                }
                if (newBio.isEmpty()) {
                    bioInputLayout.setError("Bio cannot be empty.");
                    error = true;
                }
                if (error) return;

                ParseUser user = ParseUser.getCurrentUser();
                user.put("screenName", newScreenName);
                user.put("bio", newBio);

                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        getActivity().getSupportFragmentManager().beginTransaction().remove(EditProfileFragment.this).commit();
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                });
            }
        });
    }


}