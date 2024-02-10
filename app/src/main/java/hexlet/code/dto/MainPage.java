package hexlet.code.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.naming.Context;

@AllArgsConstructor
@Getter
public class MainPage extends BasePage {
    private String currentUrl;
    private Context ctx;

    @Override
    public String getFlash() {
        return super.getFlash();
    }

    @Override
    public void setFlash(String flash) {
        super.setFlash(flash);
    }
}
