package com.habraham.abes_car_dealership.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.habraham.abes_car_dealership.R;
import com.habraham.abes_car_dealership.adapters.ChatsAdapter;
import com.habraham.abes_car_dealership.models.Chat;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment {
    private static final String TAG = "ChatsFragment";

    RecyclerView rvChats;
    ChatsAdapter chatsAdapter;
    List<Chat> chats;
    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvChats = view.findViewById(R.id.rvChats);
        chats = ParseUser.getCurrentUser().getList("chats");

        if (chats == null) chats = new ArrayList<>();

        chatsAdapter = new ChatsAdapter(getContext(), chats);
        rvChats.setLayoutManager(new LinearLayoutManager(getContext()));
        rvChats.setAdapter(chatsAdapter);
    }
}