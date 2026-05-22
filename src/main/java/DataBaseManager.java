import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import at.favre.lib.crypto.bcrypt.BCrypt;

public class DataBaseManager {
    private static final String DB_URL = "jdbc:sqlite:Users.sqlite";

    public static void initDatabase() {
        try (Connection connection = connect(); Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
            statement.execute("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE NOT NULL, password_hash TEXT NOT NULL)");
            statement.execute("CREATE TABLE IF NOT EXISTS finance_data (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER NOT NULL, income REAL NOT NULL, expense REAL NOT NULL, goal REAL NOT NULL, days INTEGER NOT NULL, created_at TEXT NOT NULL DEFAULT (datetime('now')), FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE)");
        } catch (SQLException e) {
            System.err.println("Unable to initialize database: " + e.getMessage());
        }
    }

    private static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static boolean createUser(String username, String password) {
        String passwordHash = hashPassword(password);
        String insertSql = "INSERT INTO users(username, password_hash) VALUES (?, ?)";

        try (Connection connection = connect(); PreparedStatement statement = connection.prepareStatement(insertSql)) {
            statement.setString(1, username.trim());
            statement.setString(2, passwordHash);
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("UNIQUE")) {
                return false;
            }
            System.err.println("Error creating user: " + e.getMessage());
            return false;
        }
    }

    public static Optional<UserSession> authenticate(String username, String password) {
        String query = "SELECT id, password_hash FROM users WHERE username = ?";

        try (Connection connection = connect(); PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username.trim());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String storedHash = resultSet.getString("password_hash");
                    BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), storedHash);
                    if (result.verified) {
                        int userId = resultSet.getInt("id");
                        return Optional.of(new UserSession(userId, username.trim()));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Authentication error: " + e.getMessage());
        }

        return Optional.empty();
    }

    public static boolean saveFinanceData(int userId, double income, double expense, double goal, int days) {
        String insert = "INSERT INTO finance_data(user_id, income, expense, goal, days) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = connect(); PreparedStatement statement = connection.prepareStatement(insert)) {
            statement.setInt(1, userId);
            statement.setDouble(2, income);
            statement.setDouble(3, expense);
            statement.setDouble(4, goal);
            statement.setInt(5, days);
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Unable to save finance data: " + e.getMessage());
            return false;
        }
    }

    public static Optional<FinanceRecord> getLastFinanceRecord(int userId) {
        String query = "SELECT income, expense, goal, days, created_at FROM finance_data WHERE user_id = ? ORDER BY created_at DESC LIMIT 1";

        try (Connection connection = connect(); PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(new FinanceRecord(
                            resultSet.getDouble("income"),
                            resultSet.getDouble("expense"),
                            resultSet.getDouble("goal"),
                            resultSet.getInt("days"),
                            resultSet.getString("created_at")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Unable to load user finance record: " + e.getMessage());
        }

        return Optional.empty();
    }

    private static String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    public static class FinanceRecord {
        private final double income;
        private final double expense;
        private final double goal;
        private final int days;
        private final String createdAt;

        public FinanceRecord(double income, double expense, double goal, int days, String createdAt) {
            this.income = income;
            this.expense = expense;
            this.goal = goal;
            this.days = days;
            this.createdAt = createdAt;
        }

        public double getIncome() {
            return income;
        }

        public double getExpense() {
            return expense;
        }

        public double getGoal() {
            return goal;
        }

        public int getDays() {
            return days;
        }

        public String getCreatedAt() {
            return createdAt;
        }
    }
}
