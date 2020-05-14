package Services;

/**
 * @Author Robert Skaar
 * @Project newProject  -  https://github.com/robskaar
 * @Date 11-05-2020
 **/

public enum ThemeControl {
    // Stylesheet paths
    DARK("Foundation/Resources/CSS/DarkMode.css"),
    DEFAULT("Foundation/Resources/CSS/DefaultStyle.css"),
    ; // semicolon needed when fields / methods follow
    private String theme;
    public static ThemeControl currentTheme;

    ThemeControl(String theme) {
        this.theme = theme;
    }

    public String getTheme() {
        return theme;
    }
}
