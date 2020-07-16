package com.habraham.abes_car_dealership.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.habraham.abes_car_dealership.R;
import com.habraham.abes_car_dealership.SliderAdapter;
import com.habraham.abes_car_dealership.SliderItem;
import com.habraham.abes_car_dealership.models.Listing;
import com.habraham.abes_car_dealership.rawValues;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;


public class CreationFragment extends Fragment {
    private static final String TAG = "CreationFragment";

    Toolbar toolbar;
    TextInputLayout titleInputLayout;
    TextInputEditText titleEditText;
    TextInputLayout descriptionInputLayout;
    TextInputEditText descriptionEditText;
    MaterialButton btnCreateListing;

    TextInputLayout makeLayout;
    AutoCompleteTextView makeDropdown;
    TextInputLayout yearLayout;
    AutoCompleteTextView yearDropdown;

    SliderView sliderView;
    SliderAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_creation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = view.findViewById(R.id.toolbar);
        titleInputLayout = view.findViewById(R.id.titleTextInput);
        titleEditText = view.findViewById(R.id.titleEditText);
        descriptionInputLayout = view.findViewById(R.id.descriptionTextInput);
        descriptionEditText = view.findViewById(R.id.descriptionEditText);
        btnCreateListing = view.findViewById(R.id.btnCreateListing);
        makeLayout = view.findViewById(R.id.makeLayout);
        makeDropdown = view.findViewById(R.id.makeDropdown);
        yearLayout = view.findViewById(R.id.yearLayout);
        yearDropdown = view.findViewById(R.id.yearDropdown);

        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(CreationFragment.this).commit();
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        makeDropdown.setAdapter(new ArrayAdapter<>(getContext(), R.layout.dropdown_menu_popup_item, rawValues.makes));
        yearDropdown.setAdapter(new ArrayAdapter<>(getContext(), R.layout.dropdown_menu_popup_item, rawValues.years));

        sliderView = view.findViewById(R.id.photoSlider);
        adapter = new SliderAdapter(getContext());
        sliderView.setSliderAdapter(adapter);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);

        btnCreateListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean error = false;

                String title = titleEditText.getText().toString();
                if (title.isEmpty()) {
                    titleInputLayout.setError("Title of listing cannot be empty.");
                    error = true;
                }
                String description = descriptionEditText.getText().toString();
                if (description.isEmpty()) {
                    descriptionInputLayout.setError("Description of listing cannot be empty");
                    error = true;
                }
                String make = makeDropdown.getText().toString();
                if (make.isEmpty()) {
                    makeLayout.setError("Make of listing cannot be empty.");
                    error = true;
                }

                String year = yearDropdown.getText().toString();
                if (year.isEmpty()) {
                    yearLayout.setError("Year of listing cannot be empty.");
                    error = true;
                }

                if (!error) createListing(title, description, make, year);
            }
        });
    }

    public void createListing(String title, String description, String make, String year) {
        titleInputLayout.setError(null);
        descriptionInputLayout.setError(null);

        Listing listing = new Listing();
        listing.setTitle(title);
        listing.setDescription(description);
        listing.setSeller(ParseUser.getCurrentUser());
        listing.setMake(make);
        listing.setYear(year);

        listing.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(CreationFragment.this).commit();
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }
}