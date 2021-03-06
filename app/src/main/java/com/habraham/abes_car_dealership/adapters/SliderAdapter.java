package com.habraham.abes_car_dealership.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.habraham.abes_car_dealership.fragments.FilterFragmentDialog;
import com.habraham.abes_car_dealership.fragments.ImageFragmentDialog;
import com.habraham.abes_car_dealership.fragments.ListingsFragment;
import com.habraham.abes_car_dealership.models.SliderItem;
import com.habraham.abes_car_dealership.databinding.SlideItemContainerBinding;

import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {
    private static final String TAG = "SliderAdapter";

    private Context context;
    private List<SliderItem> sliderItems;
    private ViewPager2 viewPager2;
    private SlideItemContainerBinding binding;

    public SliderAdapter(Context context, List<SliderItem> sliderItems, ViewPager2 viewPager2) {
        this.context = context;
        this.sliderItems = sliderItems;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder");
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = SlideItemContainerBinding.inflate(inflater, parent, false);
        View view = binding.getRoot();
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        Log.i(TAG, "onBindViewHolder: " + position);
        holder.setImage(sliderItems.get(position));
    }

    @Override
    public int getItemCount() {
        return sliderItems.size();
    }

    class SliderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imageSlide;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageSlide = binding.imageSlide;
            itemView.setOnClickListener(this);
        }

        void setImage(SliderItem sliderItem) {
            if (sliderItem.getImageUrl() != null) {
                Glide.with(context).load(sliderItem.getImageUrl()).into(imageSlide);
            } else {
                Glide.with(context).load(sliderItem.getBitmap()).into(imageSlide);
            }
        }

        @Override
        public void onClick(View view) {
            SliderItem sliderItem = sliderItems.get(getAdapterPosition());
            Log.i(TAG, "onClick: Slider: " + sliderItem);
            ImageFragmentDialog imageFragmentDialog = ImageFragmentDialog.newInstance(sliderItem.getImageUrl());
            imageFragmentDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "");
        }
    }
}
