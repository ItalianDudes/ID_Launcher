package it.italiandudes.id_launcher.javafx.controllers;

import it.italiandudes.id_launcher.javafx.components.SceneController;
import it.italiandudes.id_launcher.javafx.controllers.tabs.ControllerSceneTabDnD_Visualizer;
import it.italiandudes.id_launcher.javafx.controllers.tabs.ControllerSceneTabSettings;
import it.italiandudes.id_launcher.javafx.scene.tabs.SceneTabDnD_Visualizer;
import it.italiandudes.id_launcher.javafx.scene.tabs.SceneTabSettings;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import org.jetbrains.annotations.NotNull;

public final class ControllerSceneMainMenu {

    // Graphic Elements
    @FXML private Tab tabDnD_Visualizer;
    @FXML private Tab tabSettings;

    // Scene Controllers
    private SceneController sceneControllerTabDnD_Visualizer;
    private SceneController sceneControllerTabSettings;

    // Initialize
    @FXML
    private void initialize() {
        sceneControllerTabDnD_Visualizer = SceneTabDnD_Visualizer.getScene(this);
        tabDnD_Visualizer.setContent(sceneControllerTabDnD_Visualizer.getParent());

        sceneControllerTabSettings = SceneTabSettings.getScene(this);
        tabSettings.setContent(sceneControllerTabSettings.getParent());
    }

    // Methods
    @NotNull
    public ControllerSceneTabDnD_Visualizer getTabDnD_Visualizer() {
        return (ControllerSceneTabDnD_Visualizer) sceneControllerTabDnD_Visualizer.getController();
    }
    @NotNull
    public ControllerSceneTabSettings getTabSettings() {
        return (ControllerSceneTabSettings) sceneControllerTabSettings.getController();
    }
}
