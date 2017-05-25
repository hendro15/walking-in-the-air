package hendro.com.accelero.model;

/**
 * Created by Hendro E. Prabowo on 02/04/2017.
 */

public class MenuElement {

    String title;
    int icon;

    public MenuElement(String title, int icon) {
        this.title = title;
        this.icon = icon;
    }

    public int getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }
}
