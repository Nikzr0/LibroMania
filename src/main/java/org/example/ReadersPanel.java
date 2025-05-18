package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.awt.event.HierarchyEvent;

public class ReadersPanel extends JPanel {
    private DatabaseManager dbManager;
    private JTable readersTable;
    private DefaultTableModel tableModel;

    public ReadersPanel(DatabaseManager dbManager) throws SQLException {
        this.dbManager = dbManager;
        initComponents();
        loadReaders();

        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0) {
                if (!isDisplayable()) {  // Проверяваме дали панелът е затворен
                    try {
                        dbManager.close();
                        System.out.println("Database connection closed from ReadersPanel.");
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new Object[]{"Name", "Email"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        readersTable = new JTable(tableModel);
        add(new JScrollPane(readersTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> showAddReaderDialog());

        JButton editButton = new JButton("Edit");
        editButton.addActionListener(e -> showEditReaderDialog());

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteSelectedReader());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshTable());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        //buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadReaders() throws SQLException {
        tableModel.setRowCount(0);
        List<Reader> readers = dbManager.getAllReaders();
        for (Reader reader : readers) {
            tableModel.addRow(new Object[]{reader.getName(), reader.getEmail()});
        }
    }

    private void refreshTable() {
        try {
            loadReaders();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error refreshing reader list: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddReaderDialog() {
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();

        Object[] message = {
                "Name:", nameField,
                "Email:", emailField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add Reader",
                JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try {
                dbManager.addReader(nameField.getText(), emailField.getText());
                loadReaders();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error adding reader: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showEditReaderDialog() {
        int selectedRow = readersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a reader to edit",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String currentName = (String) tableModel.getValueAt(selectedRow, 0);
        String currentEmail = (String) tableModel.getValueAt(selectedRow, 1);

        JTextField nameField = new JTextField(currentName);
        JTextField emailField = new JTextField(currentEmail);

        Object[] message = {
                "Name:", nameField,
                "Email:", emailField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Edit Reader",
                JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try {
                List<Reader> readers = dbManager.getAllReaders();
                int readerId = readers.get(selectedRow).getId();
                dbManager.updateReader(readerId, nameField.getText(), emailField.getText());
                loadReaders();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error updating reader: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedReader() {
        int selectedRow = readersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a reader to delete",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this reader?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                List<Reader> readers = dbManager.getAllReaders();
                int readerId = readers.get(selectedRow).getId();
                dbManager.deleteReader(readerId);
                loadReaders();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting reader: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
