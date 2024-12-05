package com.example.mobdeves19mcogr4;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class EditProfileActivity extends AppCompatActivity {

    private EditText nameEditText;
    private Button saveButton;
    private String userEmail;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        nameEditText = findViewById(R.id.nameEditText);
        saveButton = findViewById(R.id.saveButton);
        databaseHelper = new DatabaseHelper(this);
        Intent intent = getIntent();
        String currentName = intent.getStringExtra("USER_NAME");
        userEmail = intent.getStringExtra("USER_EMAIL");

        nameEditText.setText(currentName);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = nameEditText.getText().toString().trim();

                if (TextUtils.isEmpty(newName)) {
                    Toast.makeText(EditProfileActivity.this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                } else {
                    boolean success = databaseHelper.updateUserName(userEmail, newName);

                    if (success) {
                        Toast.makeText(EditProfileActivity.this, "Name updated successfully", Toast.LENGTH_SHORT).show();
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("UPDATED_NAME", newName);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    } else {
                        Toast.makeText(EditProfileActivity.this, "Failed to update name", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}

