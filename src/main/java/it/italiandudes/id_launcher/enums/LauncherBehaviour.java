package it.italiandudes.id_launcher.enums;

import org.jetbrains.annotations.NotNull;

public enum LauncherBehaviour {
    CLOSE_ON_LAUNCH("Chiudi all'Avvio"),
    STAY_OPEN("Rimani Aperto"),
    MINIMIZE("Minimizza"),
    HIDE_UNTIL_CLOSE("Nascondi fino alla Chiusura");

    // Attributes
    @NotNull private final String NAME;

    // Constructor
    LauncherBehaviour(@NotNull final String NAME) {
        this.NAME = NAME;
    }

    // Methods
    @Override @NotNull
    public String toString() {
        return NAME;
    }
}
