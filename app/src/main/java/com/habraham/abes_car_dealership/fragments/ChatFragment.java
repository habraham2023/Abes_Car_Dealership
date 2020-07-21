package com.habraham.abes_car_dealership.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.habraham.abes_car_dealership.R;
import com.habraham.abes_car_dealership.models.Chat;

public class ChatFragment extends Fragment {
    private static final String TAG = "ChatFragment";
    private static final String ARG_CHAT = "chat";

    private Chat chat;

    public ChatFragment() {
        // Required empty public constructor
    }

    public static ChatFragment newInstance(Chat chat) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CHAT, chat);
        fragment.setArguments(args);
        return fragment;
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            chat = getArguments().getParcelable(ARG_CHAT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "onCreateView: " + chat);
    }
}