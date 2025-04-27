package it.italiandudes.id_launcher.enums;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum ReleaseType {
    RELEASE("RELEASE"),
    BETA("BETA"),
    ALPHA("ALPHA [POTENZIALMENTE INSTABILE]"),
    DEV("DEV BUILD [ALTAMENTE SCONSIGLIATO]");

    // Static
    @Nullable
    public static ReleaseType fromVersionToReleaseType(@NotNull final String version) {
        if (version.isEmpty()) return null;
        return fromReleaseMarkerToReleaseType(String.valueOf(version.charAt(version.length()-1)));
    }
    @Nullable
    public static ReleaseType fromReleaseMarkerToReleaseType(@NotNull final String versionMarker) {
        if (versionMarker.length() != 1) return null;
        return switch (versionMarker) {
            case "R" -> RELEASE;
            case "B" -> BETA;
            case "A" -> ALPHA;
            case "D" -> DEV;
            default -> null;
        };
    }

    // Attributes
    @NotNull private final String name;
    @NotNull private final String VERSION_MARKER;

    // Constructor
    ReleaseType(@NotNull final String name) {
        this.name = name;
        this.VERSION_MARKER = String.valueOf(name().charAt(0));
    }

    // Methods
    public @NotNull String getVersionMarker() {
        return VERSION_MARKER;
    }
    @Override @NotNull
    public String toString() {
        return name;
    }
}
