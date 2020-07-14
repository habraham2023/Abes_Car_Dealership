package com.habraham.abes_car_dealership.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.habraham.abes_car_dealership.R;
import com.habraham.abes_car_dealership.adapters.ListingsAdapter;
import com.habraham.abes_car_dealership.models.Listing;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MyListingsFragment extends Fragment {
    private static final String TAG = "MyListingsFragment";

    RecyclerView rvMyListings;
    ListingsAdapter adapter;
    List<Listing> myListings;

    public MyListingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_listings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvMyListings = view.findViewById(R.id.rvMyListings);
        myListings = new ArrayList<>();
        adapter = new ListingsAdapter(getContext(), myListings);
        rvMyListings.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMyListings.setAdapter(adapter);
        Log.i(TAG, "done: ");

        getMyListings();
    }

    private void getMyListings() {

        ParseQuery<Listing> myListingsQuery = ParseQuery.getQuery(Listing.class);
        myListingsQuery.whereEqualTo(Listing.KEY_SELLER, ParseUser.getCurrentUser());

        myListingsQuery.findInBackground(new FindCallback<Listing>() {
            @Override
            public void done(List<Listing> listings, ParseException e) {
                myListings.clear();
                myListings.addAll(listings);
                Log.i(TAG, "done: " + myListings);
                adapter.set(listings);
            }
        });
    }


}