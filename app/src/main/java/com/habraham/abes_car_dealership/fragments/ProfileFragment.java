package com.habraham.abes_car_dealership.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.material.button.MaterialButton;
import com.habraham.abes_car_dealership.OnboardingActivity;
import com.habraham.abes_car_dealership.R;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;


public class ProfileFragment extends Fragment {
    ImageView ivProfilePicture;
    TextView tvScreenName;
    TextView tvUsername;
    TextView tvBio;
    MaterialButton btnEditProfile;
    MaterialButton btnLogout;
    Toolbar toolbar;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivProfilePicture = view.findViewById(R.id.ivProfilePicture);
        tvScreenName = view.findViewById(R.id.tvScreenName);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvBio = view.findViewById(R.id.tvBio);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        toolbar = view.findViewById(R.id.toolbar);

        toolbar.setNavigationIcon(R.drawable.back);

        // Send user to previous fragment
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(ProfileFragment.this).commit();
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        ParseUser user = ParseUser.getCurrentUser();

        Glide.with(getContext()).load(user.getParseFile("profilePicture").getUrl()).transform(new CircleCrop()).into(ivProfilePicture);
        tvScreenName.setText(user.getString("screenName"));
        tvUsername.setText("@" + user.getUsername());

        if (user.getString("bio") == null)
            tvBio.setText("Set a Bio.");
        else
            tvBio.setText(user.getString("bio"));

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {
                        Intent intent = new Intent(getContext(), OnboardingActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditProfileFragment editProfileFragment = new EditProfileFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.rlContainer, editProfileFragment).addToBackStack(null).commit();
            }
        });
    }
}