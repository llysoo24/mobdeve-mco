package com.example.mobdeves19mcogr4;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlacesApiService {
    @GET("maps/api/place/nearbysearch/json")
    Call<NearbySearchResponse> getNearbyPlaces(
            @Query("location") String location,
            @Query("radius") int radius,
            @Query("type") String type,
            @Query("key") String apiKey
    );

    @GET("maps/api/place/details/json")
    Call<PlaceDetailsResponse> getPlaceDetails(
            @Query("place_id") String placeId,
            @Query("key") String apiKey
    );
}