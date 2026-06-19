package Project;

import javax.swing.*;
import java.awt.*;

public class HomePage extends JFrame {

    public HomePage() {
        setTitle("Timetable Scheduler");

        setSize(600, 400);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.BLACK);

        setLayout(new BorderLayout(20, 20)); // Space around edges

        // Top label
        JLabel label = new JLabel("Timetable Scheduler ", SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        add(label, BorderLayout.NORTH);

        // Center panel for buttons
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(Color.BLACK);
        centerPanel.setLayout(new GridLayout(5, 1, 10, 10)); // 5 buttons, 10px vertical gap
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 100, 20, 100)); // side space

        // Buttons
        JButton btn1 = new JButton("View Rooms");
        JButton btn2 = new JButton("View Teachers");
        JButton btn3 = new JButton("View Schedule");
        JButton btn4 = new JButton("Edit Schedule");
        JButton btn5 = new JButton("Exit");

        JButton[] buttons = {btn1, btn2, btn3, btn4, btn5};
        for (JButton btn : buttons) {
            btn.setPreferredSize(new Dimension(150, 40));
            btn.setForeground(Color.WHITE);
            btn.setBackground(new Color(128, 0, 170));
            centerPanel.add(btn);
        }

        add(centerPanel, BorderLayout.CENTER);

        // Button actions
        btn1.addActionListener(e -> {
            new ViewTables("Rooms", "SELECT ID, Name FROM rooms").setVisible(true);
            setVisible(false);
        });

        btn2.addActionListener(e -> {
            new ViewTables("Teachers", "SELECT ID, Name, Department, Phone, Course FROM teachers").setVisible(true);
            setVisible(false);
        });

        btn3.addActionListener(e -> {
            new ViewTables("Schedule",
                    "SELECT cs.ID, cs.Day, cs.TimeSlot, t.Name as Teacher, r.Name as Room " +
                            "FROM class_schedule cs " +
                            "JOIN teachers t ON cs.TeacherID = t.ID " +
                            "JOIN rooms r ON cs.RoomID = r.ID")
                    .setVisible(true);
            setVisible(false);
        });


        btn4.addActionListener(e -> {
             new EditSchedule();
            setVisible(false);
        });

        btn5.addActionListener(e -> System.exit(0));
    }

    public static void main(String[] args) {
       new HomePage().setVisible(true);

    }
}

