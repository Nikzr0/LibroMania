package org.example;

public class SearchResult {
    private final String title;
    private final String author;
    private final int year;
    private final int borrowCount;

    public SearchResult(String title, String author, int year, int borrowCount) {
        this.title = title;
        this.author = author;
        this.year = year;
        this.borrowCount = borrowCount;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getYear() {
        return year;
    }

    public int getBorrowCount() {
        return borrowCount;
    }
}
