package com.example.studymate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        Button logoutButton = findViewById(R.id.logoutButton); // Find the logout button in profile.xml
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect the user to the first screen
                Intent intent = new Intent(ProfileActivity.this, FirstScreenActivity.class);
                startActivity(intent);
                finish(); // Finish current activity to prevent user from going back to it
            }
        });
    }
}
