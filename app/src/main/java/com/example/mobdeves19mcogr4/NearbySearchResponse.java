package com.example.mobdeves19mcogr4;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class NearbySearchResponse {
    @SerializedName("results")
    public List<PlaceResult> results;

    public static class PlaceResult {
        @SerializedName("place_id")
        public String placeId;

        @SerializedName("name")
        public String name;

        @SerializedName("vicinity")
        public String vicinity;

        @SerializedName("geometry")
        public Geometry geometry;

        @SerializedName("opening_hours")
        public OpeningHours openingHours;

        @SerializedName("photos")
        public List<Photo> photos; // Add this field to handle photo references

        public List<String> types;

        public static class Photo {
            @SerializedName("photo_reference")
            public String photoReference;

        }
    }

    public static class Geometry {
        @SerializedName("location")
        public LatLng location;
    }

    public static class LatLng {
        @SerializedName("lat")
        public double lat;

        @SerializedName("lng")
        public double lng;
    }

    public static class OpeningHours {
        @SerializedName("open_now")
        public boolean openNow;
    }
}
