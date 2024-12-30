package it.italiandudes.id_launcher.javafx.controllers.tabs;

import it.italiandudes.id_launcher.release.IDRelease;
import it.italiandudes.id_launcher.release.IDReleaseManager;
import it.italiandudes.id_launcher.release.IDVersion;
import it.italiandudes.id_launcher.release.ReleaseType;
import it.italiandudes.id_launcher.utils.Defs;
import it.italiandudes.idl.common.JarHandler;
import it.italiandudes.idl.common.Logger;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.stream.Collectors;

public final class ControllerSceneTabDnD_Visualizer {

    /*
    *
    * TODO List:
    * - Implementare dei settaggi specifici per ogni app, tra cui cose come:
    *   - All'apertura dell'app il comportamento del launcher se chiudersi, minimizzarsi o rimanere aperto
    *   - Quali tipi versioni dell'app accettare (RELEASE, BETA, ALFA, DEV)
    * - Inserire da qualche parte nella tab dell'app che sono state trovate versioni non compatibili con il launcher.
    * - Fare in modo che la chiusura del D&D Visualizer non chiuda anche il launcher
    * */

    // Constants
    private static final File INSTALLATION_DIR = new File(new File(Defs.JAR_POSITION).getParent() + File.separator + "D&D Visualizer");
    private static final String APP_NAME = "DnD_Visualizer";

    // Attributes
    @NotNull private final ArrayList<@NotNull IDRelease> releases = new ArrayList<>();

    // Graphic Elements
    @FXML private TextField textFieldReleaseTitle;
    @FXML private TextArea textAreaPatchNotes;
    // @FXML private ProgressBar progressBarDownload;
    // @FXML private Label labelDownloadPercentage;
    @FXML private Button buttonDownload;
    @FXML private ComboBox<IDVersion> comboBoxVersionSelector;
    @FXML private Button buttonStart;

    // Initialize
    @FXML
    private void initialize() {
        resetController();
        new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() {
                        try {
                            refreshReleases();
                            postInitialize();
                        } catch (IOException e) {
                            Logger.log(e);
                        }
                        return null;
                    }
                };
            }
        }.start();
    }
    private void postInitialize() throws IOException { // Non-EDT Initialize
        Platform.runLater(this::resetController);
        File[] jars = INSTALLATION_DIR.listFiles((dir, name) -> name.startsWith(APP_NAME) && name.endsWith(".jar"));
        if (jars == null || jars.length == 0) {
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
        // labelDownloadPercentage.setVisible(false);
        // labelDownloadPercentage.setText("0%");
        // progressBarDownload.setVisible(false);
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
    }

    // EDT
    @FXML
    private void versionChanged() {
        buttonStart.setDisable(comboBoxVersionSelector.getSelectionModel().getSelectedItem() == null);
    }
    @FXML
    private void startDownload() {
        buttonDownload.setDisable(true);
        new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() {
                        IDRelease latestRelease = IDReleaseManager.getLatestReleaseFromRepositoryReleases(releases, ReleaseType.RELEASE);
                        if (latestRelease == null) {
                            buttonDownload.setDisable(false);
                            return null;
                        }
                        try {
                            if (!INSTALLATION_DIR.exists()) {
                                if (!INSTALLATION_DIR.mkdir()) {
                                    buttonDownload.setDisable(true);
                                    throw new IOException("An error has occurred during mkdir");
                                }
                            }
                            IDReleaseManager.downloadRelease(latestRelease, INSTALLATION_DIR);
                            postInitialize();
                        } catch (IOException e) {
                            Logger.log(e);
                            buttonDownload.setDisable(true);
                        }
                        return null;
                    }
                };
            }
        }.start();
    }
    @FXML
    private void startVersion() {
        IDVersion version = comboBoxVersionSelector.getSelectionModel().getSelectedItem();
        if (version == null) return;
        buttonStart.setDisable(true);
        new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() {
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
                        return null;
                    }
                };
            }
        }.start();
    }

}
