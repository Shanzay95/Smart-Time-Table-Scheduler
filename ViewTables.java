package Project;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ViewTables extends JFrame {

    public ViewTables(String title, String query) {
        setTitle("View " + title);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Set background color for content pane
        getContentPane().setBackground(Color.BLACK);

        DefaultTableModel model = new DefaultTableModel();
        JTable table = new JTable(model);
        table.setBackground(Color.BLACK);
        table.setForeground(Color.WHITE);
        table.setGridColor(new Color(128, 0, 128));
        table.setSelectionBackground(new Color(64, 0, 90));
        table.setSelectionForeground(Color.WHITE);
        table.getTableHeader().setBackground(new Color(80, 0, 100));
        table.getTableHeader().setForeground(Color.WHITE);

        // Load data from DB including column names dynamically
        try (Connection con = DB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            // Set columns
            String[] columnNames = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                columnNames[i - 1] = meta.getColumnLabel(i);
            }
            model.setColumnIdentifiers(columnNames);

            // Add rows
            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getObject(i);
                }
                model.addRow(row);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading data:\n" + e.getMessage());
            e.printStackTrace();
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.BLACK); // Table viewport
        add(scrollPane, BorderLayout.CENTER);

        JButton backBtn = new JButton("Back to Home");
        backBtn.setBackground(new Color(128, 0, 128)); // Purple
        backBtn.setForeground(Color.WHITE);

        JPanel bottom = new JPanel();
        bottom.setBackground(Color.BLACK);
        bottom.add(backBtn);

        backBtn.addActionListener(e -> {
            new HomePage().setVisible(true);
            dispose();
        });

        add(bottom, BorderLayout.SOUTH);
    }
}

