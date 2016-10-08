package przygoda.com.projektkoncowy_przygoda;

/**
 * Created by 4ia1 on 2016-09-28.
 */
public class Preferences {
    private static String saveLocation = "";

    public static String getSaveLocation() {
        return saveLocation;
    }

    public static String setSaveLocation(String newLocation) {
        saveLocation = newLocation;
        return saveLocation;
    }
}
