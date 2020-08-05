package com.habraham.abes_car_dealership.fragments;

import android.content.DialogInterface;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.habraham.abes_car_dealership.R;
import com.habraham.abes_car_dealership.models.Chat;
import com.habraham.abes_car_dealership.models.Favorite;
import com.habraham.abes_car_dealership.models.Listing;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MyListingsFragment extends ListingsFragment {
    private static final String TAG = "MyListingsFragment";
    private static final String ARG_SELLER = "seller";

    TextView tvMyListingsHeading;
    ParseUser user;

    public MyListingsFragment() {
        // Required empty public constructor
    }

    public static MyListingsFragment newInstance(ParseUser seller) {
        MyListingsFragment myListingsFragment = new MyListingsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_SELLER, seller);
        myListingsFragment.setArguments(args);
        return myListingsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getParcelable(ARG_SELLER);
        }
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvMyListingsHeading = binding.tvMyListingsHeading;
        tvMyListingsHeading.setVisibility(View.VISIBLE);

        if (user == null) {
            toolbar.inflateMenu(R.menu.menu_profile);
        } else {
            tvMyListingsHeading.setText(user.getString("screenName") + "'s Listings:");
            fab.setVisibility(View.GONE);
            toolbar.setNavigationIcon(R.drawable.back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().getSupportFragmentManager().beginTransaction().remove(MyListingsFragment.this).commit();
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });
        }

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

        swipeContainer.setEnabled(false);
        fab.setImageResource(R.drawable.add_listing);
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
                            .setMessage(adapter.getListing(viewHolder.getAdapterPosition()).getTitle())
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Log.i(TAG, "onClick: Don't Delete");
                                    adapter.notifyItemChanged(viewHolder.getAdapterPosition());
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
                final Listing toBeDeleted = adapter.getListing(position);
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
                                        adapter.remove(position);
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
                super.onChildDraw(c, recyclerView, viewHolder, dX / 4, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(rvCallback);
        itemTouchHelper.attachToRecyclerView(rvListings);

        getListings(0);
        getLocation();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreationFragment creationFragment = new CreationFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.rlContainer, creationFragment).addToBackStack(null).commit();
            }
        });
    }

    @Override
    // Get all listings that the user has created
    protected void getListings(final int page) {
        ParseQuery<Listing> myListingsQuery = ParseQuery.getQuery(Listing.class);
        if (user != null)
            myListingsQuery.whereEqualTo(Listing.KEY_SELLER, user);
        else
            myListingsQuery.whereEqualTo(Listing.KEY_SELLER, ParseUser.getCurrentUser());

        myListingsQuery.orderByDescending("createdAt");
        myListingsQuery.setLimit(Listing.QUERY_SIZE);
        myListingsQuery.setSkip(Listing.QUERY_SIZE * page);
        myListingsQuery.findInBackground(new FindCallback<Listing>() {
            @Override
            public void done(List<Listing> listings, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "done: ", e);
                    return;
                }

                shimmerFrameLayout.stopShimmerAnimation();
                shimmerFrameLayout.setVisibility(View.GONE);
                if (listings.size() == 0 && page == 0) {
                    noResults.setVisibility(View.VISIBLE);
                    swipeContainer.setVisibility(View.GONE);
                } else {
                    swipeContainer.setVisibility(View.VISIBLE);
                    noResults.setVisibility(View.GONE);
                    if (page == 0) adapter.clear();
                    Log.i(TAG, "done: " + listings.size());
                    adapter.addAll(listings);
                }
            }
        });

    }
}