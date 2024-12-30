package it.italiandudes.id_launcher;

import it.italiandudes.idl.common.JarHandler;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.Attributes;

public class ReflectionTest {

    // Main Method
    public static void main(String[] args) throws Throwable {

        // Percorso del JAR da lanciare
        File jarFile = new File("C:\\Users\\Hacker6329\\Desktop\\D&D Visualizer\\tester.jar");

        Attributes attributes = JarHandler.ManifestReader.readJarManifest(jarFile);
        if (!JarHandler.ManifestReader.containsKey(attributes, "ID-Launcher-Enabled") || !JarHandler.ManifestReader.getValue(attributes, "ID-Launcher-Enabled").equals("true")) {
            System.err.println("Questo jar non è lanciabile");
            return;
        }

        // Carica il JAR dinamicamente tramite URLClassLoader
        URL jarUrl = jarFile.toURI().toURL();
        URLClassLoader classLoader = new URLClassLoader(new URL[] {jarUrl}, ClassLoader.getSystemClassLoader());

        // Load the DnD_Visualizer class
        Class<?> dndVisualizerClass = classLoader.loadClass("it.italiandudes.dnd_visualizer.DnD_Visualizer");

        // Get and run the main method
        Method mainMethod = dndVisualizerClass.getMethod("launcherMain", ClassLoader.class, String[].class);
        mainMethod.invoke(null, classLoader, new String[]{});
    }

    // Methods
    /*
    private void updateApp(@NotNull final SceneController thisScene, @NotNull final String latestVersion) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Aggiornamento D&D Visualizer");
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
        new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() {
                        try {
                            Updater.downloadNewVersion(finalFileNewApp.getAbsoluteFile().getParent() + File.separator + Defs.APP_FILE_NAME+"-"+latestVersion+".jar");
                            Platform.runLater(() -> {
                                if (new ConfirmationAlert("AGGIORNAMENTO", "Aggiornamento", "Download della nuova versione completato! Vuoi chiudere questa app?").result) {
                                    System.exit(0);
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
                        }
                        return null;
                    }
                };
            }
        }.start();
    }
    private void checkForUpdates() {
        SceneController thisScene = Client.getScene();
        Client.setScene(SceneLoading.getScene());
        new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() {
                        String latestVersion = Updater.getLatestVersion();
                        if (latestVersion == null) {
                            Platform.runLater(() -> {
                                new ErrorAlert("ERRORE", "Errore di Connessione", "Si e' verificato un errore durante il controllo della versione.");
                                Client.setScene(thisScene);
                            });
                            return null;
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
                            return null;
                        }

                        String finalCurrentVersion = currentVersion;
                        Platform.runLater(() -> {
                            if (new ConfirmationAlert("AGGIORNAMENTO", "Trovata Nuova Versione", "E' stata trovata una nuova versione. Vuoi scaricarla?\nVersione Corrente: "+ finalCurrentVersion +"\nNuova Versione: "+latestVersion).result) {
                                updateApp(thisScene, latestVersion);
                            } else {
                                Platform.runLater(() -> Client.setScene(thisScene));
                            }

                        });
                        return null;
                    }
                };
            }
        }.start();
    }*/
}
