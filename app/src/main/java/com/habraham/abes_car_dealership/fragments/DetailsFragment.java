package com.habraham.abes_car_dealership.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.habraham.abes_car_dealership.R;
import com.habraham.abes_car_dealership.models.Listing;

public class DetailsFragment extends Fragment {
    private static final String TAG = "DetailsFragment";

    private static final String LISTING = "listing";
    private Listing listing;

    Toolbar toolbar;
    TextView tvTitle;
    TextView tvDescription;
    TextView tvExtraInformation;

    public DetailsFragment() {
        // Required empty public constructor
    }

    public static DetailsFragment newInstance(Listing listing) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(LISTING, listing);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            listing = getArguments().getParcelable(LISTING);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "onViewCreated: " + listing.getDescription());

        toolbar = view.findViewById(R.id.toolbar);
        tvTitle = view.findViewById(R.id.tvTitle);
        tvDescription = view.findViewById(R.id.tvDescription);
        tvExtraInformation = view.findViewById(R.id.tvExtraInformation);

        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(DetailsFragment.this).commit();
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        tvTitle.setText(listing.getTitle());
        tvDescription.setText(listing.getDescription());
        tvExtraInformation.setText("THIS NEEDS TO BE IMPLEMENTED!");
    }
}