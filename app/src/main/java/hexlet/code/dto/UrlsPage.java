package hexlet.code.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import hexlet.code.model.Url;

import java.util.List;

@AllArgsConstructor
@Getter
public class UrlsPage extends BasePage {
    private List<Url> urls;

    @Override
    public String getFlash() {
        return super.getFlash();
    }

    @Override
    public void setFlash(String flash) {
        super.setFlash(flash);
    }
}
