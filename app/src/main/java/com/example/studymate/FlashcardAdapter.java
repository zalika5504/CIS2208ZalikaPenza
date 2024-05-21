package com.example.studymate;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FlashcardAdapter extends RecyclerView.Adapter<FlashcardAdapter.ViewHolder> {
    private List<Flashcard> flashcardsList;
    private UserDatabaseHelper dbHelper;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public FlashcardAdapter(List<Flashcard> flashcardsList, Context context) {
        this.flashcardsList = flashcardsList;
        dbHelper = new UserDatabaseHelper(context);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView deleteTextView;

        public ViewHolder(View view) {
            super(view);
            titleTextView = view.findViewById(R.id.titleTextView);
            deleteTextView = view.findViewById(R.id.deleteTextView);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.flashcard_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Flashcard flashcard = flashcardsList.get(position);
        holder.titleTextView.setText(flashcard.getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(adapterPosition);
                }
            }
        });

        holder.deleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    showDeleteConfirmationDialog(v.getContext(), flashcardsList.get(adapterPosition));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return flashcardsList.size();
    }

    private void showDeleteConfirmationDialog(final Context context, final Flashcard flashcard) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Flashcard");
        builder.setMessage("Are you sure you want to delete this flashcard?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Delete flashcard from database and list
                deleteFlashcardFromDatabase(flashcard);
                flashcardsList.remove(flashcard);
                notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteFlashcardFromDatabase(Flashcard flashcard) {
        dbHelper.deleteFlashcard(flashcard);
    }
}
