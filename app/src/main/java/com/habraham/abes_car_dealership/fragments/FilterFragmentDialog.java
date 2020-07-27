package com.habraham.abes_car_dealership.fragments;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.habraham.abes_car_dealership.R;
import com.habraham.abes_car_dealership.models.Make;
import com.habraham.abes_car_dealership.models.Model;
import com.habraham.abes_car_dealership.rawValues;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class FilterFragmentDialog extends DialogFragment {
    private static final String TAG = "FilterFragmentDialog";

    TextInputLayout makeLayout;
    AutoCompleteTextView makeDropdown;
    TextInputLayout modelLayout;
    AutoCompleteTextView modelDropdown;
    TextInputLayout yearLayout;
    AutoCompleteTextView yearDropdown;

    List<Make> makes;

    MaterialButton btnCancel;
    MaterialButton btnApply;

    public FilterFragmentDialog() {}

    public interface FilterDialogListener {
        void onFinishFilterDialog(Intent i);
    }

    public static FilterFragmentDialog newInstance() {
        FilterFragmentDialog fragment = new FilterFragmentDialog();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public void onResume() {
        // Store access variables for window and blank point
        Window window = getDialog().getWindow();
        Point size = new Point();
        // Store dimensions of the screen in `size`
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        // Set the width of the dialog proportional to 75% of the screen width
        window.setLayout((int) (size.x), (int) (size.y * 0.5));
        window.setGravity(Gravity.CENTER);
        // Call super onResume after sizing
        super.onResume();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        makeLayout = view.findViewById(R.id.makeLayout);
        makeDropdown = view.findViewById(R.id.makeDropdown);
        modelLayout = view.findViewById(R.id.modelLayout);
        modelDropdown = view.findViewById(R.id.modelDropdown);
        yearLayout = view.findViewById(R.id.yearLayout);
        yearDropdown = view.findViewById(R.id.yearDropdown);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnApply = view.findViewById(R.id.btnApply);

        setMakes();

        makeDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                modelDropdown.setText(null);
                String selection = (String) adapterView.getItemAtPosition(i);
                Log.i(TAG, "onItemClick: " + selection);
                setModels(makes.get(i));
            }
        });

        yearDropdown.setAdapter(new ArrayAdapter<>(getContext(), R.layout.dropdown_menu_popup_item, rawValues.years));

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String make = makeDropdown.getText().toString();
                String model = modelDropdown.getText().toString();
                String year = yearDropdown.getText().toString();

                FilterDialogListener listener = (FilterDialogListener) getTargetFragment();
                Intent i = new Intent();
                i.putExtra("make", make);
                i.putExtra("model", model);
                i.putExtra("year", year);

                listener.onFinishFilterDialog(i);
                dismiss();
            }
        });
    }

    private void setModels(Make make) {
        List<String> modelNames = new ArrayList<>();

        for (Model model : make.getModels()) {
            modelNames.add(model.getName());
        }

        modelDropdown.setAdapter(new ArrayAdapter<>(getContext(), R.layout.dropdown_menu_popup_item, modelNames));
    }

    private void setMakes() {
        ParseQuery<Make> queryMakes = ParseQuery.getQuery(Make.class);
        queryMakes.orderByAscending(Make.KEY_NAME);
        queryMakes.include(Make.KEY_MODELS);
        queryMakes.findInBackground(new FindCallback<Make>() {
            @Override
            public void done(List<Make> makes, ParseException e) {
                FilterFragmentDialog.this.makes = makes;
                List<String> makeNames = new ArrayList<>();
                for (Make make : makes) {
                    makeNames.add(make.getName());
                }
                try {
                makeDropdown.setAdapter(new ArrayAdapter<>(getContext(), R.layout.dropdown_menu_popup_item, makeNames));
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }
        });
    }
}