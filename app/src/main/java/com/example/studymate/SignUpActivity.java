package com.example.studymate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextPassword;
    private Button buttonSignUp;
    private TextView textViewLogin;
    private UserDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        // Initialize UI elements
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        textViewLogin = findViewById(R.id.textViewLogin); // Initialize login TextView

        // Initialize database helper
        databaseHelper = new UserDatabaseHelper(this);

        // Set click listener for sign-up button
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

        // Set click listener for the login link
        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to login activity when login link is clicked
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void signUp() {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Validate input fields
        if (!validateInput(name, email, password)) {
            return;
        }

        // Add user to the database
        long result = databaseHelper.addUser(name, email, password, this);
        if (result != -1) {
            // Sign-up successful
            showSignUpSuccess();
            redirectToMainActivity();
        } else {
            // Sign-up failed
            showSignUpFailure();
        }
    }

    private boolean validateInput(String name, String email, String password) {
        // Check if any field is empty
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void showSignUpSuccess() {
        Toast.makeText(this, "Sign-up successful", Toast.LENGTH_SHORT).show();
    }

    private void showSignUpFailure() {
        Toast.makeText(this, "Sign-up failed. Please try again", Toast.LENGTH_SHORT).show();
    }

    private void redirectToMainActivity() {
        Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
        startActivity(intent);
        finish(); // Close sign-up activity
    }
}
