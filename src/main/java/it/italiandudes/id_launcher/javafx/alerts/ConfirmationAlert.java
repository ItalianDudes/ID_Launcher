package it.italiandudes.id_launcher.javafx.alerts;

import it.italiandudes.id_launcher.javafx.JFXDefs;
import it.italiandudes.id_launcher.javafx.utils.ThemeHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.util.Optional;

@SuppressWarnings("unused")
public final class ConfirmationAlert extends Alert {

    //Attributes
    public final boolean result;

    //Constructors
    public ConfirmationAlert(String title, String header, String content){
        super(AlertType.CONFIRMATION);
        this.setResizable(true);
        ((Stage) getDialogPane().getScene().getWindow()).getIcons().add(JFXDefs.AppInfo.LOGO);
        if(title!=null) setTitle(title);
        if(header!=null) setHeaderText(header);
        if(content!=null) {
            TextArea area = new TextArea(content);
            area.setWrapText(true);
            area.setEditable(false);
            getDialogPane().setContent(area);
        }
        ThemeHandler.loadConfigTheme(this.getDialogPane().getScene());
        Optional<ButtonType> result = showAndWait();
        this.result = result.isPresent() && result.get().equals(ButtonType.OK);
    }
    public ConfirmationAlert(String header, String content){
        this(null, header, content);
    }
    public ConfirmationAlert(){
        this(null,null,null);
    }

}