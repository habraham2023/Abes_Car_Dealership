package com.habraham.abes_car_dealership.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.habraham.abes_car_dealership.R;
import com.habraham.abes_car_dealership.adapters.ListingsAdapter;
import com.habraham.abes_car_dealership.models.Listing;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class ListingsFragment extends Fragment {
    Toolbar toolbar;
    RecyclerView rvListings;
    ListingsAdapter adapter;
    List<Listing> listings;
    public ListingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_listings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = view.findViewById(R.id.toolbar);
        rvListings = view.findViewById(R.id.rvListings);

        getListings();

        listings = new ArrayList<>();
        adapter = new ListingsAdapter(getContext(), listings);
        rvListings.setLayoutManager(new LinearLayoutManager(getContext()));
        rvListings.setAdapter(adapter);
    }

    private void getListings() {
        ParseQuery<Listing> query = ParseQuery.getQuery(Listing.class);

        query.findInBackground(new FindCallback<Listing>() {
            @Override
            public void done(List<Listing> newListings, ParseException e) {
                adapter.addAll(newListings);
            }
        });
    }
}