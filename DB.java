package Project;
import java.sql.*;


public class DB {
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/timetable", "root", "8090");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

