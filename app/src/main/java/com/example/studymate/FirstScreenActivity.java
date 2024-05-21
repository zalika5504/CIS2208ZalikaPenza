package com.example.studymate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class FirstScreenActivity extends AppCompatActivity {

    private Button buttonLogin;
    private Button buttonSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);

        // Initialize UI elements
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonSignup = findViewById(R.id.buttonSignup);

        // Set click listener for the "Login" button
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start LoginActivity when the "Login" button is clicked
                Intent intent = new Intent(FirstScreenActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        // Set click listener for the "Sign Up" button
        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start SignUpActivity when the "Sign Up" button is clicked
                Intent intent = new Intent(FirstScreenActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

    }
}
