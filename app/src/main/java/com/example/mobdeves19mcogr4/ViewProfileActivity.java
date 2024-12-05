package com.example.mobdeves19mcogr4;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ViewProfileActivity extends AppCompatActivity {
    private String userEmail;
    private TextView nameTextView;
    private TextView emailTextView;
    private Button editProfileButton;
    private RecyclerView favoriteCafesRecyclerView;
    private CafeAdapter favoriteCafesAdapter;
    private List<Cafe> favoriteCafes;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        databaseHelper = new DatabaseHelper(this);

        ImageView profilePhotoImageView = findViewById(R.id.profileImageView);
        favoriteCafesRecyclerView = findViewById(R.id.favoriteCafesRecyclerView);
        favoriteCafesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        favoriteCafes = new ArrayList<>();
        favoriteCafesAdapter = new CafeAdapter(this, favoriteCafes);
        favoriteCafesRecyclerView.setAdapter(favoriteCafesAdapter);

        loadFavoriteCafes();

        nameTextView = findViewById(R.id.nameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        editProfileButton = findViewById(R.id.editProfileButton);

        Intent intent = getIntent();
        String userEmail = intent.getStringExtra("USER_EMAIL");
        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        String userName = databaseHelper.getUserName(userEmail);
        String photoUri = databaseHelper.getUserPhoto(userEmail);

        nameTextView.setText(userName);
        emailTextView.setText(userEmail);

        if (photoUri != null) {
            profilePhotoImageView.setImageURI(Uri.parse(photoUri));
        } else {
            profilePhotoImageView.setImageResource(R.drawable.default_img); // Default photo
        }

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewProfileActivity.this, EditProfileActivity.class);
                intent.putExtra("USER_NAME", nameTextView.getText().toString());
                intent.putExtra("USER_EMAIL", emailTextView.getText().toString());
                startActivityForResult(intent, 1);
            }
        });

    }
    private void loadFavoriteCafes() {
        SharedPreferences sharedPreferences = getSharedPreferences("CafeFavorites", Context.MODE_PRIVATE);
        favoriteCafes.clear();

        Map<String, ?> allFavorites = sharedPreferences.getAll();
        Gson gson = new Gson();

        for (Map.Entry<String, ?> entry : allFavorites.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String) {
                String cafeJson = (String) value;
                try {
                    Cafe cafe = gson.fromJson(cafeJson, Cafe.class);
                    if (cafe != null) {
                        favoriteCafes.add(cafe);
                    }
                } catch (Exception e) {
                    Log.e("ViewProfileActivity", "Error parsing favorite cafe: " + e.getMessage());
                }
            } else {
                Log.w("ViewProfileActivity", "Skipping invalid entry: " + entry.getKey());
            }
        }

        if (favoriteCafesAdapter != null) {
            favoriteCafesAdapter.notifyDataSetChanged();
        } else {
            Log.e("ViewProfileActivity", "CafeAdapter is not initialized!");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String updatedName = data.getStringExtra("UPDATED_NAME");
            if (updatedName != null) {
                nameTextView.setText(updatedName);
            }
        }
    }

    private Cafe getCafeByPlaceId(String placeId) {
        return new Cafe("Cafe Name", "Cafe Location", "Open", "Image URL", placeId);
    }
}
