package it.italiandudes.id_launcher.javafx.scene.tabs;

import it.italiandudes.id_launcher.javafx.JFXDefs;
import it.italiandudes.id_launcher.javafx.components.SceneController;
import it.italiandudes.id_launcher.javafx.controllers.ControllerSceneMainMenu;
import it.italiandudes.id_launcher.javafx.controllers.tabs.ControllerSceneTabDnD_Visualizer;
import it.italiandudes.id_launcher.utils.Defs;
import it.italiandudes.idl.common.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public final class SceneTabDnD_Visualizer {

    // Scene Generator
    @NotNull
    public static SceneController getScene(@NotNull final ControllerSceneMainMenu mainMenuController) {
        try {
            FXMLLoader loader = new FXMLLoader(Defs.Resources.get(JFXDefs.Resources.FXML.Tabs.FXML_TAB_DND_VISUALIZER));
            Parent root = loader.load();
            ControllerSceneTabDnD_Visualizer controller = loader.getController();
            controller.setMainMenuController(mainMenuController);
            controller.configurationComplete();
            return new SceneController(root, controller);
        } catch (IOException e) {
            Logger.log(e);
            System.exit(-1);
            return null;
        }
    }
}
