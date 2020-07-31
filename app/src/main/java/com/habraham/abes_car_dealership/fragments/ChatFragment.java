package com.habraham.abes_car_dealership.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.habraham.abes_car_dealership.R;
import com.habraham.abes_car_dealership.adapters.MessageAdapter;
import com.habraham.abes_car_dealership.models.Chat;
import com.habraham.abes_car_dealership.models.Message;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class ChatFragment extends Fragment {
    private static final String TAG = "ChatFragment";
    private static final String ARG_CHAT_ID = "chatID";
    final List<Message> messages = new ArrayList<>();
    RecyclerView rvMessages;
    MessageAdapter adapter;
    EditText etMessage;
    ImageButton send;
    Toolbar toolbar;
    private Chat chat;
    private String objectID;

    public ChatFragment() {
        // Required empty public constructor
    }

    public static ChatFragment newInstance(String objectId) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CHAT_ID, objectId);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            objectID = getArguments().getString(ARG_CHAT_ID);
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
        etMessage = view.findViewById(R.id.etMessage);
        send = view.findViewById(R.id.send);

        toolbar = view.findViewById(R.id.toolbar);

        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(ChatFragment.this).commit();
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        setChat();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = etMessage.getText().toString();
                if (data == null || data.isEmpty()) return;
                final Message message = new Message();
                message.setUser(ParseUser.getCurrentUser());
                message.setMessage(data);
                message.setChatID(objectID);
                message.saveInBackground();
                etMessage.setText(null);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            }
        });


        ParseLiveQueryClient parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();

        ParseQuery<Message> parseQuery = ParseQuery.getQuery(Message.class);
        parseQuery.whereEqualTo(Message.KEY_CHAT_ID, objectID);
        SubscriptionHandling<Message> subscriptionHandling = parseLiveQueryClient.subscribe(parseQuery);

        subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, new SubscriptionHandling.HandleEventCallback<Message>() {
            @Override
            public void onEvent(ParseQuery<Message> query, Message object) {
                messages.add(0, object);
                chat.put(Chat.KEY_CHAT_LOG, messages);
                chat.saveInBackground();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        rvMessages.smoothScrollToPosition(0);
                    }
                });
            }
        });
    }

    public void setChat() {
        ParseQuery<Chat> chatQuery = ParseQuery.getQuery(Chat.class);
        Log.i(TAG, "setChat: " + objectID);
        chatQuery.whereEqualTo(Chat.KEY_OBJECT_ID, objectID);
        chatQuery.include(Chat.KEY_CHAT_LOG);
        chatQuery.include(Chat.KEY_INITIATOR);
        chatQuery.include(Chat.KEY_CONTACTED);
        chatQuery.include(Chat.KEY_LISTING);
        chatQuery.getFirstInBackground(new GetCallback<Chat>() {
            @Override
            public void done(Chat chat, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "ERROR: ", e);
                    return;
                }

                ChatFragment.this.chat = chat;
                List<Message> initMessages = null;
                try {
                    initMessages = ((Chat) chat.fetchIfNeeded()).getChatLog();
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
                Log.i(TAG, "done: " + initMessages);

                if (initMessages != null) messages.addAll(initMessages);
                adapter = new MessageAdapter(getContext(), messages);
                final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                linearLayoutManager.setReverseLayout(true);

                rvMessages.setLayoutManager(linearLayoutManager);
                rvMessages.setAdapter(adapter);
                rvMessages.smoothScrollToPosition(0);
            }
        });
    }
}