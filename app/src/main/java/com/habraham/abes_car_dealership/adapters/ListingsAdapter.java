package com.habraham.abes_car_dealership.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.habraham.abes_car_dealership.R;
import com.habraham.abes_car_dealership.models.Favorite;
import com.habraham.abes_car_dealership.models.Listing;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class ListingsAdapter extends RecyclerView.Adapter<ListingsAdapter.ViewHolder> {
    private static final String TAG = "ListingsAdapter";

    private Context context;
    private List<Listing> listings;

    public ListingsAdapter(Context context, List<Listing> listings) {
        this.context = context;
        this.listings = listings;
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

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvDescription;
        private ImageView ivFavorite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivFavorite = itemView.findViewById(R.id.ivFavorite);
        }

        public void bind(final Listing listing) {
            tvTitle.setText(listing.getTitle());
            tvDescription.setText(listing.getDescription());

            if (Listing.listingsFavorited.contains(listing.getObjectId())) {
                Glide.with(context).load(R.drawable.favorite_fill).into(ivFavorite);
                ivFavorite.setColorFilter(context.getColor(R.color.secondaryDarkColor));
            } else {
                Glide.with(context).load(R.drawable.favorite).into(ivFavorite);
                ivFavorite.setColorFilter(Color.BLACK);
            }


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
        }
    }
}
