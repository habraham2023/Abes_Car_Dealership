package com.habraham.abes_car_dealership.fragments;

import android.os.Bundle;
import android.util.Log;
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
import com.habraham.abes_car_dealership.models.Favorite;
import com.habraham.abes_car_dealership.models.Listing;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class ListingsFragment extends Fragment {
    private static final String TAG = "ListingsFragment";

    Toolbar toolbar;
    RecyclerView rvListings;
    protected ListingsAdapter adapter;

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

        adapter = new ListingsAdapter(getContext(), new ArrayList<Listing>());
        rvListings.setLayoutManager(new LinearLayoutManager(getContext()));
        rvListings.setAdapter(adapter);

        adapter.clear();
        getListings();
    }

    // Get all initial listings to be displayed
    protected void getListings() {
        Listing.getAllListingsFavorited(new FindCallback<Favorite>() {
            @Override
            public void done(List<Favorite> favorites, ParseException e) {
                Listing.listingsFavorited.clear();
                for (Favorite favorite : favorites) {
                    Listing.listingsFavorited.add(favorite.getListing().getObjectId());
                }
                Log.i(TAG, "done: " + Listing.listingsFavorited);
                ParseQuery<Listing> query = ParseQuery.getQuery(Listing.class);
                query.orderByDescending("createdAt");
                query.findInBackground(new FindCallback<Listing>() {
                    @Override
                    public void done(List<Listing> newListings, ParseException e) {
                        adapter.clear();
                        adapter.addAll(newListings);
                    }
                });
            }
        });
    }
}