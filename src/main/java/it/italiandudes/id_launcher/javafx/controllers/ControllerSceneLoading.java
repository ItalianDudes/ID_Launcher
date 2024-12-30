package it.italiandudes.id_launcher.javafx.controllers;

import it.italiandudes.id_launcher.javafx.JFXDefs;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public final class ControllerSceneLoading {

    // Attributes
    public static final Image LOADING_GIF = new Image(JFXDefs.Resources.GIF.GIF_LOADING);

    // Graphic Elements
    @FXML private ImageView imageViewLoading;

    //Initialize
    @FXML
    private void initialize() {
        imageViewLoading.setImage(LOADING_GIF);
    }
}