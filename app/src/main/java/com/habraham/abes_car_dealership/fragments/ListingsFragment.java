package com.habraham.abes_car_dealership.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.habraham.abes_car_dealership.EndlessRecyclerViewScrollListener;
import com.habraham.abes_car_dealership.R;
import com.habraham.abes_car_dealership.adapters.ListingsAdapter;
import com.habraham.abes_car_dealership.models.Favorite;
import com.habraham.abes_car_dealership.models.Listing;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ListingsFragment extends Fragment implements FilterFragmentDialog.FilterDialogListener {
    private static final String TAG = "ListingsFragment";
    private static final int _REQUEST_CODE_LOCATION_PERMISSION = 1;
    private static final double METERS_TO_MILES = 0.00062137f;

    public Location location = new Location("location");
    protected ListingsAdapter adapter;
    Toolbar toolbar;
    RecyclerView rvListings;
    FloatingActionButton fabFilter;
    protected SwipeRefreshLayout swipeContainer;
    private EndlessRecyclerViewScrollListener scrollListener;

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
        fabFilter = view.findViewById(R.id.fabFilter);
        swipeContainer = view.findViewById(R.id.swipeContainer);

        adapter = new ListingsAdapter(getContext(), new ArrayList<Listing>(), location);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rvListings.setLayoutManager(llm);
        rvListings.setAdapter(adapter);

        scrollListener = new EndlessRecyclerViewScrollListener(llm) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i(TAG, "onLoadMore: " + page);
                getListings(page);
            }
        };
        rvListings.addOnScrollListener(scrollListener);
        adapter.clear();
        getListings(0);

        getLocation();

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getListings(0);
                scrollListener.resetState();
            }
        });

        fabFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FilterFragmentDialog filterFragmentDialog = FilterFragmentDialog.newInstance();
                filterFragmentDialog.setTargetFragment(ListingsFragment.this, 300);
                filterFragmentDialog.show(getActivity().getSupportFragmentManager(), "");
            }
        });
    }

    // Ask user for location permission if not yet given
    public void getLocation() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    _REQUEST_CODE_LOCATION_PERMISSION);
        } else {
            getCurrentLocation();
        }
    }

    @Override
    // Check to see if user has denied or accepted the permission request
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == _REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }
        }
    }

    @SuppressLint("MissingPermission")
    // Make single location request
    public void getCurrentLocation() {
        Log.i(TAG, "getCurrentLocation");
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(getActivity())
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(getActivity()).removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                            double latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            double longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                            location.setLatitude(latitude);
                            location.setLongitude(longitude);
                            Log.i(TAG, "onLocationResult: " + location);
                        }
                    }
                }, Looper.getMainLooper());
    }

    // Get all initial listings to be displayed
    protected void getListings(final int page) {
        Listing.getAllListingsFavorited(new FindCallback<Favorite>() {
            @Override
            public void done(List<Favorite> favorites, ParseException e) {
                if (page == 0) Listing.listingsFavorited.clear();
                for (Favorite favorite : favorites) {
                    Listing.listingsFavorited.add(favorite.getListing().getObjectId());
                }
                Log.i(TAG, "done: " + Listing.listingsFavorited);
                ParseQuery<Listing> query = ParseQuery.getQuery(Listing.class);
                query.orderByDescending("createdAt");
                query.setLimit(Listing.QUERY_SIZE);
                query.setSkip(Listing.QUERY_SIZE * page);
                query.findInBackground(new FindCallback<Listing>() {
                    @Override
                    public void done(List<Listing> newListings, ParseException e) {
                        if (page == 0) adapter.clear();
                        adapter.addAll(newListings);
                        swipeContainer.setRefreshing(false);
                        Log.i(TAG, "done: " + newListings.size());
                    }
                });
            }
        }, page);
    }

    @Override
    public void onFinishFilterDialog(Intent i) {
        String make = i.getStringExtra("make");
        String model = i.getStringExtra("model");
        String year = i.getStringExtra("year");
        final int maxDistance = i.getIntExtra("maxDistance", Integer.MAX_VALUE);
        final String sort = i.getStringExtra("sort");
        Log.i(TAG, "onFinishFilterDialog: " + make + " " + model + " " + year);

        ParseQuery<Listing> query = ParseQuery.getQuery(Listing.class);
        query.orderByDescending(Listing.KEY_UPDATED_AT);

        if (make != null && !make.isEmpty())
            query.whereEqualTo(Listing.KEY_MAKE, make);
        if (model != null && !model.isEmpty())
            query.whereEqualTo(Listing.KEY_MODEL, model);
        if (year != null && !year.isEmpty())
            query.whereEqualTo(Listing.KEY_YEAR, year);
        if (sort != null && !sort.isEmpty()) {
            switch (sort) {
                case "Price: low to high":
                    query.orderByAscending(Listing.KEY_PRICE);
                    break;
                case "Price: high to low":
                    query.orderByDescending(Listing.KEY_PRICE);
                    break;
                case "Most Recent":
                    query.orderByDescending(Listing.KEY_UPDATED_AT);
                    break;
                case "Least Recent":
                    query.orderByAscending(Listing.KEY_UPDATED_AT);
                    break;
            }
        }
        query.findInBackground(new FindCallback<Listing>() {
            @Override
            public void done(List<Listing> newListings, ParseException e) {
                for (int i = 0; i < newListings.size(); i++) {
                    ParseGeoPoint sellerLocation = newListings.get(i).getLatLng();
                    if (sellerLocation != null) {
                        double distance;
                        Location destination = new Location("destination");
                        destination.setLatitude(sellerLocation.getLatitude());
                        destination.setLongitude(sellerLocation.getLongitude());
                        distance = location.distanceTo(destination) * METERS_TO_MILES;
                        newListings.get(i).setDistance(distance);
                        if (distance > maxDistance) newListings.remove(newListings.get(i--));
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

                adapter.clear();
                adapter.addAll(newListings);
            }
        });
    }
}