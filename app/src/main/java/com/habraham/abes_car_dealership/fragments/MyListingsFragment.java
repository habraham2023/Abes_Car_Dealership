package com.habraham.abes_car_dealership.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.habraham.abes_car_dealership.EndlessRecyclerViewScrollListener;
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
    Toolbar toolbar;
    FloatingActionButton fabAddListing;
    EndlessRecyclerViewScrollListener scrollListener;

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
        toolbar = view.findViewById(R.id.toolbar);
        fabAddListing  = view.findViewById(R.id.fabAddListing);

        myListings = new ArrayList<>();
        adapter = new ListingsAdapter(getContext(), myListings, null, this);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rvMyListings.setLayoutManager(llm);
        rvMyListings.setAdapter(adapter);
        scrollListener = new EndlessRecyclerViewScrollListener(llm) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i(TAG, "onLoadMore: " + page);
                getMyListings(page);
            }
        };
        rvMyListings.addOnScrollListener(scrollListener);
        getMyListings(0);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.miSettings:
                        ProfileFragment profileFragment = new ProfileFragment();
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.rlContainer, profileFragment).addToBackStack(null).commit();
                        break;
                }
                return true;
            }
        });

        fabAddListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreationFragment creationFragment = new CreationFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.rlContainer, creationFragment).addToBackStack(null).commit();
            }
        });
    }

    // Get all listings that the user has created
    private void getMyListings(final int page) {
        ParseQuery<Listing> myListingsQuery = ParseQuery.getQuery(Listing.class);
        myListingsQuery.whereEqualTo(Listing.KEY_SELLER, ParseUser.getCurrentUser());
        myListingsQuery.orderByDescending("createdAt");
        myListingsQuery.setLimit(Listing.QUERY_SIZE);
        myListingsQuery.setSkip(Listing.QUERY_SIZE * page);
        myListingsQuery.findInBackground(new FindCallback<Listing>() {
            @Override
            public void done(List<Listing> listings, ParseException e) {
                if(page == 0) adapter.clear();
                Log.i(TAG, "done: " + listings.size());
                adapter.addAll(listings);
            }
        });
    }


}