package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class BorrowingsPanel extends JPanel {
    private final DatabaseManager dbManager;
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JComboBox<Reader> readerComboBox;
    private final JComboBox<Book> bookComboBox;
    private final JButton addButton;
    private final JButton deleteButton;

    public BorrowingsPanel(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new Object[]{"ID", "Reader", "Book", "Date Borrowed"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);

        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        loadBorrowings();

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridLayout(2, 1));

        JPanel topRow = new JPanel(new GridLayout(1, 4));
        readerComboBox = new JComboBox<>();
        bookComboBox = new JComboBox<>();
        loadReaders();
        loadAvailableBooks();

        topRow.add(new JLabel("Reader:"));
        topRow.add(readerComboBox);
        topRow.add(new JLabel("Book:"));
        topRow.add(bookComboBox);

        JPanel bottomRow = new JPanel(new GridLayout(1, 2));
        addButton = new JButton("Add Borrowing");
        deleteButton = new JButton("Delete Borrowing");

        addButton.setPreferredSize(new Dimension(150, 40));
        deleteButton.setPreferredSize(new Dimension(150, 40));

        addButton.addActionListener(e -> {
            Book selectedBook = (Book) bookComboBox.getSelectedItem();
            Reader selectedReader = (Reader) readerComboBox.getSelectedItem();
            if (selectedBook != null && selectedReader != null) {
                try {
                    dbManager.addBorrowing(selectedBook.getBookId(), selectedReader.getId(), new Date(System.currentTimeMillis()));
                    loadBorrowings();
                    loadAvailableBooks();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error adding borrowing", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int borrowingId = (int) tableModel.getValueAt(selectedRow, 0);
                try {
                    dbManager.deleteBorrowing(borrowingId);
                    loadBorrowings();
                    loadAvailableBooks();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error deleting borrowing", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        bottomRow.add(addButton);
        bottomRow.add(deleteButton);

        formPanel.add(topRow);
        formPanel.add(bottomRow);

        add(formPanel, BorderLayout.SOUTH);
    }

    private void loadBorrowings() {
        try {
            tableModel.setRowCount(0);
            List<Borrowing> borrowings = dbManager.getAllBorrowings();
            for (Borrowing b : borrowings) {
                tableModel.addRow(new Object[]{
                        b.getId(),
                        b.getReaderName(),
                        b.getBookTitle(),
                        b.getDateBorrowed()
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadAvailableBooks() {
        try {
            bookComboBox.removeAllItems();
            List<Book> books = dbManager.getAvailableBooks();
            for (Book book : books) {
                bookComboBox.addItem(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadReaders() {
        try {
            readerComboBox.removeAllItems();
            List<Reader> readers = dbManager.getAllReaders();
            for (Reader reader : readers) {
                readerComboBox.addItem(reader);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
