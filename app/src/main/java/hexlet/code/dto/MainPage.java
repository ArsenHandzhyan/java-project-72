package hexlet.code.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * This class is designed for extension. To safely extend this class, override the methods
 * as needed, but be aware of the potential impact on the existing functionality.
 */
@AllArgsConstructor
@Getter
public class MainPage extends BasePage {
    private String currentUser;

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
