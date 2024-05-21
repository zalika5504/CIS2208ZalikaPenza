package com.example.studymate;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BaseActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.navigation_home) {
                    // Handle Home item click
                    openHomeActivity();
                    return true;
                } else if (item.getItemId() == R.id.navigation_cards) {
                    // Handle Cards item click
                    openCardsActivity();
                    return true;
                } else if (item.getItemId() == R.id.navigation_quiz) {
                    // Handle Quiz item click
                    openQuizActivity();
                    return true;
                } else if (item.getItemId() == R.id.navigation_profile) {
                    // Handle Settings item click
                    openProfileActivity();
                    return true;
                }
                return false;
            }
        });
    }

    protected void openHomeActivity() {
        // Open HomeActivity
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    protected void openCardsActivity() {
        // Open CardsActivity
        Intent intent = new Intent(this, CardsActivity.class);
        startActivity(intent);
    }

    protected void openQuizActivity() {
        // Open QuizActivity
        Intent intent = new Intent(this, QuizActivity.class);
        startActivity(intent);
    }

    protected void openProfileActivity() {
        // Open SettingsActivity
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }
}
