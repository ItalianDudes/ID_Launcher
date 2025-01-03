package it.italiandudes.id_launcher.javafx.controllers.tabs;

import it.italiandudes.id_launcher.javafx.JFXDefs;
import it.italiandudes.id_launcher.javafx.controllers.ControllerSceneMainMenu;
import it.italiandudes.id_launcher.release.ReleaseType;
import it.italiandudes.idl.common.Logger;
import it.italiandudes.id_launcher.javafx.Client;
import it.italiandudes.id_launcher.javafx.alerts.ErrorAlert;
import it.italiandudes.id_launcher.javafx.alerts.InformationAlert;
import it.italiandudes.id_launcher.javafx.utils.Settings;
import it.italiandudes.id_launcher.javafx.utils.ThemeHandler;
import it.italiandudes.id_launcher.utils.Defs;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.IOException;

public final class ControllerSceneTabSettings {

    // Main Link
    private ControllerSceneMainMenu mainMenuController = null;
    private volatile boolean configurationComplete;
    public void setMainMenuController(@NotNull final ControllerSceneMainMenu controller) {
        mainMenuController = controller;
    }
    public void configurationComplete() {
        configurationComplete = true;
    }

    // Attributes
    private static final Image DARK_MODE = new Image(Defs.Resources.getAsStream(Defs.Resources.Image.IMAGE_DARK_MODE));
    private static final Image LIGHT_MODE = new Image(Defs.Resources.getAsStream(Defs.Resources.Image.IMAGE_LIGHT_MODE));
    private static final Image TICK = new Image(Defs.Resources.getAsStream(Defs.Resources.Image.IMAGE_TICK));
    private static final Image CROSS = new Image(Defs.Resources.getAsStream(Defs.Resources.Image.IMAGE_CROSS));

    // Graphic Elements
    @FXML private ImageView imageViewEnableDarkMode;
    @FXML private ToggleButton toggleButtonEnableDarkMode;
    @FXML private ComboBox<ReleaseType> comboBoxReleaseType;

    // Initialize
    @FXML
    private void initialize() {
        JFXDefs.startServiceTask(() -> {
            //noinspection StatementWithEmptyBody
            while (!configurationComplete);
            Platform.runLater(() -> {
                toggleButtonEnableDarkMode.setSelected(Settings.getSettings().getBoolean(Defs.SettingsKeys.ENABLE_DARK_MODE));
                if (toggleButtonEnableDarkMode.isSelected()) imageViewEnableDarkMode.setImage(DARK_MODE);
                else imageViewEnableDarkMode.setImage(LIGHT_MODE);
                comboBoxReleaseType.getItems().addAll(ReleaseType.values());
                comboBoxReleaseType.getSelectionModel().select(Settings.getSettings().getInt(Defs.SettingsKeys.RELEASE_CHANNEL));
            });
        });
    }

    // EDT
    @FXML
    private void toggleEnableDarkMode() {
        if (toggleButtonEnableDarkMode.isSelected()) {
            imageViewEnableDarkMode.setImage(DARK_MODE);
            ThemeHandler.loadDarkTheme(Client.getStage().getScene());
        }
        else {
            imageViewEnableDarkMode.setImage(LIGHT_MODE);
            ThemeHandler.loadLightTheme(Client.getStage().getScene());
        }
    }
    @FXML
    private void save() {
        JFXDefs.startServiceTask(() -> {
            try {
                Settings.getSettings().put(Defs.SettingsKeys.ENABLE_DARK_MODE, toggleButtonEnableDarkMode.isSelected());
            } catch (JSONException e) {
                Logger.log(e);
            }
            ThemeHandler.setConfigTheme();
            try {
                Settings.getSettings().put(Defs.SettingsKeys.RELEASE_CHANNEL, comboBoxReleaseType.getSelectionModel().getSelectedItem().ordinal());
            } catch (JSONException e) {
                Logger.log(e);
            }
            try {
                Settings.writeJSONSettings();
                Platform.runLater(() -> new InformationAlert("SUCCESSO", "Salvataggio Impostazioni", "Impostazioni salvate e applicate con successo!"));
            } catch (IOException e) {
                Logger.log(e);
                Platform.runLater(() -> new ErrorAlert("ERRORE", "Errore di I/O", "Si e' verificato un errore durante il salvataggio delle impostazioni."));
            }
        });
    }
}
