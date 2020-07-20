package com.habraham.abes_car_dealership.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
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

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.habraham.abes_car_dealership.MainActivity;
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
    private static final int _REQUEST_CODE_LOCATION_PERMISSION = 1;

    Toolbar toolbar;
    RecyclerView rvListings;
    protected ListingsAdapter adapter;
    LatLng location;

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

        getLocation();
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
                .requestLocationUpdates(locationRequest, new LocationCallback(){
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(getActivity()).removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int latestLocationIndex = locationResult.getLocations().size()-1;
                            double latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            double longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                            location = new LatLng(latitude, longitude);
                            Log.i(TAG, "onLocationResult: " + location);
                        }
                    }
                }, Looper.getMainLooper());
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