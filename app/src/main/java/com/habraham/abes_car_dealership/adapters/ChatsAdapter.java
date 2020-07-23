package com.habraham.abes_car_dealership.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.habraham.abes_car_dealership.R;
import com.habraham.abes_car_dealership.activities.MainActivity;
import com.habraham.abes_car_dealership.fragments.ChatFragment;
import com.habraham.abes_car_dealership.models.Chat;
import com.habraham.abes_car_dealership.models.Listing;
import com.habraham.abes_car_dealership.models.Message;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {
    private static final String TAG = "ChatsAdapter";

    Context context;
    List<Chat> chats;

    public ChatsAdapter(Context context, List<Chat> chats) {
        this.context = context;
        this.chats = chats;
        Log.i(TAG, "ChatsAdapter: " + chats);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat = chats.get(position);
        holder.bind(chat);
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public void clear() {
        chats.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Chat> chats) {
        this.chats.addAll(chats);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvSellerName;
        TextView tvListingTitle;
        TextView tvTime;
        TextView tvLastMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSellerName = itemView.findViewById(R.id.tvSellerName);
            tvListingTitle = itemView.findViewById(R.id.tvListingTitle);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
            itemView.setOnClickListener(this);
        }

        public void bind(Chat chat) {
            chat.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject fetchedChat, ParseException e) {
                    ((Chat) fetchedChat).getListing().fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject fetchListing, ParseException e) {
                            tvListingTitle.setText(((Listing) fetchListing).getTitle());
                            ((Listing) fetchListing).getSeller().fetchInBackground(new GetCallback<ParseObject>() {
                                @Override
                                public void done(ParseObject fetchedUser, ParseException e) {
                                    tvSellerName.setText(((ParseUser) fetchedUser).getUsername());
                                }
                            });
                        }
                    });
                    if (((Chat) fetchedChat).getChatLog() != null)
                        ((Chat) fetchedChat).getChatLog().get(0).fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject fetchedMessage, ParseException e) {
                                tvLastMessage.setText(((Message) fetchedMessage).getMessage());
                            }
                        });
                    tvTime.setText(setTime(fetchedChat.getUpdatedAt().toString()));
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

        @Override
        public void onClick(View view) {
            ChatFragment chatFragment = ChatFragment.newInstance(chats.get(getAdapterPosition()));
            ((MainActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.rlContainer, chatFragment).addToBackStack(null).commit();
        }
    }
}
