package com.example.tugaspraktikum01;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    private TextView profileName;
    private TextView profileBio;
    private TextView usernameTitle;
    private ShapeableImageView profileImage;
    private MaterialButton btnEditProfile;
    private MaterialButton btnShareProfile;
    private View addNewHighlight;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Alternatif 1: Gunakan layout terpisah jika Anda membuat activity_profile.xml
        // setContentView(R.layout.activity_profile);

        // Alternatif 2: Gunakan activity_main.xml jika tidak membuat layout terpisah
        setContentView(R.layout.activity_main);

        Log.d(TAG, "ProfileActivity onCreate started");

        // Initialize views
        initializeViews();

        // Initialize SharedPreferences
        preferences = getSharedPreferences("UserProfile", MODE_PRIVATE);

        // Setup button listeners
        setupButtonListeners();

        // Load user data
        loadUserData();
    }

    private void initializeViews() {
        try {
            profileName = findViewById(R.id.profile_name);
            profileBio = findViewById(R.id.profile_bio);
            usernameTitle = findViewById(R.id.username_title);
            profileImage = findViewById(R.id.profile_image);
            btnEditProfile = findViewById(R.id.btn_edit_profile);
            btnShareProfile = findViewById(R.id.btn_share_profile);
            addNewHighlight = findViewById(R.id.add_new_highlight);

            Log.d(TAG, "Views initialized successfully");

            if (btnEditProfile == null) {
                Log.e(TAG, "Button Edit Profile tidak ditemukan!");
                Toast.makeText(this, "Button Edit Profile tidak ditemukan", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views: " + e.getMessage());
        }
    }

    private void setupButtonListeners() {
        if (btnEditProfile != null) {
            btnEditProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Button Edit Profile diklik!");

                    try {
                        Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.e(TAG, "Error navigating to EditProfileActivity: " + e.getMessage());
                        Toast.makeText(ProfileActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        if (btnShareProfile != null) {
            btnShareProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out my Instagram profile");
                    shareIntent.putExtra(Intent.EXTRA_TEXT,
                            "Check out my Instagram profile: https://www.instagram.com/" +
                                    usernameTitle.getText().toString());
                    startActivity(Intent.createChooser(shareIntent, "Share via"));
                }
            });
        }

        if (addNewHighlight != null) {
            addNewHighlight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ProfileActivity.this, "Add new highlight", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
    }

    private void loadUserData() {
        try {
            String name = preferences.getString("name", "Manusia hidden gems");
            String username = preferences.getString("username", "reynaldy_al");
            String bio = preferences.getString("bio", "Photography • Music • Sleep\nBe my luck\nwww.instagram.com/reynaldy_al");

            if (profileName != null) profileName.setText(name);
            if (usernameTitle != null) usernameTitle.setText(username);
            if (profileBio != null) profileBio.setText(bio);

            Log.d(TAG, "Data loaded - Name: " + name + ", Username: " + username);
        } catch (Exception e) {
            Log.e(TAG, "Error loading data: " + e.getMessage());
        }
    }

    // Method untuk XML onClick attribute
    public void onEditProfileClick(View view) {
        Log.d(TAG, "onEditProfileClick from XML called");
        Intent intent = new Intent(this, EditProfileActivity.class);
        startActivity(intent);
    }
}