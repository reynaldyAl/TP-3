package com.example.tugaspraktikum01;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int EDIT_PROFILE_REQUEST_CODE = 1001;

    private TextView profileName;
    private TextView profileBio;
    private TextView usernameTitle;
    private ShapeableImageView profileImage;
    private ShapeableImageView navProfileImage; // Image in navbar
    private MaterialButton btnEditProfile;
    private MaterialButton btnShareProfile;
    private View addNewHighlight;

    // Default profile values
    private String name = "Manusia hidden gems";
    private String username = "reynaldy_al";
    private String website = "www.instagram.com/reynaldy_al";
    private String bio = "Photography • Music • Sleep\nBe my luck";
    private String email = "reynaldy@example.com";
    private String phone = "+62 8123456789";
    private String profileImagePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Log.d(TAG, "MainActivity onCreate started");

        // Initialize views
        initializeViews();

        // Setup button listeners
        setupButtonListeners();

        // Update UI with current data
        updateUI();
    }

    private void initializeViews() {
        profileName = findViewById(R.id.profile_name);
        profileBio = findViewById(R.id.profile_bio);
        usernameTitle = findViewById(R.id.username_title);
        profileImage = findViewById(R.id.profile_image);
        navProfileImage = findViewById(R.id.nav_profile);
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        btnShareProfile = findViewById(R.id.btn_share_profile);
        addNewHighlight = findViewById(R.id.add_new_highlight);

        Log.d(TAG, "Views initialized. Edit Profile Button: " +
                (btnEditProfile != null ? "found" : "not found") +
                ", Profile Image: " + (profileImage != null ? "found" : "not found") +
                ", Nav Profile Image: " + (navProfileImage != null ? "found" : "not found"));
    }

    /**
     * Sets up button listeners for Edit Profile and Share Profile buttons
     */
    private void setupButtonListeners() {
        // Edit Profile button listener
        if (btnEditProfile != null) {
            btnEditProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Edit Profile button clicked");
                    launchEditProfile();
                }
            });
        }

        // Share Profile button listener
        if (btnShareProfile != null) {
            btnShareProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out my Instagram profile");
                    shareIntent.putExtra(Intent.EXTRA_TEXT,
                            "Check out my Instagram profile: https://www.instagram.com/" + username);
                    startActivity(Intent.createChooser(shareIntent, "Share via"));
                }
            });
        }

        // Add New Highlight button listener
        if (addNewHighlight != null) {
            addNewHighlight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Add New Highlight clicked");
                    Toast.makeText(MainActivity.this, "Add new highlight feature", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * Launch the EditProfileActivity with current profile data as intent extras
     */
    private void launchEditProfile() {
        Intent intent = new Intent(MainActivity.this, EditProfileActivity.class);
//        bisa simplified menggunakan parcelable
        // Add all profile data as extras
        intent.putExtra("name", name);
        intent.putExtra("username", username);
        intent.putExtra("website", website);
        intent.putExtra("bio", bio);
        intent.putExtra("email", email);
        intent.putExtra("phone", phone);
        intent.putExtra("profileImagePath", profileImagePath);

        // Start activity expecting a result
        startActivityForResult(intent, EDIT_PROFILE_REQUEST_CODE);
    }

    /**
     * Handle result from EditProfileActivity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_PROFILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Get updated profile data from the returning intent
            name = data.getStringExtra("name");
            username = data.getStringExtra("username");
            website = data.getStringExtra("website");
            bio = data.getStringExtra("bio");
            email = data.getStringExtra("email");
            phone = data.getStringExtra("phone");
            profileImagePath = data.getStringExtra("profileImagePath");

            Log.d(TAG, "Received updated profile data: name=" + name + ", username=" + username);
            Log.d(TAG, "Updated profile image path: " + profileImagePath);

            // Update the UI with the new data
            updateUI();

            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Update the UI with current profile data
     */
    private void updateUI() {
        try {
            // Create a combined bio with website at the end if not already included
            String fullBio = bio;
            if (!website.isEmpty() && !bio.contains(website)) {
                fullBio = bio + "\n" + website;
            }

            // Update UI elements
            if (profileName != null) profileName.setText(name);
            if (usernameTitle != null) usernameTitle.setText(username);
            if (profileBio != null) profileBio.setText(fullBio);

            Log.d(TAG, "User data updated in UI: " + name + ", " + username);

            // Update profile image if available
            if (profileImagePath != null && !profileImagePath.isEmpty()) {
                try {
                    Uri imageUri = Uri.parse(profileImagePath);

                    // Check if the URI is a file path
                    if (imageUri.toString().startsWith("file:")) {
                        File imgFile = new File(imageUri.getPath());
                        if (imgFile.exists()) {
                            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                            // Update main profile image
                            if (profileImage != null) {
                                profileImage.setImageBitmap(bitmap);
                            }

                            // Update navbar profile image
                            if (navProfileImage != null) {
                                navProfileImage.setImageBitmap(bitmap);
                            }

                            Log.d(TAG, "Profile images updated from file path");
                            return; // Exit early as we've set the images successfully
                        }
                    }

                    // If we reach here, try to load the URI directly
                    if (profileImage != null) {
                        profileImage.setImageURI(null); // Clear cache
                        profileImage.setImageURI(imageUri);
                    }

                    if (navProfileImage != null) {
                        navProfileImage.setImageURI(null); // Clear cache
                        navProfileImage.setImageURI(imageUri);
                    }

                    Log.d(TAG, "Profile images updated from URI");

                } catch (Exception e) {
                    Log.e(TAG, "Error loading profile image: " + e.getMessage());
                    // Fallback to default image
                    if (profileImage != null) profileImage.setImageResource(R.drawable.profil_pict);
                    if (navProfileImage != null) navProfileImage.setImageResource(R.drawable.profil_pict);
                }
            } else {
                Log.d(TAG, "No custom profile image set, using default");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in updateUI: " + e.getMessage());
        }
    }

    // Method for XML onClick attribute if needed
    public void onEditProfileClick(View view) {
        Log.d(TAG, "onEditProfileClick called from XML");
        launchEditProfile();
    }
}