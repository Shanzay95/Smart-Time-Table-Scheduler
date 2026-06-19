package Project;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class EditSchedule {
    JFrame frame;
    JTable table;
    DefaultTableModel model;
    JComboBox<String> Teacher, Room, Day, Time;
    JTextField txtCourse;
    JPanel panel;

    public EditSchedule() {
        frame = new JFrame("Class Schedule");
        frame.setLayout(new BorderLayout());
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);


        // Table
        model = new DefaultTableModel(new String[]{"ID", "Day", "Teacher", "Room", "Time", "Course"}, 0);
        table = new JTable(model);
        table.setForeground(Color.WHITE);
        table.setBackground(new Color(128, 100, 216));

        table.setSelectionBackground(new Color(216, 0, 216));
        table.getTableHeader().setBackground(new Color(128, 0, 170));
        table.getTableHeader().setForeground(Color.WHITE);
        frame.add(new JScrollPane(table), BorderLayout.CENTER);



        // Form panel
        panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.setBackground(Color.BLACK);

        Teacher = new JComboBox<>();
        Room = new JComboBox<>();
        Day = new JComboBox<>(new String[]{"Monday", "Tuesday", "Wednesday", "Thursday",});
        Time = new JComboBox<>(new String[]{"8:00-10:00", "10:00-12:00", "12:00-2:00", "2:00-4:00"});
        txtCourse = new JTextField();
        txtCourse.setEditable(false);


        Teacher.setBackground(new Color(128, 100, 216));
        Room.setBackground(new Color(128, 100, 216));
        Day.setBackground(new Color(128, 100, 216));
        Time.setBackground(new Color(128, 100, 216));
        txtCourse.setBackground(new Color(128, 100, 216));

        // Add and style labels and inputs
        addLabeledField("Teacher:", Teacher);
        addLabeledField("Course:", txtCourse);
        addLabeledField("Day:", Day);
        addLabeledField("Time:", Time);
        addLabeledField("Room:", Room);

        // Buttons
        Color purple = new Color(128, 0, 170);
        Color white = Color.WHITE;
        JButton add = new JButton("Add"), update = new JButton("Update"), delete = new JButton("Delete"), back = new JButton("Back");
        for (JButton b : new JButton[]{add, update, delete, back}) {
            b.setBackground(purple);
            b.setForeground(white);
        }

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.BLACK);
        buttonPanel.add(add);
        buttonPanel.add(update);
        buttonPanel.add(delete);
        buttonPanel.add(back);
        buttonPanel.setForeground(Color.WHITE);
        frame.add(panel, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        // Listeners
        Teacher.addActionListener(e -> loadCourse());
        add.addActionListener(e -> save(false));
        update.addActionListener(e -> save(true));
        delete.addActionListener(e -> delete());
        back.addActionListener(e -> {
            frame.dispose();
            new HomePage();
        });

        loadTeachers();
        loadRooms();
        loadSchedule();


    }

    void addLabeledField(String labelText, JComponent comp) {
        JLabel lbl = new JLabel(labelText);
        lbl.setForeground(Color.WHITE);
        panel.add(lbl);
        panel.add(comp);
    }

    void loadTeachers() {
        try (Connection con = DB.getConnection(); ResultSet rs = con.createStatement().executeQuery("SELECT * FROM teachers")) {
            while (rs.next())
                Teacher.addItem(rs.getInt("id") + " - " + rs.getString("name"));
        } catch (Exception e) {
            error(e);
        }
    }

    void loadRooms() {
        try (Connection con = DB.getConnection(); ResultSet rs = con.createStatement().executeQuery("SELECT * FROM rooms")) {
            while (rs.next())
                Room.addItem(rs.getInt("id") + " - " + rs.getString("name"));
        } catch (Exception e) {
            error(e);
        }
    }

    void loadCourse() {
        try (Connection con = DB.getConnection()) {
            if (Teacher.getSelectedItem() == null) return;
            int id = Integer.parseInt(Teacher.getSelectedItem().toString().split(" - ")[0]);
            PreparedStatement ps = con.prepareStatement("SELECT course FROM teachers WHERE id=?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) txtCourse.setText(rs.getString(1));
        } catch (Exception e) {
            error(e);
        }
    }

    void loadSchedule() {
        model.setRowCount(0);
        try (Connection con = DB.getConnection(); ResultSet rs = con.createStatement().executeQuery(
                "SELECT cs.id, cs.day, t.name, r.name, cs.timeslot, cs.course " +
                        "FROM class_schedule cs JOIN teachers t ON cs.teacherID=t.id " +
                        "JOIN rooms r ON cs.roomID=r.id order by cs.id ASC")) {
            while (rs.next())
                model.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6)});
        } catch (Exception e) {
            error(e);
        }
    }

    void save(boolean update) {
        int row = table.getSelectedRow();
        int id = update && row >= 0 ? (int) model.getValueAt(row, 0) : -1;
        try (Connection con = DB.getConnection()) {
            int tid = Integer.parseInt(Teacher.getSelectedItem().toString().split(" - ")[0]);
            int rid = Integer.parseInt(Room.getSelectedItem().toString().split(" - ")[0]);
            String day = Day.getSelectedItem().toString(), time = Time.getSelectedItem().toString(), course = txtCourse.getText();

            PreparedStatement check = con.prepareStatement("SELECT COUNT(*) FROM class_schedule WHERE day=? AND timeslot=? AND (teacherID=? OR roomID=?)" + (update ? " AND id<>?" : ""));
            check.setString(1, day);
            check.setString(2, time);
            check.setInt(3, tid);
            check.setInt(4, rid);
            if (update) check.setInt(5, id);
            ResultSet rs = check.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(frame, "Conflict: Already scheduled");
                return;
            }

            String sql = update ? "UPDATE class_schedule SET day=?, teacherID=?, roomID=?, timeslot=?, course=? WHERE id=?" :
                    "INSERT INTO class_schedule(day, teacherID, roomID, timeslot, course) VALUES (?,?,?,?,?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, day);
            ps.setInt(2, tid);
            ps.setInt(3, rid);
            ps.setString(4, time);
            ps.setString(5, course);
            if (update) ps.setInt(6, id);
            ps.executeUpdate();
            loadSchedule();
        } catch (Exception e) {
            error(e);
        }
    }

    void delete() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int id = (int) model.getValueAt(row, 0);
        try (Connection con = DB.getConnection()) {
            PreparedStatement ps = con.prepareStatement("DELETE FROM class_schedule WHERE id=?");
            ps.setInt(1, id);
            ps.executeUpdate();
            loadSchedule();
        } catch (Exception e) {
            error(e);
        }
    }

    void error(Exception e) {
        JOptionPane.showMessageDialog(frame, e.getMessage());
    }

}

