package com.example.studymate;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class HomeActivity extends BaseActivity {

    private EditText editTextTitle;
    private EditText editTextDescription;
    private EditText editTextQuestion;
    private Button buttonSaveFlashcard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        setupBottomNavigation();

        // Initialize UI components
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextQuestion = findViewById(R.id.editTextQuestion);
        buttonSaveFlashcard = findViewById(R.id.buttonSaveFlashcard);

        // Set onClickListener for the button
        buttonSaveFlashcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform action when the button is clicked
                saveFlashcard();
            }
        });
    }

    private void saveFlashcard() {
        // Retrieve data from UI components
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String question = editTextQuestion.getText().toString().trim();

        // Check if title or description or question is empty
        if (title.isEmpty() || description.isEmpty() || question.isEmpty()) {
            showToast("Please enter title, description, and question.");
            return;
        }

        // Retrieve userId from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        long userId = sharedPreferences.getLong("userId", -1);

        if (userId == -1) {
            showToast("Error: User not logged in.");
            return;
        }

        // Save flashcard data into the database
        UserDatabaseHelper dbHelper = new UserDatabaseHelper(this);
        long result = dbHelper.addFlashcard(title, description, question, (int) userId);

        if (result != -1) {
            // Flashcard saved successfully
            showToast("Flashcard saved: Title - " + title + ", Description - " + description + ", Question - " + question);

            // Clear input fields after saving
            editTextTitle.setText("");
            editTextDescription.setText("");
            editTextQuestion.setText("");
        } else {
            // Failed to save flashcard
            showToast("Failed to save flashcard.");
        }
    }

    private void showToast(String message) {
        // Helper method to display toast messages
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
