package com.example.mobdeves19mcogr4;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeActivity extends FragmentActivity {

    private RecyclerView cafeRecyclerView;
    private CafeAdapter cafeAdapter;
    private List<Cafe> filteredCafes;
    private List<Cafe> allCafes;
    private FusedLocationProviderClient fusedLocationClient;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private double currentLatitude;
    private double currentLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        cafeRecyclerView = findViewById(R.id.nearbyCafesRecycler);
        cafeRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        filteredCafes = new ArrayList<>();
        allCafes = new ArrayList<>();
        cafeAdapter = new CafeAdapter(this, filteredCafes);
        cafeRecyclerView.setAdapter(cafeAdapter);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getCurrentLocation();

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterCafes(newText);
                return true;
            }
        });

        ImageButton filterButton = findViewById(R.id.filterButton);
        filterButton.setOnClickListener(v -> showFilterPopup(v));

        Button viewProfileButton = findViewById(R.id.viewProfileBtn);
        viewProfileButton.setOnClickListener(v -> {
            Intent intent = getIntent();
            String userName = intent.getStringExtra("USER_NAME");
            String userEmail = intent.getStringExtra("USER_EMAIL");

            Intent profileIntent = new Intent(HomeActivity.this, ViewProfileActivity.class);
            profileIntent.putExtra("USER_NAME", userName);
            profileIntent.putExtra("USER_EMAIL", userEmail);
            startActivity(profileIntent);
        });

        Button mapButton = findViewById(R.id.mapViewBtn);
        mapButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MapActivity.class);
            startActivity(intent);
        });
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();
                fetchNearbyCafes(currentLatitude, currentLongitude);
            } else {
                Toast.makeText(HomeActivity.this, "Unable to fetch current location.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to fetch nearby cafes using Retrofit
    private void fetchNearbyCafes(double latitude, double longitude) {
        String location = latitude + "," + longitude;
        int radius = 500;
        String type = "cafe";
        String apiKey = "AIzaSyAXKEk9m2cpmyT0WzDwwCGvndwxHWZOrYk";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PlacesApiService service = retrofit.create(PlacesApiService.class);
        Call<NearbySearchResponse> call = service.getNearbyPlaces(location, radius, type, apiKey);
        call.enqueue(new Callback<NearbySearchResponse>() {
            @Override
            public void onResponse(Call<NearbySearchResponse> call, Response<NearbySearchResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Cafe> nearbyCafes = new ArrayList<>();
                    for (NearbySearchResponse.PlaceResult result : response.body().results) {
                        String name = result.name;
                        String location = result.vicinity != null ? result.vicinity : "No address available";
                        String status = result.openingHours != null && result.openingHours.openNow ? "Open" : "Closed";
                        String placeId = result.placeId;

                        boolean hasWifi = result.types != null && result.types.contains("cafe");
                        boolean servesFood = result.types != null && result.types.contains("restaurant");

                        String imageUrl = null;
                        if (result.photos != null && !result.photos.isEmpty()) {
                            String photoReference = result.photos.get(0).photoReference;
                            imageUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="
                                    + photoReference + "&key=" + apiKey;
                        }

                        Cafe cafe = new Cafe(name, location, status, imageUrl, placeId);
                        cafe.setHasWifi(hasWifi);
                        cafe.setServesFood(servesFood);
                        nearbyCafes.add(cafe);
                    }
                    updateCafeList(nearbyCafes);
                } else {
                    Toast.makeText(HomeActivity.this, "No cafes found nearby.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<NearbySearchResponse> call, Throwable t) {
                Log.e("HomeActivity", "Error fetching cafes: " + t.getMessage());
                Toast.makeText(HomeActivity.this, "Error fetching cafes. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void filterCafes(String query) {
        List<Cafe> filteredList = new ArrayList<>();
        for (Cafe cafe : allCafes) {
            if (cafe.getName().toLowerCase().contains(query.toLowerCase()) ||
                    cafe.getLocation().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(cafe);
            }
        }
        filteredCafes.clear();
        filteredCafes.addAll(filteredList);
        cafeAdapter.notifyDataSetChanged();
    }

    private void updateCafeList(List<Cafe> cafes) {
        allCafes.clear();
        allCafes.addAll(cafes);
        filteredCafes.clear();
        filteredCafes.addAll(cafes);
        cafeAdapter.notifyDataSetChanged();
    }

    // Show the filter popup menu
    private void showFilterPopup(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.getMenuInflater().inflate(R.menu.filter_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.filter_wifi) {
                applyFilter(true, false, false);
                return true;
            } else if (item.getItemId() == R.id.filter_food) {
                applyFilter(false, true, false);
                return true;
            } else if (item.getItemId() == R.id.filter_details) {
                applyFilter(false, false, true);
                return true;
            } else if (item.getItemId() == R.id.filter_all) {
                resetFilters();
                return true;
            } else {
                return false;
            }
        });
        popupMenu.show();
    }

    // Apply the selected filter
    private void applyFilter(boolean filterWifi, boolean filterFood, boolean filterDetails) {
        List<Cafe> filteredList = new ArrayList<>();
        for (Cafe cafe : allCafes) {
            boolean matchesFilter = true;

            if (filterWifi && !cafe.hasWifi()) {
                matchesFilter = false;
            }

            if (filterFood && !cafe.servesFood()) {
                matchesFilter = false;
            }

            if (filterDetails && !cafe.isDetails()) {
                matchesFilter = false;
            }

            if (matchesFilter) {
                filteredList.add(cafe);
            }
        }

        filteredCafes.clear();
        filteredCafes.addAll(filteredList);
        cafeAdapter.notifyDataSetChanged();

        String message = filteredList.isEmpty() ? "No cafes match the selected filters." : "Filters applied.";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void resetFilters() {
        filteredCafes.clear();
        filteredCafes.addAll(allCafes);
        cafeAdapter.notifyDataSetChanged();

        Toast.makeText(this, "Showing all cafes", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("CafeFavorites", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        Map<String, ?> allFavorites = sharedPreferences.getAll();

        for (Cafe cafe : allCafes) {
            cafe.setFavorite(allFavorites.containsKey(cafe.getPlaceId()));
        }

        cafeAdapter.notifyDataSetChanged();
    }

}
