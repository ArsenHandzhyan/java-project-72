package hexlet.code.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import hexlet.code.model.Url;

@AllArgsConstructor
@Getter
public class UrlPage extends BasePage {
    private Url url;

    @Override
    public String getFlash() {
        return super.getFlash();
    }

    @Override
    public void setFlash(String flash) {
        super.setFlash(flash);
    }
}
