package hexlet.code.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class Url {
    private Long id;
    @ToString.Include
    private String name;
    private LocalDateTime createdAt;

    public Url(String name, LocalDateTime createdAt) {
        this.name = name;
        this.createdAt = createdAt;
    }
}
