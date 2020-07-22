package com.habraham.abes_car_dealership.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.habraham.abes_car_dealership.R;
import com.habraham.abes_car_dealership.adapters.MessageAdapter;
import com.habraham.abes_car_dealership.models.Chat;
import com.habraham.abes_car_dealership.models.Message;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {
    private static final String TAG = "ChatFragment";
    private static final String ARG_CHAT = "chat";

    private Chat chat;

    RecyclerView rvMessages;
    MessageAdapter adapter;

    Toolbar toolbar;

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

        rvMessages = view.findViewById(R.id.rvMessages);
        toolbar = view.findViewById(R.id.toolbar);

        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(ChatFragment.this).commit();
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        chat.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject fetchedChat, ParseException e) {
                List<Message> messages = ((Chat)fetchedChat).getChatLog();
                Log.i(TAG, "done: " + messages);

                if (messages == null) messages = new ArrayList<>();

                adapter = new MessageAdapter(getContext(), messages);
                rvMessages.setLayoutManager(new LinearLayoutManager(getContext()));
                rvMessages.setAdapter(adapter);
            }
        });
    }
}