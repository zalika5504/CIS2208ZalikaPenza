package com.example.studymate;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class QuizActivity extends BaseActivity {

    private TextView textViewTitle;
    private TextView textViewQuestion;
    private Button buttonShowAnswer;
    private UserDatabaseHelper dbHelper;
    private Flashcard currentFlashcard;
    private int currentFlashcardIndex = 0;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz);
        setupBottomNavigation();

        // Initialize UI components
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewQuestion = findViewById(R.id.textViewQuestion);
        buttonShowAnswer = findViewById(R.id.buttonShowAnswer);
        Button buttonNext = findViewById(R.id.buttonNext);

        // Initialize database helper
        dbHelper = new UserDatabaseHelper(this);

        // Retrieve userId from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getLong("userId", -1);

        if (userId == -1) {
            showToast("Error: User not logged in.");
            return;
        }

        // Load flashcard data
        loadFlashcard();

        // Set up click listener for Show Answer button
        buttonShowAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Display flashcard description
                showFlashcardDescription();
            }
        });

        // Set up click listener for Next button
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Load the next flashcard
                loadNextFlashcard();
            }
        });
    }

    private void loadFlashcard() {
        // Retrieve all flashcards from the database for the current user
        List<Flashcard> flashcards = dbHelper.getAllFlashcards((int) userId);
        if (!flashcards.isEmpty()) {
            // Get the current flashcard based on the current index
            currentFlashcard = flashcards.get(currentFlashcardIndex);
            // Display flashcard title and question
            textViewTitle.setText(currentFlashcard.getTitle());
            textViewQuestion.setText(currentFlashcard.getQuestion());
        } else {
            // Handle case where no flashcards are available
            textViewTitle.setText("No Flashcard Available");
            textViewQuestion.setText("");
        }
    }

    // Method to load the next flashcard
    private void loadNextFlashcard() {
        // Retrieve all flashcards from the database for the current user
        List<Flashcard> flashcards = dbHelper.getAllFlashcards((int) userId);
        currentFlashcardIndex++;
        // Check if the index exceeds the number of flashcards
        if (currentFlashcardIndex >= flashcards.size()) {
            // Reset the index to loop back to the first flashcard
            currentFlashcardIndex = 0;
        }
        // Get the current flashcard based on the current index
        currentFlashcard = flashcards.get(currentFlashcardIndex);
        // Display flashcard title and question
        textViewTitle.setText(currentFlashcard.getTitle());
        textViewQuestion.setText(currentFlashcard.getQuestion());
    }

    private void showFlashcardDescription() {
        showFlashcardDescriptionDialog();
    }

    private void showFlashcardDescriptionDialog() {
        if (currentFlashcard != null) {
            // Create a custom dialog layout
            View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_flashcard_dialog, null);

            // Initialize views in the dialog layout
            TextView titleTextView = dialogView.findViewById(R.id.dialogTitleTextView);
            TextView descriptionTextView = dialogView.findViewById(R.id.dialogDescriptionTextView);
            TextView closeButton = dialogView.findViewById(R.id.closeButton);

            // Set the title and description of the flashcard
            titleTextView.setText(currentFlashcard.getTitle());
            descriptionTextView.setText(currentFlashcard.getDescription());

            // Create the dialog
            final androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setView(dialogView)
                    .create();

            // Set a click listener for the close button
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss(); // Dismiss the dialog when close button is clicked
                }
            });

            // Show the dialog
            alertDialog.show();
        }
    }

    private void showToast(String message) {
        // Helper method to display toast messages
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
