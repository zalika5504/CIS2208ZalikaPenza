package com.example.studymate;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CardsActivity extends BaseActivity implements FlashcardAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private FlashcardAdapter adapter;
    private List<Flashcard> flashcardsList;
    private UserDatabaseHelper dbHelper;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cards);
        setupBottomNavigation();

        recyclerView = findViewById(R.id.recyclerViewFlashcards);


        int numberOfColumns = 2;
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        dbHelper = new UserDatabaseHelper(this);

        // Retrieve userId from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getLong("userId", -1);

        if (userId == -1 || !dbHelper.isUserLoggedIn(userId)) {
            showToast("Error: User not logged in.");
            finish(); // Close the activity
            return;
        }

        // Load flashcards data
        loadFlashcards();

        // Set item click listener to handle flashcard clicks
        adapter.setOnItemClickListener(this);
    }

    private void loadFlashcards() {
        // Retrieve all flashcards from the database for the current user
        flashcardsList = dbHelper.getAllFlashcards((int) userId);
        adapter = new FlashcardAdapter(flashcardsList, this);
        recyclerView.setAdapter(adapter);
    }

    // Implement onItemClick method of the FlashcardAdapter.OnItemClickListener interface
    @Override
    public void onItemClick(int position) {
        Flashcard flashcard = flashcardsList.get(position);
        // Show the description of the clicked flashcard
        showFlashcardDescription(flashcard);
    }

    // Method to show the description of the flashcard in a custom dialog
    private void showFlashcardDescription(Flashcard flashcard) {
        // Create a custom dialog layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_flashcard_dialog, null);

        // Initialize views in the dialog layout
        TextView titleTextView = dialogView.findViewById(R.id.dialogTitleTextView);
        TextView descriptionTextView = dialogView.findViewById(R.id.dialogDescriptionTextView);
        TextView closeButton = dialogView.findViewById(R.id.closeButton);

        // Set the title and description of the flashcard
        titleTextView.setText(flashcard.getTitle());
        descriptionTextView.setText(flashcard.getDescription());

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

    private void showToast(String message) {
        // Helper method to display toast messages
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
