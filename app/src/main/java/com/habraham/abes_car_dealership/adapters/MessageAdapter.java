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
import com.habraham.abes_car_dealership.R;
import com.habraham.abes_car_dealership.models.Message;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private static final String TAG = "MessageAdapter";

    Context context;
    List<Message> messages;

    public MessageAdapter(Context context, List<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.message_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.bind(message);
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
            ivOther = itemView.findViewById(R.id.ivOther);
            ivMe = itemView.findViewById(R.id.ivMe);
            tvMessage = itemView.findViewById(R.id.tvMessage);
        }

        public void bind(Message message) {
            message.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject fetchMessage, ParseException e) {
                    String message = ((Message) fetchMessage).getMessage();
                    Log.i(TAG, "done: " + message);
                    tvMessage.setText(message);
                    ((Message) fetchMessage).getUser().fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject fetchedUser, ParseException e) {
                            String fetchedId = ((ParseUser) fetchedUser).getObjectId();
                            String userId = ParseUser.getCurrentUser().getObjectId();
                            if (fetchedId.equals(userId)) {
                                ivMe.setVisibility(View.VISIBLE);
                                Glide.with(context).load(((ParseUser) fetchedUser).getParseFile("profilePicture").getUrl()).into(ivMe);
                            } else {
                                ivOther.setVisibility(View.VISIBLE);
                                Glide.with(context).load(((ParseUser) fetchedUser).getParseFile("profilePicture").getUrl()).into(ivOther);
                            }
                        }
                    });
                }
            });

        }
    }
}
