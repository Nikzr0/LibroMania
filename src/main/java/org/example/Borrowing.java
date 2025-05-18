package org.example;

import java.sql.Date;

public class Borrowing {
    private int id;
    private String bookTitle;
    private String readerName;
    private Date dateBorrowed;

    public Borrowing(int id, String bookTitle, String readerName, Date dateBorrowed) {
        this.id = id;
        this.bookTitle = bookTitle;
        this.readerName = readerName;
        this.dateBorrowed = dateBorrowed;
    }

    public int getId() {
        return id;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getReaderName() {
        return readerName;
    }

    public Date getDateBorrowed() {
        return dateBorrowed;
    }
}
