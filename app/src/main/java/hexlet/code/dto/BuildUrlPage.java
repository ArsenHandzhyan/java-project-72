package hexlet.code.dto;

import io.javalin.validation.ValidationError;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * This class is designed for extension. To safely extend this class, override the methods
 * as needed, but be aware of the potential impact on the existing functionality.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BuildUrlPage extends BasePage {
    public Long id;
    public String name;
    public LocalDateTime createdAt;
    public Map<String, List<ValidationError<Object>>> errors;

    /**
     * Gets the flash message.
     *
     * @return the flash message
     */
    @Override
    public String getFlash() {
        return super.getFlash();
    }

    /**
     * Sets the flash message.
     *
     * @param flash the flash message to set
     */
    @Override
    public void setFlash(String flash) {
        super.setFlash(flash);
    }
}
