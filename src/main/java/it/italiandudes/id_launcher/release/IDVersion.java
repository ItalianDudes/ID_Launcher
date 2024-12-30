package it.italiandudes.id_launcher.release;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public class IDVersion extends File {

    // Constructors
    public IDVersion(@NotNull final File file) {
        super(file.getAbsolutePath());
    }

    // Methods
    @Override @NotNull
    public String toString() {
        return super.getName();
    }
}
