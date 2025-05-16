package pr.iss.repo;

import pr.iss.domain.User;
import pr.iss.domain.Tester;
import pr.iss.domain.Programmer;
import pr.iss.repo.UserRepository;
import pr.iss.repo.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDbRepository implements UserRepository {
    private final JdbcUtils dbUtils;

    public UserDbRepository(JdbcUtils dbUtils) {
        this.dbUtils = dbUtils;
    }

    @Override
    public User findByUsernameAndPassword(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = dbUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");

                    if (isTester(id, conn)) {
                        Tester tester = new Tester();
                        tester.setId(id);
                        tester.setUsername(username);
                        tester.setPassword(password);
                        return tester;
                    }

                    if (isProgrammer(id, conn)) {
                        Programmer programmer = new Programmer();
                        programmer.setId(id);
                        programmer.setUsername(username);
                        programmer.setPassword(password);
                        return programmer;
                    }

                    // fallback, ar trebui să nu ajungă aici
                    return null;
                }
            }
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
        }
        return null;
    }

    private boolean isTester(int userId, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM tester WHERE id = ?")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean isProgrammer(int userId, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM programmer WHERE id = ?")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

}

