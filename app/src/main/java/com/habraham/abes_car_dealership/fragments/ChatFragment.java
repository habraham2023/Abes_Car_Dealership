package com.habraham.abes_car_dealership.fragments;

import android.graphics.Canvas;
import android.os.Bundle;
import android.text.format.DateUtils;
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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.habraham.abes_car_dealership.R;
import com.habraham.abes_car_dealership.adapters.MessageAdapter;
import com.habraham.abes_car_dealership.databinding.FragmentChatBinding;
import com.habraham.abes_car_dealership.models.Chat;
import com.habraham.abes_car_dealership.models.Message;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

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
    private FragmentChatBinding binding;

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
    public void onDestroy() {
        super.onDestroy();
        if (chat != null && (chat.getChatLog() == null || chat.getChatLog().size() == 0)) chat.deleteInBackground();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(getLayoutInflater(), container, false);
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

        rvMessages = binding.rvMessages;
        etMessage = binding.etMessage;
        send = binding.send;

        toolbar = binding.toolbar;

        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(ChatFragment.this).commit();
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        final ParseQuery<Message> parseQuery = ParseQuery.getQuery(Message.class);

        ItemTouchHelper.SimpleCallback rvCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {}

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                String createdAt = messages.get(viewHolder.getAdapterPosition()).getCreatedAt().toString();
                String relativeDate = setTime(createdAt);
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeRightLabel(relativeDate)
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX / 4, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(rvCallback);
        itemTouchHelper.attachToRecyclerView(rvMessages);

        setChat();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ParseUser user = ParseUser.getCurrentUser();
                final ArrayList<Chat> chats = (ArrayList<Chat>) user.get("chats");

                if (chats == null) {
                    // Create a new chats ArrayList and add a Chat object to it
                    List<Chat> newChats = new ArrayList<>();
                    newChats.add(chat);
                    user.put("chats", newChats);
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            sendMessage();
                        }
                    });
                } else {
                    sendMessage();
                }
            }
        });

        ParseLiveQueryClient parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();

        Log.i(TAG, "setLiveQuery: " + objectID);
        parseQuery.whereEqualTo(Message.KEY_CHAT_ID, objectID);

        SubscriptionHandling<Message> subscriptionHandling = parseLiveQueryClient.subscribe(parseQuery);
        subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, new SubscriptionHandling.HandleEventCallback<Message>() {
            @Override
            public void onEvent(ParseQuery<Message> query, final Message object) {
                messages.add(0, object);
                chat.put(Chat.KEY_CHAT_LOG, messages);
                chat.saveInBackground();

                if (getActivity() == null) {
                    Log.i(TAG, "onEvent: Stop");
                    return;
                }
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

    private String setTime(String createdAt) {
        String format = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(format, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(createdAt).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    private void sendMessage() {
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
                List<Message> initMessages = chat.getChatLog();

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