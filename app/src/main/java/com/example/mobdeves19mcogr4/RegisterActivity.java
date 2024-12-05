package com.example.mobdeves19mcogr4;

import android.content.Intent; // Import Intent class
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View; // Import View class
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;
    private Bitmap selectedImageBitmap;
    private EditText emailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailEditText = findViewById(R.id.emailEditText);

        databaseHelper = new DatabaseHelper(this);
        Button uploadPhotoButton = findViewById(R.id.uploadPhotoButton);
        EditText nameEditText = findViewById(R.id.nameEditText);
        EditText emailEditText = findViewById(R.id.emailEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        Button submitRegisterButton = findViewById(R.id.submitRegisterButton);

        uploadPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        submitRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String photoPath = selectedImageUri != null ? selectedImageUri.toString() : null;

                if (databaseHelper.insertUser(name, email, password, photoPath)) {
                    Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    intent.putExtra("USER_NAME", name);
                    intent.putExtra("USER_EMAIL", email);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                String photoPath = selectedImageUri.toString();
                // Save the photo path for the user in the database
                databaseHelper.updateUserPhoto(emailEditText.getText().toString(), photoPath);
            }
        }
    }
}

