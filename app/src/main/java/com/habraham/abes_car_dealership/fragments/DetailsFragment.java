package com.habraham.abes_car_dealership.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.habraham.abes_car_dealership.R;
import com.habraham.abes_car_dealership.adapters.SliderAdapter;
import com.habraham.abes_car_dealership.SliderItem;
import com.habraham.abes_car_dealership.models.Chat;
import com.habraham.abes_car_dealership.models.Favorite;
import com.habraham.abes_car_dealership.models.Listing;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;
import java.util.List;

public class DetailsFragment extends Fragment {
    private static final String TAG = "DetailsFragment";

    private static final String LISTING = "listing";
    private Listing listing;

    Toolbar toolbar;
    TextView tvTitle;
    TextView tvDescription;
    TextView tvPrice;
    TextView tvSellerName;
    TextView tvLocation;
    TextView tvContact;
    TextView tvExtraInformation;
    FloatingActionButton fabAddChat;

    ImageView ivFavorite;
    ViewPager2 viewPager2;
    DotsIndicator dotsIndicator;
    List<SliderItem> sliderItems;

    public DetailsFragment() {
        // Required empty public constructor
    }

    public static DetailsFragment newInstance(Listing listing) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(LISTING, listing);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            listing = getArguments().getParcelable(LISTING);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "onViewCreated: " + listing.getDescription());

        toolbar = view.findViewById(R.id.toolbar);
        tvTitle = view.findViewById(R.id.tvTitle);
        tvDescription = view.findViewById(R.id.tvDescription);
        tvPrice = view.findViewById(R.id.tvPrice);
        tvSellerName = view.findViewById(R.id.tvSellerName);
        tvLocation = view.findViewById(R.id.tvLocation);
        tvContact = view.findViewById(R.id.tvContact);
        tvExtraInformation = view.findViewById(R.id.tvExtraInformation);
        ivFavorite = view.findViewById(R.id.ivFavorite);
        fabAddChat = view.findViewById(R.id.fabAddChat);
        viewPager2 = view.findViewById(R.id.slider);
        dotsIndicator = view.findViewById(R.id.dots_indicator);

        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(DetailsFragment.this).commit();
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        tvTitle.setText(listing.getTitle());
        tvDescription.setText(listing.getDescription());
        tvPrice.append(" $" + listing.getPrice());
        tvLocation.append(" " + listing.getAddress());
        tvContact.append(" " + listing.getContact());
        try {
            ParseUser user = listing.fetchIfNeeded().getParseUser(Listing.KEY_SELLER);
            tvSellerName.append(" " + user.fetchIfNeeded().getString("screenName"));
            if (user.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) fabAddChat.setVisibility(View.GONE);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        tvExtraInformation.setText(listing.getExtraInformation());


        if (Listing.listingsFavorited.contains(listing.getObjectId())) {
            Glide.with(getContext()).load(R.drawable.favorite_fill).into(ivFavorite);
            ivFavorite.setColorFilter(getContext().getColor(R.color.secondaryDarkColor));
        } else {
            Glide.with(getContext()).load(R.drawable.favorite).into(ivFavorite);
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
                                        Glide.with(getContext()).load(R.drawable.favorite).into(ivFavorite);
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
                        Glide.with(getContext()).load(R.drawable.favorite_fill).into(ivFavorite);
                        ivFavorite.setColorFilter(getContext().getColor(R.color.secondaryDarkColor));
                        Listing.listingsFavorited.add(listing.getObjectId());
                        Log.i(TAG, "onClick: " + Listing.listingsFavorited);
                    }
                });
            }
        });

        fabAddChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ParseUser user = ParseUser.getCurrentUser();
                final List<Chat> chats = (ArrayList<Chat>) user.get("chats");
                if (chats != null) {
                    Log.i(TAG, "onClick: " + chats);
                    // Try and find an existing matching chat
                    for (Chat chat : chats) {
                        try {
                            if (chat.isEqual(user, listing.getSeller(), listing)) {
                                Log.i(TAG, "onClick: Chat exists");
                                ChatFragment chatFragment = ChatFragment.newInstance(chat);
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.rlContainer, chatFragment).addToBackStack(null).commit();
                                return;
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                            return;
                        }
                    }

                    // If no existing chat exists add a new one to the existing chats ArrayList
                    final Chat chat = new Chat();
                    chat.setInitiator(user);
                    chat.setContacted(listing.getSeller());
                    chat.setListing(listing);
                    chat.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            chats.add(chat);
                            user.put("chats", chats);
                            user.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    ChatFragment chatFragment = ChatFragment.newInstance(chat);
                                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.rlContainer, chatFragment).addToBackStack(null).commit();
                                }
                            });
                        }
                    });
                } else {
                    // Create a new chats ArrayList and add a Chat object to it
                    final Chat chat = new Chat();
                    chat.setInitiator(user);
                    chat.setContacted(listing.getSeller());
                    chat.setListing(listing);
                    chat.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            List<Chat> newChats = new ArrayList<>();
                            newChats.add(chat);
                            user.put("chats", newChats);
                            user.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    ChatFragment chatFragment = ChatFragment.newInstance(chat);
                                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.rlContainer, chatFragment).addToBackStack(null).commit();
                                }
                            });
                        }
                    });
                }
            }
        });

        setSliderItems();
        SliderAdapter sliderAdapter = new SliderAdapter(getContext(), sliderItems, viewPager2);
        viewPager2.setAdapter(sliderAdapter);
        dotsIndicator.setViewPager2(viewPager2);

        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);
            }
        });
        viewPager2.setPageTransformer(compositePageTransformer);
    }

    private void setSliderItems() {
        sliderItems = new ArrayList<>();
        List<ParseFile> images = listing.getImages();

        for (ParseFile image : images) {
            Log.i(TAG, "setSliderItems: " + image.getUrl());
            sliderItems.add(new SliderItem(image.getUrl()));
        }
//        Log.i(TAG, "setSliderItems: " + sliderItems);
    }
}