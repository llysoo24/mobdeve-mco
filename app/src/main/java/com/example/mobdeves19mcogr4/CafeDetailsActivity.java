package com.example.mobdeves19mcogr4;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Arrays;
import java.util.List;

public class CafeDetailsActivity extends AppCompatActivity {

    private static final String TAG = "CafeDetailsActivity";

    private TextView cafeNameTextView, cafeLocationTextView, cafeDescriptionTextView, cafeRatingTextView;
    private ImageView cafeImageView;
    private TextView wifiTextView, foodTextView, ambianceTextView;
    private String placeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cafe_details);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyAXKEk9m2cpmyT0WzDwwCGvndwxHWZOrYk");
        }
        PlacesClient placesClient = Places.createClient(this);

        Intent intent = getIntent();
        placeId = intent.getStringExtra("placeId");


        if (placeId == null || placeId.isEmpty()) {
            Log.e(TAG, "Place ID is missing. Returning to previous page.");
            Toast.makeText(this, "Invalid cafe details. Returning to the previous page.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String cafeImageUrl = intent.getStringExtra("cafeImageUrl");

        cafeNameTextView = findViewById(R.id.cafeName);
        cafeLocationTextView = findViewById(R.id.cafeLocation);
        cafeDescriptionTextView = findViewById(R.id.cafeDescription);
        cafeRatingTextView = findViewById(R.id.cafeRating);
        cafeImageView = findViewById(R.id.cafeImage);

        wifiTextView = findViewById(R.id.wifiTextView);
        foodTextView = findViewById(R.id.foodTextView);
        ambianceTextView = findViewById(R.id.ambianceTextView);

        if (cafeImageUrl != null && !cafeImageUrl.isEmpty()) {
            Glide.with(this)
                    .load(cafeImageUrl)
                    .placeholder(R.drawable.logo_coffee)
                    .into(cafeImageView);
        } else {
            fetchCafeDetails(placesClient, placeId);
        }
        fetchCafeDetails(placesClient, placeId);
    }

    private void fetchCafeDetails(PlacesClient placesClient, String placeId) {
        List<Place.Field> placeFields = Arrays.asList(
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.PHOTO_METADATAS,
                Place.Field.RATING,
                Place.Field.TYPES
        );
        FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        placesClient.fetchPlace(request).addOnCompleteListener(new OnCompleteListener<FetchPlaceResponse>() {
            @Override
            public void onComplete(@NonNull Task<FetchPlaceResponse> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    Place place = task.getResult().getPlace();

                    cafeNameTextView.setText(place.getName() != null ? place.getName() : "Unknown Name");
                    cafeLocationTextView.setText(place.getAddress() != null ? place.getAddress() : "Address not available");
                    cafeRatingTextView.setText("Rating: " + (place.getRating() != null ? place.getRating() : "N/A"));

                    if (place.getPhotoMetadatas() != null && !place.getPhotoMetadatas().isEmpty()) {
                        PhotoMetadata photoMetadata = place.getPhotoMetadatas().get(0);

                        FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata).build();
                        placesClient.fetchPhoto(photoRequest).addOnCompleteListener(new OnCompleteListener<com.google.android.libraries.places.api.net.FetchPhotoResponse>() {
                            @Override
                            public void onComplete(@NonNull Task<com.google.android.libraries.places.api.net.FetchPhotoResponse> photoTask) {
                                if (photoTask.isSuccessful() && photoTask.getResult() != null) {
                                    cafeImageView.setImageBitmap(photoTask.getResult().getBitmap());
                                } else {
                                    Log.e(TAG, "Failed to fetch photo: " + photoTask.getException());
                                    cafeImageView.setImageResource(R.drawable.logo_coffee);
                                }
                            }
                        });
                    } else {
                        cafeImageView.setImageResource(R.drawable.logo_coffee);
                    }

                    if (place.getTypes() != null) {
                        wifiTextView.setText(place.getTypes().contains(Place.Type.CAFE) ? "Wi-Fi: Available" : "Wi-Fi: Not available");
                        foodTextView.setText(place.getTypes().contains(Place.Type.CAFE) ? "Food: Available" : "Food: Not Available");
                        ambianceTextView.setText("Details: Suitable for casual visits");
                    } else {
                        wifiTextView.setText("Wi-Fi: Unknown");
                        foodTextView.setText("Food: Unknown");
                        ambianceTextView.setText("Details: Not available");
                    }

                } else {
                    Log.e(TAG, "Failed to fetch place details: " + task.getException());
                    cafeDescriptionTextView.setText("Failed to load cafe details.");
                    Toast.makeText(CafeDetailsActivity.this, "Failed to load cafe details.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
