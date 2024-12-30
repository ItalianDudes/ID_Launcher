package it.italiandudes.id_launcher.release;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum ReleaseType {
    RELEASE,
    BETA,
    ALPHA,
    DEV;

    // Static
    @Nullable
    public static ReleaseType fromVersionToReleaseType(@NotNull final String version) {
        if (version.isEmpty()) return null;
        return fromReleaseMarkerToReleaseType(String.valueOf(version.charAt(version.length()-1)));
    }
    @Nullable
    public static ReleaseType fromReleaseMarkerToReleaseType(@NotNull final String versionMarker) {
        if (versionMarker.length() != 1) return null;
        switch (versionMarker) {
            case "R": return RELEASE;
            case "B": return BETA;
            case "A": return ALPHA;
            case "D": return DEV;
            default: return null;
        }
    }

    // Attributes
    @NotNull private final String VERSION_MARKER;

    // Constructor
    ReleaseType() {
        this.VERSION_MARKER = String.valueOf(name().charAt(0));
    }

    // Methods
    public @NotNull String getVersionMarker() {
        return VERSION_MARKER;
    }
    @Override @NotNull
    public String toString() {
        return name();
    }
}
