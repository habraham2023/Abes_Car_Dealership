package com.habraham.abes_car_dealership.fragments;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.habraham.abes_car_dealership.models.Listing;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FavoritesFragment extends ListingsFragment {
    private static final String TAG = "FavoritesFragment";
    private static final double METERS_TO_MILES = 0.00062137f;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        make = model = year = sort = "";
        maxDistance = Integer.MAX_VALUE;
        super.onViewCreated(view, savedInstanceState);
        swipeContainer.setEnabled(false);
    }

    @Override
    protected void getListings(int page) {
        Log.i(TAG, "getListings: " + Listing.listingsFavorited);
        ParseQuery<Listing> query = ParseQuery.getQuery(Listing.class);
        query.orderByDescending(Listing.KEY_UPDATED_AT);
        setFilteredQuery(query, make, model, year, maxDistance, sort);

        query.findInBackground(new FindCallback<Listing>() {
            @Override
            public void done(List<Listing> listings, ParseException e) {
                Log.i(TAG, "done: " + listings.size());
                List<Listing> filtered = new ArrayList<>();
                for (Listing listing : listings) {
                    if (Listing.listingsFavorited.contains(listing.getObjectId())) {
                        filtered.add(listing);
                    }
                }
                Log.i(TAG, "done: " + filtered);
                shimmerFrameLayout.stopShimmerAnimation();
                shimmerFrameLayout.setVisibility(View.GONE);
                if (filtered.size() == 0) {
                    noResults.setVisibility(View.VISIBLE);
                    swipeContainer.setVisibility(View.GONE);
                } else {
                    swipeContainer.setVisibility(View.VISIBLE);
                    noResults.setVisibility(View.GONE);
                    adapter.clear();
                    adapter.addAll(filtered);
                }
            }
        });
    }

    @Override
    public void onFinishFilterDialog(Intent i) {
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        swipeContainer.setVisibility(View.GONE);
        shimmerFrameLayout.startShimmerAnimation();

        make = i.getStringExtra("make");
        model = i.getStringExtra("model");
        year = i.getStringExtra("year");
        maxDistance = i.getIntExtra("maxDistance", Integer.MAX_VALUE);
        sort = i.getStringExtra("sort");
        Log.i(TAG, "onFinishFilterDialog: " + make + " " + model + " " + year);

        ParseQuery<Listing> query = ParseQuery.getQuery(Listing.class);
        query.orderByDescending(Listing.KEY_UPDATED_AT);
        setFilteredQuery(query, make, model, year, maxDistance, sort);

        query.findInBackground(new FindCallback<Listing>() {
            @Override
            public void done(List<Listing> newListings, ParseException e) {
                for (int i = 0; i < newListings.size(); i++) {
                    if (!Listing.listingsFavorited.contains(newListings.get(i).getObjectId())) {
                        newListings.remove(i--);
                        continue;
                    }
                    ParseGeoPoint sellerLocation = newListings.get(i).getLatLng();
                    if (sellerLocation != null) {
                        double distance;
                        Location destination = new Location("destination");
                        destination.setLatitude(sellerLocation.getLatitude());
                        destination.setLongitude(sellerLocation.getLongitude());
                        distance = location.distanceTo(destination) * METERS_TO_MILES;
                        newListings.get(i).setDistance(distance);
                        if (distance > maxDistance) newListings.remove(i--);
                    }
                }

                if (sort != null && sort.equals("Distance: low to high"))
                    Collections.sort(newListings, new Comparator<Listing>() {
                        @Override
                        public int compare(Listing listing1, Listing listing2) {
                            return (int) (listing1.getDistance() - listing2.getDistance());
                        }
                    });
                else if (sort != null && sort.equals("Distance: high to low"))
                    Collections.sort(newListings, new Comparator<Listing>() {
                        @Override
                        public int compare(Listing listing1, Listing listing2) {
                            return (int) (listing2.getDistance() - listing1.getDistance());
                        }
                    });

                shimmerFrameLayout.stopShimmerAnimation();
                shimmerFrameLayout.setVisibility(View.GONE);
                if (newListings.size() == 0) {
                    noResults.setVisibility(View.VISIBLE);
                    swipeContainer.setVisibility(View.GONE);
                } else {
                    swipeContainer.setVisibility(View.VISIBLE);
                    noResults.setVisibility(View.GONE);
                    adapter.clear();
                    adapter.addAll(newListings);
                }
            }
        });
    }
}