package com.habraham.abes_car_dealership.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.habraham.abes_car_dealership.R;
import com.habraham.abes_car_dealership.fragments.DetailsFragment;
import com.habraham.abes_car_dealership.fragments.FavoritesFragment;
import com.habraham.abes_car_dealership.fragments.MyListingsFragment;
import com.habraham.abes_car_dealership.models.Chat;
import com.habraham.abes_car_dealership.models.Favorite;
import com.habraham.abes_car_dealership.models.Listing;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class ListingsAdapter extends RecyclerView.Adapter<ListingsAdapter.ViewHolder> {
    private static final String TAG = "ListingsAdapter";
    private static final double METERS_TO_MILES = 0.00062137f;
    private Context context;
    private List<Listing> listings;
    private Location location;
    private Fragment fragment;

    public ListingsAdapter(Context context, List<Listing> listings, Location location, Fragment fragment) {
        this.context = context;
        this.listings = listings;
        this.location = location;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.listing_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Listing listing = listings.get(position);
        holder.bind(listing);
    }

    @Override
    public int getItemCount() {
        return listings.size();
    }

    public void clear() {
        listings.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Listing> list) {
        listings.addAll(list);
        notifyDataSetChanged();
    }

    private void deleteListing(final int position) {
        final Listing toBeDeleted = listings.get(position);
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
                                listings.remove(position);
                                notifyItemRemoved(position);
                            }
                        });
                    }
                });
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView tvTitle;
        private TextView tvDescription;
        private TextView tvPrice;
        private TextView tvDistance;
        private ImageView ivFirstImage;
        private ImageView ivFavorite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            ivFirstImage = itemView.findViewById(R.id.ivFirstImage);
            ivFavorite = itemView.findViewById(R.id.ivFavorite);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void bind(final Listing listing) {
            tvTitle.setText(listing.getTitle());
            tvDescription.setText(listing.getDescription());
            tvPrice.setText(String.format("$%.2f", listing.getPrice()));
            if (Listing.listingsFavorited.contains(listing.getObjectId())) {
                Glide.with(context).load(R.drawable.favorite_fill).into(ivFavorite);
                ivFavorite.setColorFilter(context.getColor(R.color.secondaryDarkColor));
            } else {
                Glide.with(context).load(R.drawable.favorite).into(ivFavorite);
                ivFavorite.setColorFilter(Color.BLACK);
            }

            Glide.with(context).load(listing.getImages().get(0).getUrl()).into(ivFirstImage);


            ivFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Checks to see if listing has already been favorited
                    if (Listing.listingsFavorited.contains(listing.getObjectId())) {
                        ParseQuery<Favorite> deleteFavorite = ParseQuery.getQuery(Favorite.class);
                        deleteFavorite.whereEqualTo("user", ParseUser.getCurrentUser());
                        deleteFavorite.whereEqualTo("listing", listing);
                        deleteFavorite.getFirstInBackground(new GetCallback<Favorite>() {
                            @Override
                            public void done(Favorite favorite, ParseException e) {
                                // Remove favorite that user has previously made
                                favorite.deleteInBackground(new DeleteCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            Glide.with(context).load(R.drawable.favorite).into(ivFavorite);
                                            ivFavorite.setColorFilter(Color.BLACK);
                                            Listing.listingsFavorited.remove(listing.getObjectId());
                                            Log.i(TAG, "onClick: " + Listing.listingsFavorited);
                                            if (fragment instanceof FavoritesFragment) {
                                                listings.remove(getAdapterPosition());
                                                notifyItemRemoved(getAdapterPosition());
                                            }
                                        }
                                    }
                                });
                            }
                        });
                        return;
                    }

                    // If listing hasn't been favorited yet then do so
                    Favorite favorite = new Favorite();
                    favorite.setListing(listing);
                    favorite.setUser(ParseUser.getCurrentUser());

                    favorite.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            Log.i(TAG, "done: Saved Favorite");
                            Glide.with(context).load(R.drawable.favorite_fill).into(ivFavorite);
                            ivFavorite.setColorFilter(context.getColor(R.color.secondaryDarkColor));
                            Listing.listingsFavorited.add(listing.getObjectId());
                            Log.i(TAG, "onClick: " + Listing.listingsFavorited);
                        }
                    });
                }
            });

            if (location != null) {
                double distance;
                ParseGeoPoint sellerLocation = listing.getLatLng();
                if (sellerLocation != null) {
                    Location destination = new Location("destination");
                    destination.setLatitude(sellerLocation.getLatitude());
                    destination.setLongitude(sellerLocation.getLongitude());
                    distance = location.distanceTo(destination) * METERS_TO_MILES;
                    Log.i(TAG, "onClick: " + listing.getDistance());
                    String result = String.format(" %.2fmi", distance);
                    tvDistance.setText(result);
                }
            }
        }

        @Override
        public void onClick(View view) {
            DetailsFragment detailsFragment = DetailsFragment.newInstance(listings.get(getAdapterPosition()));
            ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.rlContainer, detailsFragment).addToBackStack(null).commit();
        }


        @Override
        public boolean onLongClick(View view) {
            if (fragment instanceof MyListingsFragment) {
                Log.i(TAG, "onLongClick: is MyListingsFragment");
                new MaterialAlertDialogBuilder(context)
                        .setTitle("Delete this listing?")
                        .setMessage(listings.get(getAdapterPosition()).getTitle())
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
                                deleteListing(getAdapterPosition());
                            }
                        }).show();
                return true;
            }
            return false;
        }
    }
}
