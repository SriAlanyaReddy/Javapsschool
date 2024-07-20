import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

 class db1{

    private static final String DB_URL = "jdbc:mysql://localhost:3306/db";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public static void main(String[] args) {
        Connection conn = null;
        FileWriter csvWriter = null;

        try {
            // Establish database connection
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);

            // Create tables if they do not exist
            createTables(conn);

            // Query to select all student data
            String query = "SELECT * FROM Students";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            // Create a FileWriter to write to a CSV file
            csvWriter = new FileWriter("students.csv");

            // Write header
            csvWriter.append("StudentID,Name,TotalCredits\n");

            // Write data
            while (rs.next()) {
                csvWriter.append(rs.getInt("StudentID") + ",");
                csvWriter.append(rs.getString("Name") + ",");
                csvWriter.append(rs.getDouble("TotalCredits") + "\n");
            }

            System.out.println("Data exported to CSV file successfully.");

        } catch (SQLException | IOException e) {
            System.out.println("Error occurred: " + e.getMessage());
        } finally {
            // Close resources
            try {
                if (csvWriter != null) {
                    csvWriter.flush();
                    csvWriter.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (IOException | SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
    }

    private static void createTables(Connection conn) throws SQLException {
        // SQL commands to create tables
        String createStudentsTable = "CREATE TABLE IF NOT EXISTS Students (" +
                "StudentID INT PRIMARY KEY, " +
                "Name VARCHAR(100), " +
                "TotalCredits DOUBLE" +
                ")";

        String createAwardsTable = "CREATE TABLE IF NOT EXISTS Awards (" +
                "StudentID INT, " +
                "ScholarshipID INT, " +
                "AwardDate DATE, " +
                "PRIMARY KEY (StudentID, ScholarshipID, AwardDate), " +
                "FOREIGN KEY (StudentID) REFERENCES Students(StudentID)" +
                ")";

        String createAccountsTable = "CREATE TABLE IF NOT EXISTS Accounts (" +
                "StudentID INT PRIMARY KEY, " +
                "Balance DOUBLE, " +
                "FOREIGN KEY (StudentID) REFERENCES Students(StudentID)" +
                ")";

        // Execute the table creation commands
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createStudentsTable);
            stmt.execute(createAwardsTable);
            stmt.execute(createAccountsTable);
            System.out.println("Tables created successfully.");
        }
    }
}
