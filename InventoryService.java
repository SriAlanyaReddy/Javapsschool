import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class InventoryService {

    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/inventory_db";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    // SQL Queries
    private static final String INSERT_OR_UPDATE_PRODUCT =
            "INSERT INTO products (product_id, name, category) VALUES (?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE name = VALUES(name), category = VALUES(category)";

    private static final String INSERT_OR_UPDATE_INVENTORY =
            "INSERT INTO product_inventory (product_id, quantity, warehouse_location) VALUES (?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE quantity = VALUES(quantity), warehouse_location = VALUES(warehouse_location)";

    public void processProductData(String csvFilePath) {
        Connection conn = null;
        BufferedReader reader = null;
        Set<String> processedProductIds = new HashSet<>();

        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            conn.setAutoCommit(false);

            reader = new BufferedReader(new FileReader(csvFilePath));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");

                if (fields.length != 5) {
                    throw new IllegalArgumentException("Invalid data format: " + line);
                }

                String productId = fields[0].trim();
                String name = fields[1].trim();
                String category = fields[2].trim();
                int quantity;
                String warehouseLocation = fields[4].trim();

                try {
                    quantity = Integer.parseInt(fields[3].trim());
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid quantity: " + fields[3]);
                }

                // Check for duplicate product IDs
                if (processedProductIds.contains(productId)) {
                    throw new DuplicateOrderId("Duplicate product ID found: " + productId);
                }
                processedProductIds.add(productId);

                // Insert or update product
                try (PreparedStatement productStmt = conn.prepareStatement(INSERT_OR_UPDATE_PRODUCT)) {
                    productStmt.setString(1, productId);
                    productStmt.setString(2, name);
                    productStmt.setString(3, category);
                    productStmt.executeUpdate();
                }

                // Insert or update inventory
                try (PreparedStatement inventoryStmt = conn.prepareStatement(INSERT_OR_UPDATE_INVENTORY)) {
                    inventoryStmt.setString(1, productId);
                    inventoryStmt.setInt(2, quantity);
                    inventoryStmt.setString(3, warehouseLocation);
                    inventoryStmt.executeUpdate();
                }
            }

            conn.commit();

        } catch (IOException e) {
            logError("Error reading CSV file: " + e.getMessage());
            rollbackTransaction(conn);
        } catch (SQLException e) {
            logError("Database error: " + e.getMessage());
            rollbackTransaction(conn);
        } catch (IllegalArgumentException | DuplicateOrderId e) {
            logError("Data validation error: " + e.getMessage());
            rollbackTransaction(conn);
        } finally {
            closeResources(reader, conn);
        }
    }

    private void rollbackTransaction(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                logError("Error rolling back transaction: " + e.getMessage());
            }
        }
    }

    private void closeResources(BufferedReader reader, Connection conn) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                logError("Error closing file reader: " + e.getMessage());
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                logError("Error closing database connection: " + e.getMessage());
            }
        }
    }

    private void logError(String message) {
        // Implement your logging logic here (e.g., log to a file or system logger)
        System.err.println(message);
    }

    // Custom exception for duplicate product IDs
    public static class DuplicateOrderId extends RuntimeException {
        public DuplicateOrderId(String message) {
            super(message);
        }
    }
}
