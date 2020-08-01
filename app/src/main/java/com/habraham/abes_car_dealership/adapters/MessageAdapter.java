package com.habraham.abes_car_dealership.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.habraham.abes_car_dealership.databinding.MessageItemBinding;
import com.habraham.abes_car_dealership.models.Message;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private static final String TAG = "MessageAdapter";

    Context context;
    List<Message> messages;
    private MessageItemBinding binding;

    public MessageAdapter(Context context, List<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = MessageItemBinding.inflate(inflater, parent, false);
        View view = binding.getRoot();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.i(TAG, "onBindViewHolder: " + position);
        Message message = messages.get(position);
        try {
            holder.bind(message);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivOther;
        ImageView ivMe;
        TextView tvMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivOther = binding.ivOther;
            ivMe = binding.ivMe;
            tvMessage = binding.tvMessage;
        }

        public void bind(Message message) throws ParseException {
            Log.i(TAG, "bind: " + message.getMessage());

            tvMessage.setText(message.getMessage());

            String fetchedId = message.getUser().getObjectId();

            Log.i(TAG, "bind: " + fetchedId);
            if (fetchedId.equals(ParseUser.getCurrentUser().getObjectId())) {
                ivMe.setVisibility(View.VISIBLE);
                ivOther.setVisibility(View.GONE);
                Log.i(TAG, "bind: My Message");
                Glide.with(context).load(message.getUser().fetchIfNeeded().getParseFile("profilePicture").getUrl()).transform(new CircleCrop()).into(ivMe);
            } else {
                ivOther.setVisibility(View.VISIBLE);
                ivMe.setVisibility(View.GONE);
                Log.i(TAG, "bind: Other Message");
                Glide.with(context).load(message.getUser().fetchIfNeeded().getParseFile("profilePicture").getUrl()).transform(new CircleCrop()).into(ivOther);
            }
        }
    }
}
