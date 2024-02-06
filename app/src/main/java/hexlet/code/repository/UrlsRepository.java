package hexlet.code.repository;

import hexlet.code.model.Url;
import lombok.Getter;

import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static hexlet.code.repository.BaseRepository.dataSource;

public class UrlsRepository {
    @Getter
    private static final List<Url> URLS = new ArrayList<>();

    public static void save(Url url) throws SQLException {
        String sql = "INSERT INTO urls (name, createdAt) VALUES (?, ?)";
        try (var conn = dataSource.getConnection();
             var preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, url.getName());
            preparedStatement.setObject(2, url.getCreatedAt());
            preparedStatement.executeUpdate();
            var generatedKeys = preparedStatement.getGeneratedKeys();
            // Устанавливаем ID в сохраненную сущность
            if (generatedKeys.next()) {
                url.setId(generatedKeys.getLong(1));
            } else {
                throw new SQLException("DB have not returned an id after saving an entity");
            }
        }
    }

    public static Optional<Url> find(Long id) throws SQLException {
        var sql = "SELECT * FROM urls WHERE id = ?";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            var resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                var name = resultSet.getString("name");
                var createdAt = resultSet.getObject("createdAt");
                var url = new Url(name, (LocalDateTime) createdAt);
                url.setId(id);
                return Optional.of(url);
            }
            return Optional.empty();
        }
    }

    public static List<Url> getEntities() throws SQLException {
        var sql = "SELECT * FROM urls";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            var resultSet = stmt.executeQuery();
            var result = new ArrayList<Url>();
            while (resultSet.next()) {
                var id = resultSet.getLong("id");
                var name = resultSet.getString("name");
                var createdAt = resultSet.getObject("createdAt");
                var url = new Url(name, (LocalDateTime) createdAt);
                url.setId(id);
                result.add(url);
            }
            return result;
        }
    }

    public static void delete(Long id) throws SQLException {
        String sql = "DELETE FROM urls WHERE id = ?";
        try (var conn = dataSource.getConnection();
             var preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Deleting url failed, no rows affected.");
            }
        }
    }
    public static void update(Long id, String name, String createdAt) throws SQLException {
        var sql = "UPDATE urls SET name = ?, createdAt = ? WHERE id = ?";
        try (var conn = dataSource.getConnection();
             var preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, name);
            preparedStatement.setObject(2, createdAt);
            preparedStatement.setLong(3, id);
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Url with id = " + id + " not found for update");
            }
        }
    }

    public static List<Url> searchEntities(String name, String createdAt) throws SQLException {
        var sql = "SELECT * FROM urls WHERE LOWER(name) LIKE LOWER(?) AND LOWER(createdAt) LIKE LOWER(?)";
        try (var conn = dataSource.getConnection();
             var preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, "%" + name + "%");
            preparedStatement.setString(2, "%" + createdAt + "%");
            var resultSet = preparedStatement.executeQuery();
            var result = new ArrayList<Url>();
            while (resultSet.next()) {
                var id = resultSet.getLong("id");
                var urlName = resultSet.getString("name");
                var urlCreatedAt = resultSet.getObject("createdAt");
                var url = new Url(urlName, (LocalDateTime) urlCreatedAt);
                url.setId(id);
                result.add(url);
            }
            return result;
        }
    }
}
