package hexlet.code.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class UrlCheck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long urlId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Url url;

    private Integer statusCode;
    private String title;
    private String h1;
    @Column(length = 1000)
    private String description;
    private LocalDateTime createdAt;
}
