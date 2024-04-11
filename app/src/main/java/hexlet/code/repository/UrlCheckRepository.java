package hexlet.code.repository;

import hexlet.code.model.UrlCheck;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static hexlet.code.repository.BaseRepository.dataSource;

public class UrlCheckRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(UrlCheckRepository.class);

    public static void save(UrlCheck urlCheck) throws SQLException {
        String sql = "INSERT INTO url_checks (url_id, status_code, title, h1, description, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setLong(1, urlCheck.getUrl().getId());
            preparedStatement.setInt(2, urlCheck.getStatusCode());
            preparedStatement.setString(3, urlCheck.getTitle());
            preparedStatement.setString(4, urlCheck.getH1());
            preparedStatement.setString(5, urlCheck.getDescription());
            preparedStatement.setObject(6, urlCheck.getCreatedAt());
            preparedStatement.executeUpdate();
        }
    }

    public static List<UrlCheck> findByUrlId(Long urlId) throws SQLException {
        String sql = "SELECT * FROM url_checks WHERE url_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setLong(1, urlId);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<UrlCheck> urlChecks = new ArrayList<>();
            while (resultSet.next()) {
                urlChecks.add(mapUrlCheckFromResultSet(resultSet));
            }
            return urlChecks.isEmpty() ? Collections.emptyList() : urlChecks;
        }
    }

    public static long getNextIdForUrl(long urlId) throws SQLException {
        String sql = "SELECT COALESCE(MAX(id), 0) + 1 AS next_id FROM url_checks WHERE url_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setLong(1, urlId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getLong("next_id");
            }
            return 1;
        }
    }

    public static UrlCheck findLastByUrlId(Long urlId) {
        String sql = "SELECT * FROM url_checks WHERE url_id = ? ORDER BY created_at DESC LIMIT 1";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setLong(1, urlId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return mapUrlCheckFromResultSet(resultSet);
            }
            return null;
        } catch (SQLException e) {
            LOGGER.error("Ошибка при поиске последней проверки URL по ID: {}", urlId, e);
            return null;
        }
    }

    private static UrlCheck mapUrlCheckFromResultSet(ResultSet resultSet) throws SQLException {
        UrlCheck urlCheck = new UrlCheck();
        urlCheck.setId(resultSet.getLong("id"));
        urlCheck.setUrlId(resultSet.getLong("url_id"));
        urlCheck.setStatusCode(resultSet.getInt("status_code"));
        urlCheck.setTitle(resultSet.getString("title"));
        urlCheck.setH1(resultSet.getString("h1"));
        urlCheck.setDescription(resultSet.getString("description"));
        urlCheck.setCreatedAt(resultSet.getObject("created_at", LocalDateTime.class));
        return urlCheck;
    }
}
