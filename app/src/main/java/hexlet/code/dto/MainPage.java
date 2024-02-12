package hexlet.code.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MainPage extends BasePage {
    private String currentUser;

    @Override
    public String getFlash() {
        return super.getFlash();
    }

    @Override
    public void setFlash(String flash) {
        super.setFlash(flash);
    }
}
