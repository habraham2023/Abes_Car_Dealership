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
import com.habraham.abes_car_dealership.databinding.ChatItemBinding;
import com.habraham.abes_car_dealership.fragments.ChatFragment;
import com.habraham.abes_car_dealership.models.Chat;
import com.parse.ParseException;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {
    private static final String TAG = "ChatsAdapter";

    Context context;
    List<Chat> chats;
    private ChatItemBinding binding;

    public ChatsAdapter(Context context, List<Chat> chats) {
        this.context = context;
        this.chats = chats;
        Log.i(TAG, "ChatsAdapter: " + chats);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = ChatItemBinding.inflate(inflater, parent, false);

        View view = binding.getRoot();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat = chats.get(position);
        try {
            holder.bind(chat);
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
            tvSellerName = binding.tvSellerName;
            tvListingTitle = binding.tvListingTitle;
            tvTime = binding.tvTime;
            tvLastMessage = binding.tvLastMessage;
            itemView.setOnClickListener(this);
        }

        public void bind(Chat chat) throws ParseException {
            tvListingTitle.setText(chat.getListing().getTitle());
            tvSellerName.setText(chat.getListing().getSeller().fetchIfNeeded().getString("screenName"));
            if (chat.getChatLog() != null)
                tvLastMessage.setText(chat.getChatLog().get(0).getMessage());
            tvTime.setText(setTime(chat.getUpdatedAt().toString()));
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
            Log.i(TAG, "onClick: " + chats.get(getAdapterPosition()).getObjectId());
            ChatFragment chatFragment = ChatFragment.newInstance(chats.get(getAdapterPosition()).getObjectId());
            ((MainActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.rlContainer, chatFragment).addToBackStack(null).commit();
        }
    }
}
