package it.italiandudes.id_launcher.javafx.controllers.tabs;

import it.italiandudes.id_launcher.javafx.JFXDefs;
import it.italiandudes.id_launcher.javafx.alerts.ErrorAlert;
import it.italiandudes.id_launcher.javafx.controllers.ControllerSceneMainMenu;
import it.italiandudes.id_launcher.javafx.utils.Settings;
import it.italiandudes.id_launcher.release.IDRelease;
import it.italiandudes.id_launcher.release.IDReleaseManager;
import it.italiandudes.id_launcher.release.IDVersion;
import it.italiandudes.id_launcher.release.ReleaseType;
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

    /*
    *
    * TODO List:
    * - All'apertura dell'app il comportamento del launcher se chiudersi, minimizzarsi o rimanere aperto
    * - Inserire da qualche parte nella tab dell'app che sono state trovate versioni non compatibili con il launcher
    * - Fare in modo che la chiusura del D&D Visualizer non chiuda anche il launcher
    * - Dedicare una cartella ad ogni versione del D&D Visualizer che l'utente ha installato tramite launcher
    * */

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
        File[] jars = INSTALLATION_DIR.listFiles((dir, name) -> name.startsWith(APP_NAME) && name.endsWith(".jar"));
        String filename = latest != null?latest.getFilename():null;
        if (jars == null || jars.length == 0 || Arrays.stream(jars).map(File::getName).noneMatch(Predicate.isEqual(filename))) {
            Platform.runLater(() -> {
                buttonDownload.setDisable(false);
                buttonDownload.setVisible(true);
            });
        } else {
            List<File> jarList = new ArrayList<>();
            for (File jar : jars) {
                Attributes manifest = JarHandler.ManifestReader.readJarManifest(jar);
                if (JarHandler.ManifestReader.containsKey(manifest, "ID-Launcher-Enabled") && JarHandler.ManifestReader.getValue(manifest, "ID-Launcher-Enabled").equals("true")) {
                    jarList.add(jar);
                }
            }
            Platform.runLater(() -> {
                buttonDownload.setDisable(true);
                buttonDownload.setVisible(false);
                buttonStart.setVisible(true);
                comboBoxVersionSelector.getItems().addAll(jarList.stream().map(IDVersion::new).collect(Collectors.toList()));
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
                mainMethod.invoke(null, classLoader, new String[]{});
                // mainMethod.invoke() blocks until the thread is "dead", but with javafx after the main calls javafx the thread dies
                // TODO: find a way to block here until the D&D Visualizer closes
            } catch (Exception e) {
                Logger.log(e);
            }
            Platform.runLater(() -> buttonStart.setDisable(false));
        });
    }

}
