package it.italiandudes.id_launcher.utils;

import it.italiandudes.id_launcher.ID_Launcher;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

public final class Defs {

    // App File Name
    public static final String APP_FILE_NAME = "ID_Launcher";

    // Jar App Position
    public static final String JAR_POSITION;
    static {
        try {
            JAR_POSITION = new File(ID_Launcher.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsolutePath();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    // JSON Settings
    public static final class SettingsKeys {
        public static final String ENABLE_DARK_MODE = "enableDarkMode";
        public static final String RELEASE_CHANNEL = "releaseChannel";
        public static final String LAUNCHER_BEHAVIOUR = "launcherBehaviour";
    }

    // Resources Location
    public static final class Resources {

        // Project Resources Root
        public static final String PROJECT_RESOURCES_ROOT = "/it/italiandudes/id_launcher/resources/";

        //Resource Getters
        public static URL get(@NotNull final String resourceConst) {
            return Objects.requireNonNull(ID_Launcher.class.getResource(resourceConst));
        }
        public static InputStream getAsStream(@NotNull final String resourceConst) {
            return Objects.requireNonNull(ID_Launcher.class.getResourceAsStream(resourceConst));
        }

        // JSON
        public static final class JSON {
            public static final String JSON_LAUNCHER_SETTINGS = "launcher_settings.json";
            public static final String DEFAULT_JSON_LAUNCHER_SETTINGS = PROJECT_RESOURCES_ROOT + "json/" + JSON_LAUNCHER_SETTINGS;
        }

        // Images
        public static final class Image {
            private static final String IMAGE_DIR = PROJECT_RESOURCES_ROOT + "image/";
            public static final String IMAGE_DARK_MODE = IMAGE_DIR + "dark_mode.png";
            public static final String IMAGE_LIGHT_MODE = IMAGE_DIR + "light_mode.png";
            public static final String IMAGE_TICK = IMAGE_DIR + "tick.png";
            public static final String IMAGE_CROSS = IMAGE_DIR + "cross.png";
        }
    }
}
