package it.italiandudes.id_launcher.javafx.components;

import javafx.scene.Parent;
import org.jetbrains.annotations.NotNull;

public final class SceneController {

    // Attributes
    @NotNull private final Parent parent;
    @NotNull private final Object controller;

    // Constructors
    public SceneController(@NotNull final Parent parent, @NotNull final Object controller) {
        this.parent = parent;
        this.controller = controller;
    }

    // Methods
    @NotNull
    public Parent getParent() {
        return parent;
    }
    @NotNull
    public Object getController() {
        return controller;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SceneController that)) return false;
        if (!getParent().equals(that.getParent())) return false;
        return getController().equals(that.getController());
    }
    @Override
    public int hashCode() {
        int result = getParent().hashCode();
        result = 31 * result + getController().hashCode();
        return result;
    }
}
