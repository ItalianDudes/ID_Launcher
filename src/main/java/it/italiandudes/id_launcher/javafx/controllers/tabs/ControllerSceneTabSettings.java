package it.italiandudes.id_launcher.javafx.controllers.tabs;

import it.italiandudes.id_launcher.enums.LauncherBehaviour;
import it.italiandudes.id_launcher.enums.ReleaseType;
import it.italiandudes.id_launcher.javafx.Client;
import it.italiandudes.id_launcher.javafx.JFXDefs;
import it.italiandudes.id_launcher.javafx.alerts.ConfirmationAlert;
import it.italiandudes.id_launcher.javafx.alerts.ErrorAlert;
import it.italiandudes.id_launcher.javafx.alerts.InformationAlert;
import it.italiandudes.id_launcher.javafx.components.SceneController;
import it.italiandudes.id_launcher.javafx.controllers.ControllerSceneMainMenu;
import it.italiandudes.id_launcher.javafx.scene.SceneLoading;
import it.italiandudes.id_launcher.javafx.utils.Settings;
import it.italiandudes.id_launcher.javafx.utils.ThemeHandler;
import it.italiandudes.id_launcher.utils.Defs;
import it.italiandudes.id_launcher.utils.Updater;
import it.italiandudes.idl.common.JarHandler;
import it.italiandudes.idl.common.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.jar.Attributes;

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

    // Graphic Elements
    @FXML private ImageView imageViewEnableDarkMode;
    @FXML private ToggleButton toggleButtonEnableDarkMode;
    @FXML private ComboBox<ReleaseType> comboBoxReleaseType;
    @FXML private ComboBox<LauncherBehaviour> comboBoxLauncherBehaviour;

    // Initialize
    @FXML
    private void initialize() {
        JFXDefs.startServiceTask(() -> {
            while (!configurationComplete) Thread.onSpinWait();
            Platform.runLater(() -> {
                toggleButtonEnableDarkMode.setSelected(Settings.getSettings().getBoolean(Defs.SettingsKeys.ENABLE_DARK_MODE));
                if (toggleButtonEnableDarkMode.isSelected()) imageViewEnableDarkMode.setImage(DARK_MODE);
                else imageViewEnableDarkMode.setImage(LIGHT_MODE);
                comboBoxReleaseType.getItems().addAll(ReleaseType.values());
                comboBoxReleaseType.getSelectionModel().select(Settings.getSettings().getInt(Defs.SettingsKeys.RELEASE_CHANNEL));
                comboBoxLauncherBehaviour.getItems().addAll(LauncherBehaviour.values());
                comboBoxLauncherBehaviour.getSelectionModel().select(Settings.getSettings().getInt(Defs.SettingsKeys.LAUNCHER_BEHAVIOUR));
            });
        });
    }

    // Methods
    private void updateApp(@NotNull final SceneController thisScene, @NotNull final String latestVersion) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Aggiornamento ItalianDudes Launcher");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Java Executable File", "*.jar"));
        fileChooser.setInitialFileName(Defs.APP_FILE_NAME+"-"+latestVersion+".jar");
        fileChooser.setInitialDirectory(new File(Defs.JAR_POSITION).getParentFile());
        File fileNewApp;
        try {
            fileNewApp = fileChooser.showSaveDialog(Client.getStage().getScene().getWindow());
        } catch (IllegalArgumentException e) {
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            fileNewApp = fileChooser.showSaveDialog(Client.getStage().getScene().getWindow());
        }
        if (fileNewApp == null) {
            Client.setScene(thisScene);
            return;
        }
        File finalFileNewApp = fileNewApp;
        JFXDefs.startServiceTask(() -> {
            try {
                Updater.downloadNewVersion(finalFileNewApp.getAbsoluteFile().getParent() + File.separator + Defs.APP_FILE_NAME+"-"+latestVersion+".jar");
                Platform.runLater(() -> {
                    if (new ConfirmationAlert("AGGIORNAMENTO", "Aggiornamento", "Download della nuova versione completato! Vuoi chiudere questa app?").result) {
                        Client.exit();
                    } else {
                        Client.setScene(thisScene);
                    }
                });
            } catch (IOException e) {
                Logger.log(e);
                Platform.runLater(() -> {
                    new ErrorAlert("ERRORE", "Errore di Download", "Si e' verificato un errore durante il download della nuova versione dell'app.");
                    Client.setScene(thisScene);
                });
            } catch (URISyntaxException e) {
                Logger.log(e);
                Platform.runLater(() -> {
                    new ErrorAlert("ERRORE", "Errore di Validazione", "Si e' verificato un errore durante la validazione del link di download della nuova versione dell'app.");
                    Client.setScene(thisScene);
                });
            }
        });
    }

    // EDT
    @FXML
    private void checkForUpdates() {
        SceneController thisScene = Client.getScene();
        Client.setScene(SceneLoading.getScene());
        JFXDefs.startServiceTask(() -> {
            String latestVersion = Updater.getLatestVersion();
            if (latestVersion == null) {
                Platform.runLater(() -> {
                    new ErrorAlert("ERRORE", "Errore di Connessione", "Si e' verificato un errore durante il controllo della versione.");
                    Client.setScene(thisScene);
                });
                return;
            }

            String currentVersion = null;
            try {
                Attributes attributes = JarHandler.ManifestReader.readJarManifest(Defs.JAR_POSITION);
                currentVersion = JarHandler.ManifestReader.getValue(attributes, "Version");
            } catch (IOException e) {
                Logger.log(e);
            }

            if (Updater.getLatestVersion(currentVersion, latestVersion).equals(currentVersion)) {
                Platform.runLater(() -> {
                    new InformationAlert("AGGIORNAMENTO", "Controllo Versione", "La versione corrente e' la piu' recente.");
                    Client.setScene(thisScene);
                });
                return;
            }

            String finalCurrentVersion = currentVersion;
            Platform.runLater(() -> {
                if (new ConfirmationAlert("AGGIORNAMENTO", "Trovata Nuova Versione", "E' stata trovata una nuova versione. Vuoi scaricarla?\nVersione Corrente: "+ finalCurrentVersion +"\nNuova Versione: "+latestVersion).result) {
                    updateApp(thisScene, latestVersion);
                } else {
                    Platform.runLater(() -> Client.setScene(thisScene));
                }
            });
        });
    }
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
                Settings.getSettings().put(Defs.SettingsKeys.LAUNCHER_BEHAVIOUR, comboBoxLauncherBehaviour.getSelectionModel().getSelectedItem().ordinal());
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
