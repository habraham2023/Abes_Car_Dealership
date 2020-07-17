package com.habraham.abes_car_dealership.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.habraham.abes_car_dealership.R;

import com.habraham.abes_car_dealership.models.Listing;
import com.habraham.abes_car_dealership.rawValues;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class CreationFragment extends Fragment {
    private static final String TAG = "CreationFragment";

    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    public String photoFileName = "image.jpg";
    protected File photoFile;

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
    MaterialButton btnAddPhoto;

    TextInputLayout priceLayout;
    TextInputEditText priceEditText;
    TextInputLayout contactLayout;
    TextInputEditText contactEditText;

    TextInputLayout extraInformationLayout;
    TextInputEditText extraInformationEditText;
    TextInputLayout addressLayout;
    TextInputEditText addressEditText;

    List<ParseFile> photos;
    TextView tvImageCount;
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
        btnAddPhoto = view.findViewById(R.id.btnAddPhoto);
        priceLayout = view.findViewById(R.id.priceLayout);
        priceEditText = view.findViewById(R.id.priceEditText);
        contactLayout = view.findViewById(R.id.contactLayout);
        contactEditText = view.findViewById(R.id.contactEditText);
        extraInformationLayout = view.findViewById(R.id.extraInformationLayout);
        extraInformationEditText = view.findViewById(R.id.extraInformationEditText);
        addressLayout = view.findViewById(R.id.addressLayout);
        addressEditText = view.findViewById(R.id.addressEditText);

        photos = new ArrayList<>();
        tvImageCount = view.findViewById(R.id.tvImageCount);
        tvImageCount.setText(Integer.toString(photos.size()));

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


        btnAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: " + photos.size());
                launchCamera();
            }
        });

        btnCreateListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                titleInputLayout.setError(null);
                descriptionInputLayout.setError(null);
                makeLayout.setError(null);
                yearLayout.setError(null);
                priceLayout.setError(null);
                contactLayout.setError(null);
                extraInformationLayout.setError(null);
                addressLayout.setError(null);

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
                String price = priceEditText.getText().toString();
                if (price.isEmpty()) {
                    priceLayout.setError("Price of listing cannot be empty.");
                }
                String contact = contactEditText.getText().toString();
                if (contact.isEmpty()) {
                    contactLayout.setError("Contact Information cannot be empty.");
                }
                String extraInformation = extraInformationEditText.getText().toString();

                String address =  addressEditText.getText().toString();
                if (address.isEmpty()) {
                    addressLayout.setError("Address cannot be empty");
                }
                if (photos.isEmpty()) {
                    Toast.makeText(getContext(), "Listing needs at least one photo.", Toast.LENGTH_SHORT);
                    error = true;
                }

                if (!error) createListing(title, description, make, year, price, contact, extraInformation, address);
            }
        });
    }

    public void createListing(String title, String description, String make, String year, String price, String contact, String extraInformation, String address) {
        titleInputLayout.setError(null);
        descriptionInputLayout.setError(null);

        Listing listing = new Listing();
        listing.setTitle(title);
        listing.setDescription(description);
        listing.setSeller(ParseUser.getCurrentUser());
        listing.setMake(make);
        listing.setYear(year);
        listing.setImages(photos);
        listing.setPrice(price);
        listing.setContact(contact);
        listing.setExtraInformation(extraInformation);
        listing.setAddress(address);

        listing.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(CreationFragment.this).commit();
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    public void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider.Abes_Community_Car_Dealership", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // Start the image capture intent to take photo
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    private File getPhotoFileUri(String photoFileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + photoFileName);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                // Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                photos.add(new ParseFile(photoFile));
                tvImageCount.setText(Integer.toString(photos.size()));
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}