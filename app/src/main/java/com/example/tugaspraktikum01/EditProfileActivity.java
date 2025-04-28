package com.example.tugaspraktikum01;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.imageview.ShapeableImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etName;
    private EditText etUsername;
    private EditText etWebsite;
    private EditText etBio;
    private EditText etEmail;
    private EditText etPhone;
    private TextView btnSave;
    private ImageButton btnBack;
    private TextView btnChangePhoto;
    private ShapeableImageView profileImage;

    private Uri selectedImageUri;
    private String profileImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Log.d(TAG, "EditProfileActivity onCreate started");

        // Initialize views
        initializeViews();

        // Load data from intent
        loadDataFromIntent();

        // Set click listeners
        setupClickListeners();
    }

    private void initializeViews() {
        try {
            etName = findViewById(R.id.et_name);
            etUsername = findViewById(R.id.et_username);
            etWebsite = findViewById(R.id.et_website);
            etBio = findViewById(R.id.et_bio);
            etEmail = findViewById(R.id.et_email);
            etPhone = findViewById(R.id.et_phone);
            btnSave = findViewById(R.id.btn_save);
            btnBack = findViewById(R.id.btn_back);
            btnChangePhoto = findViewById(R.id.btn_change_photo);
            profileImage = findViewById(R.id.profile_image);

            Log.d(TAG, "Views initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views: " + e.getMessage());
            Toast.makeText(this, "Error initializing views: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void loadDataFromIntent() {
        try {
            Intent intent = getIntent();

            if (intent != null) {
                // note : lebih baik menggunakan parcelable, gunakan object
                // Load profile data from intent extras
                String name = intent.getStringExtra("name");
                String username = intent.getStringExtra("username");
                String website = intent.getStringExtra("website");
                String bio = intent.getStringExtra("bio");
                String email = intent.getStringExtra("email");
                String phone = intent.getStringExtra("phone");
                profileImagePath = intent.getStringExtra("profileImagePath");

                // Set values to edit fields
                if (name != null) etName.setText(name);
                if (username != null) etUsername.setText(username);
                if (website != null) etWebsite.setText(website);
                if (bio != null) etBio.setText(bio);
                if (email != null) etEmail.setText(email);
                if (phone != null) etPhone.setText(phone);

                Log.d(TAG, "Loaded data from intent - Name: " + name + ", Username: " + username);

                // Load profile image if available
                if (profileImagePath != null && !profileImagePath.isEmpty()) {
                    try {
                        // Handle file paths
                        if (profileImagePath.startsWith("file:")) {
                            File imgFile = new File(Uri.parse(profileImagePath).getPath());
                            if (imgFile.exists()) {
                                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                                profileImage.setImageBitmap(bitmap);
                                Log.d(TAG, "Loaded image from file path: " + profileImagePath);
                                return; // Exit early
                            }
                        }

                        // If not a file or file doesn't exist, try as URI
                        Uri imageUri = Uri.parse(profileImagePath);
                        profileImage.setImageURI(imageUri);
                        Log.d(TAG, "Loaded image from URI: " + profileImagePath);
                    } catch (Exception e) {
                        profileImage.setImageResource(R.drawable.profil_pict);
                        Log.e(TAG, "Error loading profile image, using default: " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading data from intent: " + e.getMessage());
            Toast.makeText(this, "Error loading profile data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupClickListeners() {
        // Save button click listener
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Save button clicked");
                returnResultAndFinish();
            }
        });

        // Back button click listener
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Back button clicked");
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        // Change photo button click listener
        btnChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Change photo button clicked");
                try {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, PICK_IMAGE_REQUEST);
                } catch (Exception e) {
                    Log.e(TAG, "Error opening gallery: " + e.getMessage());
                    Toast.makeText(EditProfileActivity.this, "Error opening gallery: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void returnResultAndFinish() {
        try {
            // Get data from input fields
            String name = etName.getText().toString();
            String username = etUsername.getText().toString();
            String website = etWebsite.getText().toString();
            String bio = etBio.getText().toString();
            String email = etEmail.getText().toString();
            String phone = etPhone.getText().toString();

            // Validate data
            if (username.isEmpty()) {
                Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create intent for sending data back to MainActivity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("name", name);
            resultIntent.putExtra("username", username);
            resultIntent.putExtra("website", website);
            resultIntent.putExtra("bio", bio);
            resultIntent.putExtra("email", email);
            resultIntent.putExtra("phone", phone);

            // Handle profile image - if new image was selected, save it
            if (selectedImageUri != null) {
                // Create a persistent copy of the image file
                String newImagePath = saveImageToAppStorage(selectedImageUri);
                if (newImagePath != null) {
                    resultIntent.putExtra("profileImagePath", newImagePath);
                    Log.d(TAG, "Saved persistent image at: " + newImagePath);
                } else {
                    // Fallback to using the URI directly
                    resultIntent.putExtra("profileImagePath", selectedImageUri.toString());
                    Log.d(TAG, "Saved image URI: " + selectedImageUri.toString());
                }
            } else {
                // If no new image was selected, keep the original path
                resultIntent.putExtra("profileImagePath", profileImagePath);
            }

            // Set the result and finish
            setResult(RESULT_OK, resultIntent);
            finish();

            Log.d(TAG, "Profile data returned to MainActivity");
            Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error returning data: " + e.getMessage());
            Toast.makeText(this, "Error saving profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Saves an image to the app's private storage directory
     * @param sourceUri The URI of the image to save
     * @return The path to the saved image as a file URI string, or null if failed
     */
    private String saveImageToAppStorage(Uri sourceUri) {
        try {
            // Create directory if it doesn't exist
            File storageDir = new File(getFilesDir(), "profile_images");
            if (!storageDir.exists()) {
                storageDir.mkdirs();
            }

            // Delete previous profile image if it exists
            if (profileImagePath != null && !profileImagePath.isEmpty() && profileImagePath.startsWith("file:")) {
                try {
                    File previousFile = new File(Uri.parse(profileImagePath).getPath());
                    if (previousFile.exists()) {
                        previousFile.delete();
                        Log.d(TAG, "Deleted previous profile image");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error deleting previous image: " + e.getMessage());
                }
            }

            // Create a new file for the image
            File imageFile = new File(storageDir, "profile_" + System.currentTimeMillis() + ".jpg");

            // Copy the image data
            InputStream inputStream = getContentResolver().openInputStream(sourceUri);
            FileOutputStream outputStream = new FileOutputStream(imageFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

            // Return the file path as a file URI string
            return "file:" + imageFile.getAbsolutePath();

        } catch (IOException e) {
            Log.e(TAG, "Error saving image to app storage: " + e.getMessage());
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                selectedImageUri = data.getData();
                profileImage.setImageURI(selectedImageUri);
                Log.d(TAG, "New image selected: " + selectedImageUri);
            } catch (Exception e) {
                Log.e(TAG, "Error loading selected image: " + e.getMessage());
                Toast.makeText(this, "Failed to load image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}