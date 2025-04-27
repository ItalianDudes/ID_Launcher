package it.italiandudes.id_launcher.javafx;

import it.italiandudes.id_launcher.javafx.alerts.ErrorAlert;
import it.italiandudes.id_launcher.javafx.components.SceneController;
import it.italiandudes.id_launcher.javafx.scene.SceneMainMenu;
import it.italiandudes.id_launcher.javafx.utils.Settings;
import it.italiandudes.id_launcher.javafx.utils.ThemeHandler;
import it.italiandudes.idl.common.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class Client extends Application {

    // Attributes
    private static Application INSTANCE = null;
    private static Clipboard SYSTEM_CLIPBOARD = null;
    private static Stage STAGE = null;
    private static SceneController SCENE = null;

    // JavaFX Application Main
    @Override
    public void start(Stage stage) {
        INSTANCE = this;
        SYSTEM_CLIPBOARD = Clipboard.getSystemClipboard();
        Client.STAGE = stage;
        stage.setResizable(true);
        stage.setTitle(JFXDefs.AppInfo.NAME);
        stage.getIcons().add(JFXDefs.AppInfo.LOGO);
        SCENE = Objects.requireNonNull(SceneMainMenu.getScene());
        stage.setScene(new Scene(SCENE.getParent()));
        ThemeHandler.loadConfigTheme(stage.getScene());
        stage.show();
        stage.setX((JFXDefs.SystemGraphicInfo.SCREEN_WIDTH - stage.getWidth()) / 2);
        stage.setY((JFXDefs.SystemGraphicInfo.SCREEN_HEIGHT - stage.getHeight()) / 2);
        stage.getScene().getWindow().addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, event -> exit());

        // Fullscreen Event Listener
        stage.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            if (event.getCode() == KeyCode.F11) {
                if (!stage.isFullScreen()) {
                    if (!stage.isMaximized()) {
                        stage.setMaximized(true);
                    }
                    stage.setFullScreen(true);
                }
            } else if (event.getCode() == KeyCode.ESCAPE) {
                if (stage.isFullScreen()) stage.setFullScreen(false);
            }
        });

        // Notice into the logs that the application started Successfully
        Logger.log("Application started successfully!");
    }

    // Start Methods
    public static void start(String[] args) {
        Settings.loadSettingsFile();
        launch(args);
    }

    // Methods
    @NotNull
    public static Application getApplicationInstance() {
        return INSTANCE;
    }
    @NotNull
    public static Clipboard getSystemClipboard() {
        return SYSTEM_CLIPBOARD;
    }
    @NotNull
    public static Stage getStage(){
        return STAGE;
    }
    @NotNull
    public static SceneController getScene() {
        return SCENE;
    }
    public static void setScene(@NotNull final SceneController newScene) {
        SCENE = newScene;
        STAGE.getScene().setRoot(newScene.getParent());
    }
    @NotNull
    public static Stage initPopupStage(@NotNull final SceneController sceneController) {
        Stage popupStage = new Stage();
        popupStage.setResizable(true);
        popupStage.getIcons().add(JFXDefs.AppInfo.LOGO);
        popupStage.setTitle(JFXDefs.AppInfo.NAME);
        popupStage.initOwner(STAGE);
        popupStage.initModality(Modality.WINDOW_MODAL);
        popupStage.setScene(new Scene(sceneController.getParent()));
        ThemeHandler.loadConfigTheme(popupStage.getScene());
        return popupStage;
    }
    public static void showMessageAndGoToMenu(@NotNull final Throwable t) {
        Logger.log(t);
        Platform.runLater(() -> {
            new ErrorAlert("ERRORE", "Errore di Database", "Si e' verificato un errore durante la comunicazione con il database, ritorno al menu principale.");
            setScene(SceneMainMenu.getScene());
        });


    }
    public static void exit() {
        Logger.log("Exit Method Called, exiting Java process...");
        Platform.runLater(() -> STAGE.hide());
        Logger.close();
        Platform.exit();
        System.exit(0);
    }
}
