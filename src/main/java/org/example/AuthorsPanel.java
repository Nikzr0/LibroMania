package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class AuthorsPanel extends JPanel {
    private DatabaseManager dbManager;
    private JTable authorsTable;
    private DefaultTableModel tableModel;

    public AuthorsPanel(DatabaseManager dbManager) throws SQLException {
        this.dbManager = dbManager;
        initComponents();
        loadAuthors();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Country"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        authorsTable = new JTable(tableModel);

        authorsTable.getColumnModel().getColumn(0).setMinWidth(0);
        authorsTable.getColumnModel().getColumn(0).setMaxWidth(0);
        authorsTable.getColumnModel().getColumn(0).setWidth(0);

        add(new JScrollPane(authorsTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> showAddAuthorDialog());

        JButton editButton = new JButton("Edit");
        editButton.addActionListener(e -> showEditAuthorDialog());

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteSelectedAuthor());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> {
            try {
                loadAuthors();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error loading authors: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        // buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadAuthors() throws SQLException {
        tableModel.setRowCount(0);
        List<Author> authors = dbManager.getAllAuthors();
        for (Author author : authors) {
            tableModel.addRow(new Object[]{author.getId(), author.getName(), author.getCountry()});
        }
    }

    private void showAddAuthorDialog() {
        JTextField nameField = new JTextField();
        JTextField countryField = new JTextField();

        Object[] message = {
                "Name:", nameField,
                "Country:", countryField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add Author",
                JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name cannot be empty",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                dbManager.addAuthor(name, countryField.getText().trim());  // Този метод може да хвърли SQLException
                loadAuthors();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error adding author: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showEditAuthorDialog() {
        int selectedRow = authorsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an author to edit",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int authorId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentName = (String) tableModel.getValueAt(selectedRow, 1);
        String currentCountry = (String) tableModel.getValueAt(selectedRow, 2);

        JTextField nameField = new JTextField(currentName);
        JTextField countryField = new JTextField(currentCountry);

        Object[] message = {
                "Name:", nameField,
                "Country:", countryField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Edit Author",
                JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String newName = nameField.getText().trim();
            if (newName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name cannot be empty",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                dbManager.updateAuthor(authorId, newName, countryField.getText().trim());  // Този метод може да хвърли SQLException
                loadAuthors();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error updating author: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedAuthor() {
        int selectedRow = authorsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an author to delete",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this author?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int authorId = (int) tableModel.getValueAt(selectedRow, 0);
            try {
                dbManager.deleteAuthor(authorId);
                loadAuthors();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting author: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    public void closeDatabaseConnection() {
        try {
            dbManager.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error closing database connection: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}