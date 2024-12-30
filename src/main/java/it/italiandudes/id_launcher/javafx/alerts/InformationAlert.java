package it.italiandudes.id_launcher.javafx.alerts;

import it.italiandudes.id_launcher.javafx.JFXDefs;
import it.italiandudes.id_launcher.javafx.utils.ThemeHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

@SuppressWarnings("unused")
public final class InformationAlert extends Alert {

    //Constructors
    public InformationAlert(String title, String header, String content){
        super(AlertType.INFORMATION);
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
        showAndWait();
    }
    public InformationAlert(String header, String content){
        this(null, header, content);
    }
    public InformationAlert(){
        this(null,null,null);
    }
}
