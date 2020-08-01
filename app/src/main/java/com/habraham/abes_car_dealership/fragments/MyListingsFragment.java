package com.habraham.abes_car_dealership.fragments;

import android.content.DialogInterface;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.habraham.abes_car_dealership.EndlessRecyclerViewScrollListener;
import com.habraham.abes_car_dealership.R;
import com.habraham.abes_car_dealership.adapters.ListingsAdapter;
import com.habraham.abes_car_dealership.databinding.FragmentMyListingsBinding;
import com.habraham.abes_car_dealership.models.Chat;
import com.habraham.abes_car_dealership.models.Favorite;
import com.habraham.abes_car_dealership.models.Listing;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MyListingsFragment extends Fragment {
    private static final String TAG = "MyListingsFragment";

    RecyclerView rvMyListings;
    ListingsAdapter adapter;
    List<Listing> myListings;
    Toolbar toolbar;
    FloatingActionButton fabAddListing;
    EndlessRecyclerViewScrollListener scrollListener;
    private FragmentMyListingsBinding binding;
    public MyListingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMyListingsBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvMyListings = binding.rvMyListings;
        toolbar = binding.toolbar;
        fabAddListing = binding.fabAddListing;

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

        ItemTouchHelper.SimpleCallback rvCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.RIGHT) {
                    Log.i(TAG, "onLongClick: is MyListingsFragment");
                    new MaterialAlertDialogBuilder(getContext())
                            .setTitle("Delete this listing?")
                            .setMessage(myListings.get(viewHolder.getAdapterPosition()).getTitle())
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Log.i(TAG, "onClick: Don't Delete");
                                }
                            })
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Log.i(TAG, "onClick: Do Delete");
                                    deleteListing(viewHolder.getAdapterPosition());
                                }
                            }).show();
                }
            }

            private void deleteListing(final int position) {
                final Listing toBeDeleted = myListings.get(position);
                ParseQuery<Chat> relatedChatsQuery = ParseQuery.getQuery(Chat.class);
                relatedChatsQuery.whereEqualTo(Chat.KEY_LISTING, toBeDeleted);
                Log.i(TAG, "deleteListing: ");
                relatedChatsQuery.findInBackground(new FindCallback<Chat>() {
                    @Override
                    public void done(List<Chat> chats, ParseException e) {
                        Log.i(TAG, "done: " + chats);
                        for (Chat chat : chats) chat.deleteInBackground();
                        ParseQuery<Favorite> favoriteParseQuery = ParseQuery.getQuery(Favorite.class);
                        favoriteParseQuery.whereEqualTo(Favorite.KEY_LISTING, toBeDeleted);
                        favoriteParseQuery.findInBackground(new FindCallback<Favorite>() {
                            @Override
                            public void done(List<Favorite> favorites, ParseException e) {
                                for (Favorite favorite : favorites) favorite.deleteInBackground();
                                toBeDeleted.deleteInBackground(new DeleteCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        myListings.remove(position);
                                        adapter.notifyItemRemoved(position);

                                    }
                                });
                            }
                        });
                    }
                });
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(getContext(), R.color.cardinal))
                        .addSwipeRightActionIcon(R.drawable.delete)
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(rvCallback);
        itemTouchHelper.attachToRecyclerView(rvMyListings);

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