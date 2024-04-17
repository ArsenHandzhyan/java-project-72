package hexlet.code.model;

import hexlet.code.repository.UrlCheckRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Url {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private LocalDateTime createdAt;

    public Url(String name, LocalDateTime createdAt) {
        this.name = name;
        this.createdAt = createdAt;
    }

    public String getStatusCode() {
        UrlCheck lastCheck = UrlCheckRepository.findLastByUrlId(this.id);
        return lastCheck != null ? String.valueOf(lastCheck.getStatusCode()) : "";
    }

    public LocalDateTime getLastCheckDate() {
        UrlCheck lastCheck = UrlCheckRepository.findLastByUrlId(this.id);
        return lastCheck != null ? lastCheck.getCreatedAt() : null;
    }
}
