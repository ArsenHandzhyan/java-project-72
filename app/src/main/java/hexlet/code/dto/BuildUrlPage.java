package hexlet.code.dto;

import io.javalin.validation.ValidationError;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BuildUrlPage extends BasePage {
    public Long id;
    public String name;
    public LocalDateTime createdAt;
    public Map<String, List<ValidationError<Object>>> errors;

    @Override
    public String getFlash() {
        return super.getFlash();
    }

    @Override
    public void setFlash(String flash) {
        super.setFlash(flash);
    }
}
