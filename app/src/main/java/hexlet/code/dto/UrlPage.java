package hexlet.code.dto;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class UrlPage extends BasePage {
    private Url url;
    private List<UrlCheck> urlChecks;

    /**
     * Returns the flash message.
     *
     * <p>This method is intended for use in subclasses. If you override this method, ensure that
     * you call the superclass's implementation to maintain the correct behavior.</p>
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
     * <p>This method is intended for use in subclasses. If you override this method, ensure that
     * you call the superclass's implementation to maintain the correct behavior.</p>
     *
     * @param flash the flash message to set
     */
    @Override
    public void setFlash(String flash) {
        super.setFlash(flash);
    }
}
