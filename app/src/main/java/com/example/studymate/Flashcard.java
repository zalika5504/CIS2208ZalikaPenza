package com.example.studymate;

public class Flashcard {
    private int id;
    private String title;
    private String description;

    private String question;

    public Flashcard(int id, String title, String description,String question) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.question = question;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getQuestion() { return question; }
}
