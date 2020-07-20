package com.habraham.abes_car_dealership.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.habraham.abes_car_dealership.R;
import com.habraham.abes_car_dealership.SliderAdapter;
import com.habraham.abes_car_dealership.SliderItem;
import com.habraham.abes_car_dealership.models.Listing;
import com.habraham.abes_car_dealership.models.Make;
import com.habraham.abes_car_dealership.models.Model;
import com.habraham.abes_car_dealership.rawValues;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class CreationFragment extends Fragment {
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    private static final String TAG = "CreationFragment";
    public String photoFileName = "image";
        protected File photoFile;

    Toolbar toolbar;
    TextInputLayout titleInputLayout;
    TextInputEditText titleEditText;
    TextInputLayout descriptionInputLayout;
    TextInputEditText descriptionEditText;
    MaterialButton btnCreateListing;

    TextInputLayout makeLayout;
    AutoCompleteTextView makeDropdown;
    TextInputLayout modelLayout;
    AutoCompleteTextView modelDropdown;
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
    List<Make> makes;

    ViewPager2 viewPager2;
    DotsIndicator dotsIndicator;
    SliderAdapter sliderAdapter;
    List<SliderItem> sliderItems;


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
        modelLayout = view.findViewById(R.id.modelLayout);
        modelDropdown = view.findViewById(R.id.modelDropdown);
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
        viewPager2 = view.findViewById(R.id.slider);
        dotsIndicator = view.findViewById(R.id.dots_indicator);

        photos = new ArrayList<>();
        sliderItems = new ArrayList<>();

        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(CreationFragment.this).commit();
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

//        makeDropdown.setAdapter(new ArrayAdapter<>(getContext(), R.layout.dropdown_menu_popup_item, rawValues.makes));
//        yearDropdown.setAdapter(new ArrayAdapter<>(getContext(), R.layout.dropdown_menu_popup_item, rawValues.years));
        setMakes();


        btnAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: " + photos.size());
                launchCamera();
            }
        });

        makeDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                modelDropdown.setText(null);
                String selection = (String) adapterView.getItemAtPosition(i);
                Log.i(TAG, "onItemClick: " + selection);
                setModels(makes.get(i));
            }
        });

        modelDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                yearDropdown.setAdapter(new ArrayAdapter<>(getContext(), R.layout.dropdown_menu_popup_item, rawValues.years));
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

                String address = addressEditText.getText().toString();
                if (address.isEmpty()) {
                    addressLayout.setError("Address cannot be empty");
                }
                if (photos.isEmpty()) {
                    Toast.makeText(getContext(), "Listing needs at least one photo.", Toast.LENGTH_SHORT);
                    error = true;
                }

                if (!error)
                    createListing(title, description, make, year, price, contact, extraInformation, address);
            }
        });

        sliderAdapter = new SliderAdapter(getContext(), sliderItems, viewPager2);
        viewPager2.setAdapter(sliderAdapter);
        dotsIndicator.setViewPager2(viewPager2);

        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r  * 0.15f);
            }
        });
        viewPager2.setPageTransformer(compositePageTransformer);
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
                CreationFragment.this.makes = makes;
                List<String> makeNames = new ArrayList<>();
                for (Make make : makes) {
                    makeNames.add(make.getName());
                }
                makeDropdown.setAdapter(new ArrayAdapter<>(getContext(), R.layout.dropdown_menu_popup_item, makeNames));
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
        photoFile = getPhotoFileUri(photoFileName + photos.size());

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
                Bitmap takenImage = rotateBitmapOrientation(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                sliderItems.add(new SliderItem(takenImage));
                sliderAdapter.notifyItemInserted(sliderItems.size());
                dotsIndicator.setViewPager2(viewPager2);
                viewPager2.setCurrentItem(sliderItems.size()-1, true);
                photos.add(new ParseFile(photoFile));

            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Rotates image so that the picture taken is displayed correctly
    public Bitmap rotateBitmapOrientation(String photoFilePath) {
        // Create and configure BitmapFactory
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoFilePath, bounds);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(photoFilePath, opts);
        // Read EXIF Data
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(photoFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
        // Rotate Bitmap
        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
        // Return result
        return rotatedBitmap;
    }
}