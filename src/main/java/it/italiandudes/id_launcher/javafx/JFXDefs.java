package it.italiandudes.id_launcher.javafx;

import it.italiandudes.id_launcher.utils.Defs;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public final class JFXDefs {

    // Service Starter
    public static void startServiceTask(@NotNull final Runnable runnable) {
        new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() {
                        runnable.run();
                        return null;
                    }
                };
            }
        }.start();
    }

    // App Info
    public static final class AppInfo {
        public static final String NAME = "ItalianDudes Launcher";
        public static final Image LOGO = new Image(Defs.Resources.getAsStream(Resources.Image.Logo.LOGO_ID_LAUNCHER));
    }

    // System Info
    public static final class SystemGraphicInfo {
        public static final Rectangle2D SCREEN_RESOLUTION = Screen.getPrimary().getBounds();
        public static final double SCREEN_WIDTH = SCREEN_RESOLUTION.getWidth();
        public static final double SCREEN_HEIGHT = SCREEN_RESOLUTION.getHeight();
    }

    // Resource Locations
    public static final class Resources {

        // FXML Location
        public static final class FXML {
            private static final String FXML_DIR = Defs.Resources.PROJECT_RESOURCES_ROOT + "fxml/";
            public static final String FXML_LOADING = FXML_DIR + "SceneLoading.fxml";
            public static final String FXML_MAIN_MENU = FXML_DIR + "SceneMainMenu.fxml";
            public static final class Tabs {
                private static final String TABS_DIR = FXML_DIR + "tabs/";
                public static final String FXML_TAB_DND_VISUALIZER = TABS_DIR + "SceneTabDnD_Visualizer.fxml";
                public static final String FXML_TAB_DESPICABLE_CARDS = TABS_DIR + "SceneTabDespicableCards.fxml";
                public static final String FXML_TAB_SETTINGS = TABS_DIR + "SceneTabSettings.fxml";
            }
        }

        // GIF Location
        public static final class GIF {
            private static final String GIF_DIR = Defs.Resources.PROJECT_RESOURCES_ROOT + "gif/";
            public static final String GIF_LOADING = GIF_DIR+"loading.gif";
        }

        // CSS Location
        public static final class CSS {
            private static final String CSS_DIR = Defs.Resources.PROJECT_RESOURCES_ROOT + "css/";
            public static final String CSS_LIGHT_THEME = CSS_DIR + "light_theme.css";
            public static final String CSS_DARK_THEME = CSS_DIR + "dark_theme.css";
        }

        // Image Location
        public static final class Image {
            private static final String IMAGE_DIR = Defs.Resources.PROJECT_RESOURCES_ROOT + "image/";
            public static final class Logo {
                private static final String LOGO_DIR = IMAGE_DIR + "logo/";
                public static final String LOGO_ID_LAUNCHER = LOGO_DIR + "app-logo.png";
                public static final String LOGO_DND_VISUALIZER = LOGO_DIR + "dnd_visualizer.png";
            }
            public static final String IMAGE_FILE_EXPLORER = IMAGE_DIR+"file-explorer.png";
        }
    }
}
