package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class SearchPanel extends JPanel {
    private final DatabaseManager dbManager;
    private JTextField authorField, titleField, yearField;
    private DefaultTableModel tableModel;

    public SearchPanel(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        JPanel searchInputs = new JPanel(new GridLayout(2, 4, 5, 5));
        authorField = new JTextField();
        titleField = new JTextField();
        yearField = new JTextField();

        searchInputs.add(new JLabel("Author Name:"));
        searchInputs.add(authorField);
        searchInputs.add(new JLabel("Book Title:"));
        searchInputs.add(titleField);
        searchInputs.add(new JLabel("Year:"));
        searchInputs.add(yearField);

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> performSearch());
        searchInputs.add(new JLabel());
        searchInputs.add(searchButton);

        add(searchInputs, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"Title", "Author", "Year"}, 0);
        JTable resultsTable = new JTable(tableModel);
        add(new JScrollPane(resultsTable), BorderLayout.CENTER);
    }

    private void performSearch() {
        String author = authorField.getText().trim();
        String title = titleField.getText().trim();
        String yearText = yearField.getText().trim();

        Integer year = null;
        if (!yearText.isEmpty()) {
            try {
                year = Integer.parseInt(yearText);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Year must be a number", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        try {
            List<SearchResult> results = dbManager.searchBooksFlexible(author, title, year);
            tableModel.setRowCount(0);
            for (SearchResult r : results) {
                tableModel.addRow(new Object[]{
                        r.getTitle(),
                        r.getAuthor(),
                        r.getYear(),
                        r.getBorrowCount()
                });
            }

            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No results found.", "Search", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Search failed: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
