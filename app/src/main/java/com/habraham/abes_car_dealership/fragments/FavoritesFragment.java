package com.habraham.abes_car_dealership.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.habraham.abes_car_dealership.R;
import com.habraham.abes_car_dealership.models.Listing;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends ListingsFragment {
    private static final String TAG = "FavoritesFragment";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeContainer.setEnabled(false);
    }

    @Override
    protected void getListings(int page) {
        Log.i(TAG, "getListings: " + Listing.listingsFavorited);
        ParseQuery<Listing> query = ParseQuery.getQuery(Listing.class);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<Listing>() {
            @Override
            public void done(List<Listing> listings, ParseException e) {
                List<Listing> filtered = new ArrayList<>();
                for (Listing listing : listings) {
                    if (Listing.listingsFavorited.contains(listing.getObjectId())) {
                        filtered.add(listing);
                    }
                }
                adapter.clear();
                adapter.addAll(filtered);
            }
        });
    }
}