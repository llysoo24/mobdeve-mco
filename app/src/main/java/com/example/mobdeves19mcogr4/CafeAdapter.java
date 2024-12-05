package com.example.mobdeves19mcogr4;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import android.widget.Toast;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class CafeAdapter extends RecyclerView.Adapter<CafeAdapter.CafeViewHolder> {
    private final Context context;
    private final List<Cafe> cafes;

    public CafeAdapter(Context context, List<Cafe> cafes) {
        this.context = context;
        this.cafes = cafes != null ? cafes : new ArrayList<>();
    }

    @NonNull
    @Override
    public CafeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cafe, parent, false);
        return new CafeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CafeViewHolder holder, int position) {
        Cafe cafe = cafes.get(position);

        holder.nameTextView.setText(cafe.getName());
        holder.addressTextView.setText(cafe.getLocation());
        holder.descriptionTextView.setText(cafe.getStatus() != null ? cafe.getStatus() : "No description available");

        Glide.with(context)
                .load(cafe.getImageUrl())
                .placeholder(R.drawable.logo_only)
                .into(holder.cafeImageView);

        holder.heartButton.setImageResource(cafe.isFavorite() ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
        holder.heartButton.setOnClickListener(v -> {
            boolean newFavoriteStatus = !cafe.isFavorite();
            cafe.setFavorite(newFavoriteStatus);

            SharedPreferences sharedPreferences = context.getSharedPreferences("CafeFavorites", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            if (newFavoriteStatus) {
                Gson gson = new Gson();
                String cafeJson = gson.toJson(cafe);
                editor.putString(cafe.getPlaceId(), cafeJson);
            } else {
                editor.remove(cafe.getPlaceId());
            }
            editor.apply();

            holder.heartButton.setImageResource(newFavoriteStatus ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);

            if (newFavoriteStatus) {
                sendFavoriteNotification(cafe);
            }

        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CafeDetailsActivity.class);
            intent.putExtra("placeId", cafe.getPlaceId());
            intent.putExtra("cafeName", cafe.getName());
            intent.putExtra("cafeLocation", cafe.getLocation());
            intent.putExtra("cafeStatus", cafe.getStatus());
            intent.putExtra("cafeImageUrl", cafe.getImageUrl());

            context.startActivity(intent);
        });
    }

    private void sendFavoriteNotification(Cafe cafe) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "favorite_channel")
                .setSmallIcon(R.drawable.ic_heart_filled)
                .setContentTitle("Cafe Added to Favorites")
                .setContentText(cafe.getName() + " has been added to your favorites!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Favorites Notifications";
            String description = "Notifications when cafes are added to favorites";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            android.app.NotificationChannel channel = new android.app.NotificationChannel("favorite_channel", name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(0, builder.build());
    }

    @Override
    public int getItemCount() {
        return cafes.size();
    }
    static class CafeViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView addressTextView;
        TextView descriptionTextView;
        ImageButton heartButton;
        ImageView cafeImageView;

        public CafeViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.cafeName);
            addressTextView = itemView.findViewById(R.id.cafeAddress);
            descriptionTextView = itemView.findViewById(R.id.cafeDescription);
            heartButton = itemView.findViewById(R.id.heartButton);
            cafeImageView = itemView.findViewById(R.id.cafeImage);
        }
    }

    public void updateData(List<Cafe> updatedCafes) {
        this.cafes.clear();
        this.cafes.addAll(updatedCafes);
        notifyDataSetChanged();
    }
}
