package com.habraham.abes_car_dealership.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.habraham.abes_car_dealership.R;
import com.habraham.abes_car_dealership.adapters.ChatsAdapter;
import com.habraham.abes_car_dealership.databinding.FragmentChatsBinding;
import com.habraham.abes_car_dealership.models.Chat;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment {
    private static final String TAG = "ChatsFragment";

    RecyclerView rvChats;
    List<Chat> chats;
    ChatsAdapter chatsAdapter;
    ProgressBar progressBar;
    private FragmentChatsBinding binding;
    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChatsBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvChats = binding.rvChats;
        progressBar = binding.progressBar;
        chats = new ArrayList<>();
        chatsAdapter = new ChatsAdapter(getContext(), chats);

        getChats();
    }

    private void getChats() {
        progressBar.setVisibility(View.VISIBLE);
        chatsAdapter.clear();
        ParseQuery<Chat> contactedQuery = ParseQuery.getQuery(Chat.class);
        contactedQuery.whereEqualTo(Chat.KEY_CONTACTED, ParseUser.getCurrentUser());

        ParseQuery<Chat> initiateQuery = ParseQuery.getQuery(Chat.class);
        initiateQuery.whereEqualTo(Chat.KEY_INITIATOR, ParseUser.getCurrentUser());

        List<ParseQuery<Chat>> queries = new ArrayList<ParseQuery<Chat>>();
        queries.add(contactedQuery);
        queries.add(initiateQuery);

        ParseQuery<Chat> mainQuery = ParseQuery.or(queries);
        mainQuery.include(Chat.KEY_CHAT_LOG);
        mainQuery.include(Chat.KEY_LISTING);
        mainQuery.include(Chat.KEY_INITIATOR);
        mainQuery.include(Chat.KEY_CONTACTED);
        mainQuery.orderByDescending(Chat.KEY_UPDATED_AT);
        mainQuery.findInBackground(new FindCallback<Chat>() {
            @Override
            public void done(List<Chat> mChats, ParseException e) {
                if (e == null) {
                chatsAdapter.addAll(mChats);
                rvChats.setLayoutManager(new LinearLayoutManager(getContext()));
                rvChats.setAdapter(chatsAdapter);
                }

                progressBar.setVisibility(View.GONE);
            }
        });
    }
}