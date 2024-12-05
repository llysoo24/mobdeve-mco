package com.example.mobdeves19mcogr4;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PlaceDetailsResponse {

    @SerializedName("result")
    public PlaceDetails result;

    public static class PlaceDetails {

        @SerializedName("name")
        public String name;

        @SerializedName("formatted_address")
        public String formattedAddress;

        @SerializedName("formatted_phone_number")
        public String formattedPhoneNumber;

        @SerializedName("rating")
        public double rating;

        @SerializedName("opening_hours")
        public OpeningHours openingHours;

        @SerializedName("reviews")
        public List<Review> reviews;
    }

    public static class OpeningHours {
        @SerializedName("open_now")
        public boolean openNow;

        @SerializedName("weekday_text")
        public List<String> weekdayText;
    }

    public static class Review {
        @SerializedName("author_name")
        public String authorName;

        @SerializedName("text")
        public String text;

        @SerializedName("rating")
        public double rating;

        @SerializedName("relative_time_description")
        public String relativeTimeDescription;
    }
}
