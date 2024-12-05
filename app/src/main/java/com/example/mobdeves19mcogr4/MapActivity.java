package com.example.mobdeves19mcogr4;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        googleMap.setMyLocationEnabled(true);
        getCurrentLocationAndShowCafes();
    }

    private void getCurrentLocationAndShowCafes() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                googleMap.addMarker(new MarkerOptions().position(currentLocation).title("You are here"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                fetchNearbyCafes(currentLocation);
            } else {
                Toast.makeText(MapActivity.this, "Unable to fetch current location.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Log.e("MapsActivity", "Error fetching location: " + e.getMessage()));
    }

    private void fetchNearbyCafes(LatLng location) {
        String apiKey = "AIzaSyAXKEk9m2cpmyT0WzDwwCGvndwxHWZOrYk";
        String locationString = location.latitude + "," + location.longitude;
        int radius = 500;
        String type = "cafe";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PlacesApiService service = retrofit.create(PlacesApiService.class);

        Call<NearbySearchResponse> call = service.getNearbyPlaces(locationString, radius, type, apiKey);
        call.enqueue(new Callback<NearbySearchResponse>() {
            @Override
            public void onResponse(Call<NearbySearchResponse> call, Response<NearbySearchResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (NearbySearchResponse.PlaceResult result : response.body().results) {
                        LatLng cafeLocation = new LatLng(result.geometry.location.lat, result.geometry.location.lng);
                        Marker marker = googleMap.addMarker(new MarkerOptions()
                                .position(cafeLocation)
                                .title(result.name));
                        if (marker != null) {
                            marker.setTag(result.placeId);
                        }
                    }
                } else {
                    Toast.makeText(MapActivity.this, "No cafes found.", Toast.LENGTH_SHORT).show();
                }

                googleMap.setOnMarkerClickListener(clickedMarker -> {
                    String placeId = (String) clickedMarker.getTag();
                    if (placeId != null) {
                        fetchPlaceDetails(placeId, apiKey, clickedMarker);
                    }
                    return false;
                });
            }

            @Override
            public void onFailure(Call<NearbySearchResponse> call, Throwable t) {
                Log.e("MapsActivity", "Error fetching cafes: " + t.getMessage());
                Toast.makeText(MapActivity.this, "Error fetching cafes. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchPlaceDetails(String placeId, String apiKey, Marker marker) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PlacesApiService service = retrofit.create(PlacesApiService.class);

        Call<PlaceDetailsResponse> call = service.getPlaceDetails(placeId, apiKey);
        call.enqueue(new Callback<PlaceDetailsResponse>() {
            @Override
            public void onResponse(Call<PlaceDetailsResponse> call, Response<PlaceDetailsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PlaceDetailsResponse.PlaceDetails details = response.body().result;
                    String info = details.name + "\n" + details.formattedAddress + "\nRating: " + details.rating;
                    Toast.makeText(MapActivity.this, info, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<PlaceDetailsResponse> call, Throwable t) {
                Log.e("MapsActivity", "Error fetching place details: " + t.getMessage());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocationAndShowCafes();
            } else {
                Toast.makeText(this, "Location permission denied. Unable to fetch location.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
