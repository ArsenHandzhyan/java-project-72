package hexlet.code.dto;

import hexlet.code.model.Url;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * This class represents a page of URLs with additional information about the last check date and status code.
 * It is designed for extension. To safely extend this class, override the methods as needed, but be aware of the
 * potential impact on the existing functionality.
 */
@AllArgsConstructor
@Getter
public class UrlsPage extends BasePage {
    private List<Url> urls;

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
