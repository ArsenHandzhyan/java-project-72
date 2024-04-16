package hexlet.code.model;

import hexlet.code.repository.UrlCheckRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "url", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UrlCheck> urlChecks = new ArrayList<>();

    public Url(String name, LocalDateTime createdAt) {
        this.name = name;
        this.createdAt = createdAt;
    }

    /**
     * Retrieves the HTTP status code of the last check.
     *
     * @return the HTTP status code as a string, or an empty string if the URL check is not found
     */
    public String getStatusCode() {
        UrlCheck lastCheck = UrlCheckRepository.findLastByUrlId(this.id);
        return lastCheck != null ? String.valueOf(lastCheck.getStatusCode()) : "";
    }

    /**
     * Retrieves the date and time of the last check.
     *
     * @return the date and time of the last check in the format "dd/MM/yyyy HH:mm",
     * or an empty string if the URL check is not found
     */
    public LocalDateTime getLastCheckDate() {
        UrlCheck lastCheck = UrlCheckRepository.findLastByUrlId(this.id);
        return lastCheck != null ? lastCheck.getCreatedAt() : null;
    }
}
