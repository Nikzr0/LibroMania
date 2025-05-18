package org.example;

public class Book {
    private int id;
    private String title;
    private String authorName;
    private int publishYear;
    private int authorId;

    public Book(int id, String title, int authorId, String authorName, int publishYear) {
        this.id = id;
        this.title = title;
        this.authorId = authorId;
        this.authorName = authorName;
        this.publishYear = publishYear;
    }

    public int getBookId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthorName() {
        return authorName;
    }

    public int getPublishYear() {
        return publishYear;
    }

    public int getAuthorId() {
        return authorId;
    }

    @Override
    public String toString() {
        return title + " by " + authorName;
    }
}
