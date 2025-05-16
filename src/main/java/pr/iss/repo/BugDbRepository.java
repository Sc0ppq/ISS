package pr.iss.repo;

import pr.iss.domain.Bug;
import pr.iss.domain.Tester;
import pr.iss.repo.BugRepository;
import pr.iss.repo.JdbcUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BugDbRepository implements BugRepository {
    private final JdbcUtils dbUtils;

    public BugDbRepository(JdbcUtils dbUtils) {
        this.dbUtils = dbUtils;
    }

    @Override
    public void save(Bug bug) {
        String sql = "INSERT INTO bug(name, description, reported_by) VALUES (?, ?, ?)";
        try (Connection conn = dbUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, bug.getName());
            ps.setString(2, bug.getDescription());
            ps.setInt(3, bug.getReportedBy().getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving bug: " + e.getMessage());
        }
    }

    @Override
    public void delete(Bug bug) {
        String sql = "DELETE FROM bug WHERE id = ?";
        try (Connection conn = dbUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bug.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting bug: " + e.getMessage());
        }
    }

    @Override
    public List<Bug> findAll() {
        List<Bug> bugs = new ArrayList<>();
        String sql = """
        SELECT b.id, b.name, b.description,
               u.id AS tester_id, u.username AS tester_username
        FROM bug b
        JOIN tester t ON b.reported_by = t.id
        JOIN users u ON u.id = t.id
    """;

        try (Connection conn = dbUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Bug bug = new Bug();
                bug.setId(rs.getInt("id"));
                bug.setName(rs.getString("name"));
                bug.setDescription(rs.getString("description"));

                Tester tester = new Tester();
                tester.setId(rs.getInt("tester_id"));
                tester.setUsername(rs.getString("tester_username"));
                bug.setReportedBy(tester);

                bugs.add(bug);
            }
        } catch (SQLException e) {
            System.err.println("Error loading bugs: " + e.getMessage());
        }
        return bugs;
    }


    @Override
    public List<Bug> findByName(String name) {
        List<Bug> bugs = new ArrayList<>();
        String sql = """
        SELECT b.id, b.name, b.description,
               u.id AS tester_id, u.username AS tester_username
        FROM bug b
        JOIN tester t ON b.reported_by = t.id
        JOIN users u ON u.id = t.id
        WHERE b.name LIKE ?
    """;

        try (Connection conn = dbUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + name + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Bug bug = new Bug();
                    bug.setId(rs.getInt("id"));
                    bug.setName(rs.getString("name"));
                    bug.setDescription(rs.getString("description"));

                    Tester tester = new Tester();
                    tester.setId(rs.getInt("tester_id"));
                    tester.setUsername(rs.getString("tester_username"));
                    bug.setReportedBy(tester);

                    bugs.add(bug);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding bugs by name: " + e.getMessage());
        }
        return bugs;
    }

}

