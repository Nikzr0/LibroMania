package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:h2:~/libromania";
    private static final String USER = "sa";
    private static final String PASS = "";

    private Connection connection;

    public DatabaseManager() {
        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            createTablesIfNotExist();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTablesIfNotExist() throws SQLException {
        String[] createTables = {
                "CREATE TABLE IF NOT EXISTS Authors (" +
                        "author_id INT PRIMARY KEY AUTO_INCREMENT, " +
                        "name VARCHAR(255) NOT NULL, " +
                        "country VARCHAR(100))",

                "CREATE TABLE IF NOT EXISTS Books (" +
                        "book_id INT PRIMARY KEY AUTO_INCREMENT, " +
                        "title VARCHAR(255) NOT NULL, " +
                        "author_id INT, " +
                        "publish_year INT, " +
                        "FOREIGN KEY (author_id) REFERENCES Authors(author_id))",

                "CREATE TABLE IF NOT EXISTS Readers (" +
                        "reader_id INT PRIMARY KEY AUTO_INCREMENT, " +
                        "name VARCHAR(255) NOT NULL, " +
                        "email VARCHAR(255))",

                "CREATE TABLE IF NOT EXISTS Borrowings (" +
                        "borrowing_id INT PRIMARY KEY AUTO_INCREMENT, " +
                        "book_id INT, " +
                        "reader_id INT, " +
                        "date_borrowed DATE, " +
                        "return_date DATE, " +
                        "FOREIGN KEY (book_id) REFERENCES Books(book_id), " +
                        "FOREIGN KEY (reader_id) REFERENCES Readers(reader_id))"
        };

        for (String sql : createTables) {
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(sql);
            }
        }
    }

    public List<Book> getAvailableBooks() throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT b.book_id, b.title, b.author_id, a.name as author_name, b.publish_year " +
                "FROM Books b " +
                "JOIN Authors a ON b.author_id = a.author_id " +
                "WHERE b.book_id NOT IN (SELECT book_id FROM Borrowings)";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                books.add(new Book(
                        rs.getInt("book_id"),
                        rs.getString("title"),
                        rs.getInt("author_id"),
                        rs.getString("author_name"),
                        rs.getInt("publish_year")
                ));
            }
        }
        return books;
    }

    public void deleteBorrowing(int borrowingId) throws SQLException {
        String sql = "DELETE FROM Borrowings WHERE borrowing_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, borrowingId);
            pstmt.executeUpdate();
        }
    }

    public List<Author> getAllAuthors() throws SQLException {
        List<Author> authors = new ArrayList<>();
        String sql = "SELECT * FROM Authors";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                authors.add(new Author(
                        rs.getInt("author_id"),
                        rs.getString("name"),
                        rs.getString("country")
                ));
            }
        }
        return authors;
    }

    public void addAuthor(String name, String country) throws SQLException {
        String sql = "INSERT INTO Authors (name, country) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, country);
            pstmt.executeUpdate();
        }
    }

    public void updateAuthor(int id, String name, String country) throws SQLException {
        String sql = "UPDATE Authors SET name = ?, country = ? WHERE author_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, country);
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
        }
    }

    public void deleteAuthor(int id) throws SQLException {
        String sql = "DELETE FROM Authors WHERE author_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    public List<Book> getAllBooks() throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT b.*, a.name as author_name FROM Books b LEFT JOIN Authors a ON b.author_id = a.author_id";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                books.add(new Book(
                        rs.getInt("book_id"),
                        rs.getString("title"),
                        rs.getInt("author_id"),
                        rs.getString("author_name"),
                        rs.getInt("publish_year")
                ));
            }
        }
        return books;
    }

    public void addBook(String title, int authorId, int publishYear) throws SQLException {
        String sql = "INSERT INTO Books (title, author_id, publish_year) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setInt(2, authorId);
            pstmt.setInt(3, publishYear);
            pstmt.executeUpdate();
        }
    }

    public void updateBook(int id, String title, int authorId, int publishYear) throws SQLException {
        String sql = "UPDATE Books SET title = ?, author_id = ?, publish_year = ? WHERE book_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setInt(2, authorId);
            pstmt.setInt(3, publishYear);
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
        }
    }

    public void deleteBook(int id) throws SQLException {
        String sql = "DELETE FROM Books WHERE book_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    public List<Reader> getAllReaders() throws SQLException {
        List<Reader> readers = new ArrayList<>();
        String sql = "SELECT * FROM Readers";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                readers.add(new Reader(
                        rs.getInt("reader_id"),
                        rs.getString("name"),
                        rs.getString("email")
                ));
            }
        }
        return readers;
    }

    public void addReader(String name, String email) throws SQLException {
        String sql = "INSERT INTO Readers (name, email) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.executeUpdate();
        }
    }

    public void updateReader(int id, String name, String email) throws SQLException {
        String sql = "UPDATE Readers SET name = ?, email = ? WHERE reader_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
        }
    }

    public void deleteReader(int id) throws SQLException {
        String sql = "DELETE FROM Readers WHERE reader_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    public List<Borrowing> getAllBorrowings() throws SQLException {
        List<Borrowing> borrowings = new ArrayList<>();
        String sql = "SELECT br.borrowing_id, b.title, r.name as reader_name, br.date_borrowed " +
                "FROM Borrowings br " +
                "JOIN Books b ON br.book_id = b.book_id " +
                "JOIN Readers r ON br.reader_id = r.reader_id " +
                "WHERE br.return_date IS NULL";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                borrowings.add(new Borrowing(
                        rs.getInt("borrowing_id"),
                        rs.getString("title"),
                        rs.getString("reader_name"),
                        rs.getDate("date_borrowed")
                ));
            }
        }
        return borrowings;
    }

    public void addBorrowing(int bookId, int readerId, Date dateBorrowed) throws SQLException {
        String sql = "INSERT INTO Borrowings (book_id, reader_id, date_borrowed, return_date) VALUES (?, ?, ?, NULL)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            pstmt.setInt(2, readerId);
            pstmt.setDate(3, dateBorrowed);
            pstmt.executeUpdate();
        }
    }

    public void markBookAsReturned(int borrowingId, Date returnDate) throws SQLException {
        String sql = "UPDATE Borrowings SET return_date = ? WHERE borrowing_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDate(1, returnDate);
            pstmt.setInt(2, borrowingId);
            pstmt.executeUpdate();
        }
    }

    public boolean isBookBorrowed(int bookId) throws SQLException {
        String query = "SELECT COUNT(*) FROM Borrowings WHERE book_id = ? AND return_date IS NULL";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public List<SearchResult> searchBooksFlexible(String authorName, String bookTitle, Integer year) throws SQLException {
        List<SearchResult> results = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT b.title, a.name as author_name, b.publish_year, " +
                        "(SELECT COUNT(*) FROM Borrowings br WHERE br.book_id = b.book_id) as borrow_count " +
                        "FROM Books b " +
                        "JOIN Authors a ON b.author_id = a.author_id WHERE 1=1"
        );

        List<Object> params = new ArrayList<>();

        if (authorName != null && !authorName.isEmpty()) {
            sql.append(" AND LOWER(a.name) LIKE ?");
            params.add("%" + authorName.toLowerCase() + "%");
        }
        if (bookTitle != null && !bookTitle.isEmpty()) {
            sql.append(" AND LOWER(b.title) LIKE ?");
            params.add("%" + bookTitle.toLowerCase() + "%");
        }
        if (year != null) {
            sql.append(" AND b.publish_year = ?");
            params.add(year);
        }

        try (PreparedStatement pstmt = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    results.add(new SearchResult(
                            rs.getString("title"),
                            rs.getString("author_name"),
                            rs.getInt("publish_year"),
                            rs.getInt("borrow_count")
                    ));
                }
            }
        }

        return results;
    }

    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}
