import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;

public class ScholarshipTransactionManager {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/university_db";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public static void main(String[] args) {
        Connection conn = null;
        PreparedStatement stmtInsertAward = null;
        PreparedStatement stmtUpdateCredits = null;
        PreparedStatement stmtUpdateAccount = null;
        Savepoint savepoint = null;

        try {
            // Establish database connection
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            conn.setAutoCommit(false); // Start transaction

            // Insert a new award record
            String insertAwardSQL = "INSERT INTO Awards (StudentID, ScholarshipID, AwardDate) VALUES (?, ?, ?)";
            stmtInsertAward = conn.prepareStatement(insertAwardSQL);
            stmtInsertAward.setInt(1, 2); // StudentID
            stmtInsertAward.setInt(2, 1); // ScholarshipID
            stmtInsertAward.setDate(3, java.sql.Date.valueOf("2024-07-20")); // AwardDate
            stmtInsertAward.executeUpdate();

            // Set savepoint before updating student's total credits
            savepoint = conn.setSavepoint("BeforeUpdateCredits");

            // Deduct the scholarship amount from the student's total credits
            String updateCreditsSQL = "UPDATE Students SET TotalCredits = TotalCredits - ? WHERE StudentID = ?";
            stmtUpdateCredits = conn.prepareStatement(updateCreditsSQL);
            double scholarshipAmount = 1000.00; // Example amount
            stmtUpdateCredits.setDouble(1, scholarshipAmount);
            stmtUpdateCredits.setInt(2, 2); // StudentID
            int rowsUpdated = stmtUpdateCredits.executeUpdate();

            // Check if the total credits have become negative
            if (rowsUpdated > 0) {
                double newCredits = getTotalCredits(conn, 2); // Method to fetch total credits
                if (newCredits < 0) {
                    // Rollback to savepoint
                    conn.rollback(savepoint);
                    System.out.println("Credits became negative. Rolled back to savepoint.");
                    return;
                }
            } else {
                throw new SQLException("Failed to update total credits.");
            }

            // Add the scholarship amount to the student's account
            String updateAccountSQL = "UPDATE Accounts SET Balance = Balance + ? WHERE StudentID = ?";
            stmtUpdateAccount = conn.prepareStatement(updateAccountSQL);
            stmtUpdateAccount.setDouble(1, scholarshipAmount);
            stmtUpdateAccount.setInt(2, 2); // StudentID
            stmtUpdateAccount.executeUpdate();

            // Commit transaction if all operations succeeded
            conn.commit();
            System.out.println("Transaction committed successfully.");

        } catch (SQLException e) {
            // Rollback entire transaction on error
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("Transaction rolled back due to error: " + e.getMessage());
                } catch (SQLException rollbackEx) {
                    System.out.println("Error rolling back transaction: " + rollbackEx.getMessage());
                }
            }
        } finally {
            // Close resources
            closeResource(stmtInsertAward);
            closeResource(stmtUpdateCredits);
            closeResource(stmtUpdateAccount);
            closeResource(conn);
        }
    }

    private static double getTotalCredits(Connection conn, int studentId) throws SQLException {
        String query = "SELECT TotalCredits FROM Students WHERE StudentID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("TotalCredits");
                } else {
                    throw new SQLException("Student not found.");
                }
            }
        }
    }

    private static void closeResource(AutoCloseable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (Exception e) {
                System.out.println("Error closing resource: " + e.getMessage());
            }
        }
    }
}
