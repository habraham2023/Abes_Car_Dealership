package com.habraham.abes_car_dealership.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
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
import android.widget.ProgressBar;
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
import com.habraham.abes_car_dealership.SliderItem;
import com.habraham.abes_car_dealership.adapters.SliderAdapter;
import com.habraham.abes_car_dealership.models.Listing;
import com.habraham.abes_car_dealership.models.Make;
import com.habraham.abes_car_dealership.models.Model;
import com.habraham.abes_car_dealership.rawValues;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;


public class CreationFragment extends Fragment {
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    public final static int PICK_PHOTO_CODE = 1046;
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
    MaterialButton btnTakePhoto;
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

    ProgressBar progressBar;

    OkHttpClient client = new OkHttpClient();
    private String url = "https://maps.googleapis.com/maps/api/geocode/json";

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
        btnTakePhoto = view.findViewById(R.id.btnTakePhoto);
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
        progressBar = view.findViewById(R.id.progressBar);

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

        setMakes();

        btnAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: " + photos.size());
                onPickPhoto();
            }
        });

        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
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
                progressBar.setVisibility(View.VISIBLE);

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
                String model = modelDropdown.getText().toString();
                if (model.isEmpty()) {
                    modelLayout.setError("Model of listing cannot be empty.");
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
                    createListing(title, description, make, model, year, price, contact, extraInformation, address);
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
                page.setScaleY(0.85f + r * 0.15f);
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

    public void createListing(String title, String description, String make, String model, String year, String price, String contact, String extraInformation, String address) {
        titleInputLayout.setError(null);
        descriptionInputLayout.setError(null);

        final Listing listing = new Listing();
        listing.setTitle(title);
        listing.setDescription(description);
        listing.setSeller(ParseUser.getCurrentUser());
        listing.setMake(make);
        listing.setModel(model);
        listing.setYear(year);
        listing.setImages(photos);
        listing.setPrice(price);
        listing.setContact(contact);
        listing.setExtraInformation(extraInformation);

        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addQueryParameter("key", getString(R.string.googleKey));
        urlBuilder.addQueryParameter("address", address);
        String fullURL = urlBuilder.build().toString();
        Log.i(TAG, "createListing: " + fullURL);
        Request request = new Request.Builder()
                .url(fullURL)
                .build();


        // Make API call for formatted address and GeoPoint
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: ", e);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseData = response.body().string();

                    JSONObject jsonObject = new JSONObject(responseData);
                    JSONObject result = jsonObject.getJSONArray("results").getJSONObject(0);
                    String formattedAddress = result.getString("formatted_address");
                    Log.i(TAG, "onResponse: " + formattedAddress);
                    listing.setAddress(formattedAddress);
                    JSONObject location = result.getJSONObject("geometry").getJSONObject("location");
                    double lat = location.getDouble("lat");
                    double lng = location.getDouble("lng");

                    ParseGeoPoint geoPoint = new ParseGeoPoint(lat, lng);
                    listing.setLatLng(geoPoint);

                    Log.i(TAG, "onResponse: " + lat + " " + lng);
                    listing.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            getActivity().getSupportFragmentManager().beginTransaction().remove(CreationFragment.this).commit();
                            getActivity().getSupportFragmentManager().popBackStack();
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                }
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
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            // by this point we have the camera photo on disk
            Bitmap takenImage = rotateBitmapOrientation(photoFile.getAbsolutePath());
            // RESIZE BITMAP, see section below
            // Load the taken image into a preview
            sliderItems.add(new SliderItem(takenImage));
            sliderAdapter.notifyItemInserted(sliderItems.size());
            dotsIndicator.setViewPager2(viewPager2);
            viewPager2.setCurrentItem(sliderItems.size() - 1, true);
            photos.add(new ParseFile(photoFile));
        } else if (requestCode == PICK_PHOTO_CODE && resultCode == RESULT_OK) {
            Uri photoUri = data.getData();
            Bitmap selectedImage = loadFromUri(photoUri);


            try {
                File f = new File(getContext().getCacheDir(), photoFileName + photos.size());
                f.createNewFile();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);

                FileOutputStream fos = new FileOutputStream(f);
                fos.write(bos.toByteArray());
                fos.flush();
                fos.close();
                photos.add(new ParseFile(f));

                sliderItems.add(new SliderItem(rotateBitmapOrientation(f.getAbsolutePath())));
                sliderAdapter.notifyItemInserted(sliderItems.size());
                dotsIndicator.setViewPager2(viewPager2);
                viewPager2.setCurrentItem(sliderItems.size() - 1, true);
            } catch (IOException e) {
                e.printStackTrace();
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

    // Trigger gallery selection for a photo
    public void onPickPhoto() {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        startActivityForResult(intent, PICK_PHOTO_CODE);
    }

    public Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            // check version of Android on device
            if (Build.VERSION.SDK_INT > 27) {
                // on newer versions of Android, use the new decodeBitmap method
                ImageDecoder.Source source = ImageDecoder.createSource(getActivity().getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                // support older versions of Android by using getBitmap
                image = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
}