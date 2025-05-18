package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class BooksPanel extends JPanel {
    private DatabaseManager dbManager;
    private JTable booksTable;
    private DefaultTableModel tableModel;

    public BooksPanel(DatabaseManager dbManager) throws SQLException {
        this.dbManager = dbManager;
        initComponents();
        loadBooks();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new Object[]{"Title", "Author", "Year"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        booksTable = new JTable(tableModel);
        add(new JScrollPane(booksTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> showAddBookDialog());

        JButton editButton = new JButton("Edit");
        editButton.addActionListener(e -> showEditBookDialog());

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteSelectedBook());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> {
            try {
                loadBooks();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error refreshing books: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        //buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadBooks() throws SQLException {
        tableModel.setRowCount(0);
        List<Book> books = dbManager.getAllBooks();
        for (Book book : books) {
            tableModel.addRow(new Object[]{
                    book.getTitle(),
                    book.getAuthorName(),
                    book.getPublishYear()
            });
        }
    }

    private void showAddBookDialog() {
        try {
            List<Author> authors = dbManager.getAllAuthors();
            String[] authorNames = authors.stream().map(Author::getName).toArray(String[]::new);

            JTextField titleField = new JTextField();
            JComboBox<String> authorCombo = new JComboBox<>(authorNames);
            JTextField yearField = new JTextField();

            Object[] message = {
                    "Title:", titleField,
                    "Author:", authorCombo,
                    "Publish Year:", yearField
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Add Book",
                    JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                int authorId = authors.get(authorCombo.getSelectedIndex()).getId();
                try {
                    int year = Integer.parseInt(yearField.getText());
                    if (year < 1000 || year > 9999) {
                        JOptionPane.showMessageDialog(this, "Please enter a valid year between 1000 and 9999",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    dbManager.addBook(titleField.getText(), authorId, year);
                    loadBooks();
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid year",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding book: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showEditBookDialog() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to edit",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            List<Book> books = dbManager.getAllBooks();
            Book selectedBook = books.get(selectedRow);

            List<Author> authors = dbManager.getAllAuthors();
            String[] authorNames = authors.stream().map(Author::getName).toArray(String[]::new);

            JTextField titleField = new JTextField(selectedBook.getTitle());
            JComboBox<String> authorCombo = new JComboBox<>(authorNames);
            authorCombo.setSelectedItem(selectedBook.getAuthorName());
            JTextField yearField = new JTextField(String.valueOf(selectedBook.getPublishYear()));

            Object[] message = {
                    "Title:", titleField,
                    "Author:", authorCombo,
                    "Publish Year:", yearField
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Edit Book",
                    JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                int authorId = authors.get(authorCombo.getSelectedIndex()).getId();
                try {
                    int year = Integer.parseInt(yearField.getText());
                    if (year < 1000 || year > 9999) {
                        JOptionPane.showMessageDialog(this, "Please enter a valid year between 1000 and 9999",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    dbManager.updateBook(selectedBook.getBookId(), titleField.getText(), authorId, year);
                    loadBooks();
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid year",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating book: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedBook() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to delete",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this book?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                List<Book> books = dbManager.getAllBooks();
                int bookId = books.get(selectedRow).getBookId();

                // ➕ Проверка дали книгата е в заем
                if (dbManager.isBookBorrowed(bookId)) {
                    JOptionPane.showMessageDialog(this, "Книгата е заета и не може да бъде изтрита.",
                            "Грешка", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                dbManager.deleteBook(bookId);
                loadBooks();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting book: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
