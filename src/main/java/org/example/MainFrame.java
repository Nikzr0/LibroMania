package org.example;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private DatabaseManager dbManager;
    private JTabbedPane tabbedPane;

    public MainFrame() {
        super("Libromania - Library Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        dbManager = new DatabaseManager();
        initUI();

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                try {
                    dbManager.close();
                    System.out.println("Database connection closed.");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void initUI() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 14));

        try {
            tabbedPane.addTab("Authors", new AuthorsPanel(dbManager));
            tabbedPane.addTab("Books", new BooksPanel(dbManager));
            tabbedPane.addTab("Readers", new ReadersPanel(dbManager));  // Този ред добавя ReadersPanel
            tabbedPane.addTab("Borrowings", new BorrowingsPanel(dbManager));
            tabbedPane.addTab("Search", new SearchPanel(dbManager));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error initializing panels: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        add(tabbedPane);

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
