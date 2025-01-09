package it.italiandudes.id_launcher.javafx.controllers.tabs;

import it.italiandudes.id_launcher.enums.LauncherBehaviour;
import it.italiandudes.id_launcher.javafx.Client;
import it.italiandudes.id_launcher.javafx.JFXDefs;
import it.italiandudes.id_launcher.javafx.alerts.ErrorAlert;
import it.italiandudes.id_launcher.javafx.controllers.ControllerSceneMainMenu;
import it.italiandudes.id_launcher.javafx.utils.Settings;
import it.italiandudes.id_launcher.release.IDRelease;
import it.italiandudes.id_launcher.release.IDReleaseManager;
import it.italiandudes.id_launcher.release.IDVersion;
import it.italiandudes.id_launcher.enums.ReleaseType;
import it.italiandudes.id_launcher.utils.Defs;
import it.italiandudes.idl.common.JarHandler;
import it.italiandudes.idl.common.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.jar.Attributes;
import java.util.stream.Collectors;

public final class ControllerSceneTabDnD_Visualizer {

    // Main Link
    private ControllerSceneMainMenu mainMenuController = null;
    private volatile boolean configurationComplete;
    public void setMainMenuController(@NotNull final ControllerSceneMainMenu controller) {
        mainMenuController = controller;
    }
    public void configurationComplete() {
        configurationComplete = true;
    }

    // Constants
    private static final File INSTALLATION_DIR = new File(new File(Defs.JAR_POSITION).getParent() + File.separator + "D&D Visualizer");
    private static final String APP_NAME = "DnD_Visualizer";

    // Attributes
    @NotNull private final ArrayList<@NotNull IDRelease> releases = new ArrayList<>();
    @Nullable private IDRelease latest = null;

    // Graphic Elements
    @FXML private TextField textFieldReleaseTitle;
    @FXML private TextArea textAreaPatchNotes;
    @FXML private ProgressBar progressBarDownload;
    @FXML private Label labelDownload;
    @FXML private Button buttonDownload;
    @FXML private ComboBox<IDVersion> comboBoxVersionSelector;
    @FXML private Button buttonStart;
    @FXML private Button buttonRefresh;

    // Initialize
    @FXML
    private void initialize() {
        JFXDefs.startServiceTask(() -> {
            //noinspection StatementWithEmptyBody
            while (!configurationComplete);
            try {
                postInitialize();
            } catch (IOException e) {
                Logger.log(e);
            }
        });
    }
    private void postInitialize() throws IOException { // Non-EDT Initialize
        Platform.runLater(this::resetController);
        refreshReleases();
        File[] versionDirs = INSTALLATION_DIR.listFiles();
        ArrayList<@NotNull File> jars = new ArrayList<>();
        if (versionDirs != null) {
            Arrays.stream(versionDirs).forEach(version -> {
                File[] jarFiles = version.listFiles((dir, name) -> name.startsWith(APP_NAME) && name.endsWith(".jar"));
                if (jarFiles != null && jarFiles.length > 0) jars.addAll(Arrays.asList(jarFiles));
            });
        }
        String latestFilename = latest != null?latest.getFilename():null;
        if (jars.isEmpty() || jars.stream().map(File::getName).noneMatch(Predicate.isEqual(latestFilename))) {
            Platform.runLater(() -> {
                buttonDownload.setDisable(false);
                buttonDownload.setVisible(true);
            });
        } else {
            List<File> launchableJarList = new ArrayList<>();
            for (File jar : jars) {
                Attributes manifest = JarHandler.ManifestReader.readJarManifest(jar);
                if (JarHandler.ManifestReader.containsKey(manifest, "ID-Launcher-Enabled") && JarHandler.ManifestReader.getValue(manifest, "ID-Launcher-Enabled").equals("true")) {
                    launchableJarList.add(jar);
                }
            }
            Platform.runLater(() -> {
                buttonDownload.setDisable(true);
                buttonDownload.setVisible(false);
                buttonStart.setVisible(true);
                comboBoxVersionSelector.getItems().addAll(launchableJarList.stream().map(IDVersion::new).collect(Collectors.toList()));
                comboBoxVersionSelector.getSelectionModel().selectFirst();
                comboBoxVersionSelector.setVisible(true);
            });
        }
    }

    // Methods
    private void resetController() {
        textFieldReleaseTitle.clear();
        textAreaPatchNotes.clear();
        comboBoxVersionSelector.getSelectionModel().clearSelection();
        comboBoxVersionSelector.getItems().clear();
        comboBoxVersionSelector.setVisible(false);
        labelDownload.setVisible(false);
        progressBarDownload.setVisible(false);
        buttonDownload.setVisible(true);
        buttonDownload.setDisable(true);
        buttonStart.setVisible(false);
        buttonStart.setDisable(true);
    }
    private void refreshReleases() {
        releases.clear();
        ArrayList<@NotNull IDRelease> fetchedReleases = IDReleaseManager.getIDReleases(APP_NAME);
        if (fetchedReleases == null) return;
        releases.addAll(fetchedReleases);
        latest = IDReleaseManager.getLatestReleaseFromRepositoryReleases(releases, ReleaseType.values()[Settings.getSettings().getInt(Defs.SettingsKeys.RELEASE_CHANNEL)]);
        displayLatestRelease();
    }
    private void displayLatestRelease() {
        if (latest == null) return;
        Platform.runLater(() -> {
            textFieldReleaseTitle.setText(latest.getReleaseTitle());
            textAreaPatchNotes.setText(latest.getPatchNotes());
        });
    }

    // EDT
    @FXML
    private void refresh() {
        buttonRefresh.setDisable(true);
        resetController();
        JFXDefs.startServiceTask(() -> {
            refreshReleases();
            try {
                postInitialize();
            } catch (IOException e) {
                Logger.log(e);
            }
            Platform.runLater(() -> buttonRefresh.setDisable(false));
        });
    }
    @FXML
    private void versionChanged() {
        buttonStart.setDisable(comboBoxVersionSelector.getSelectionModel().getSelectedItem() == null);
    }
    @FXML
    private void startDownload() {
        buttonDownload.setDisable(true);
        JFXDefs.startServiceTask(() -> {
            if (latest == null) {
                Platform.runLater(() -> buttonDownload.setDisable(false));
                return;
            }
            Platform.runLater(() -> {
                buttonDownload.setVisible(false);
                progressBarDownload.setVisible(true);
                labelDownload.setVisible(true);
            });
            try {
                if (!INSTALLATION_DIR.exists()) {
                    if (!INSTALLATION_DIR.mkdir()) {
                        Platform.runLater(ControllerSceneTabDnD_Visualizer.this::resetController);
                        throw new IOException("An error has occurred during mkdir");
                    }
                }
                IDReleaseManager.downloadRelease(latest, INSTALLATION_DIR);
                postInitialize();
            } catch (IOException e) {
                Logger.log(e);
                Platform.runLater(() -> {
                    new ErrorAlert("ERRORE", "Errore di I/O", "Si e' verificato un errore durante il download.");
                    resetController();
                });
            }
        });
    }
    @FXML
    private void startVersion() {
        IDVersion version = comboBoxVersionSelector.getSelectionModel().getSelectedItem();
        if (version == null) return;
        buttonStart.setDisable(true);
        JFXDefs.startServiceTask(() -> {
            try {
                URL jarUrl = version.toURI().toURL();
                URLClassLoader classLoader = new URLClassLoader(new URL[]{jarUrl}, ClassLoader.getSystemClassLoader());
                Class<?> dndVisualizerClass = classLoader.loadClass("it.italiandudes.dnd_visualizer.DnD_Visualizer");
                Method mainMethod = dndVisualizerClass.getMethod("launcherMain", ClassLoader.class, String[].class);
                LauncherBehaviour behaviour = LauncherBehaviour.values()[Settings.getSettings().getInt(Defs.SettingsKeys.LAUNCHER_BEHAVIOUR)];
                switch (behaviour) {
                    case CLOSE_ON_LAUNCH:
                        Logger.log("Starting D&D Visualizer, launcher closing...");
                        Platform.runLater(() -> Client.getStage().hide());
                        break;

                    case MINIMIZE:
                        Logger.log("Starting D&D Visualizer, minimizing launcher...");
                        Platform.runLater(() -> Client.getStage().setIconified(true));
                        break;

                    case STAY_OPEN:
                        Logger.log("Starting D&D Visualizer, staying open...");
                        // TODO: implement loop hole to keep disabled the start button until app is closed
                        break;
                }
                mainMethod.invoke(null, classLoader, new String[]{});
                if (behaviour != LauncherBehaviour.CLOSE_ON_LAUNCH) {
                    Logger.log("D&D Visualizer closed, restoring start...");
                    Platform.runLater(() -> buttonStart.setDisable(false));
                }
            } catch (Exception e) {
                Logger.log(e);
            }
            Platform.runLater(() -> buttonStart.setDisable(false));
        });
    }

}
