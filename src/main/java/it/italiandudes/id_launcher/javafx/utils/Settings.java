package it.italiandudes.id_launcher.javafx.utils;

import it.italiandudes.id_launcher.release.ReleaseType;
import it.italiandudes.id_launcher.utils.Defs;
import it.italiandudes.idl.common.JSONManager;
import it.italiandudes.idl.common.JarHandler;
import it.italiandudes.idl.common.Logger;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public final class Settings {

    // Settings
    private static JSONObject SETTINGS = null;

    // Settings Loader
    public static void loadSettingsFile() {
        File settingsFile = new File(new File(Defs.JAR_POSITION).getParent() + File.separator + Defs.Resources.JSON.JSON_LAUNCHER_SETTINGS);
        if (!settingsFile.exists() || !settingsFile.isFile()) {
            try {
                JarHandler.copyFileFromJar(new File(Defs.JAR_POSITION), Defs.Resources.JSON.DEFAULT_JSON_LAUNCHER_SETTINGS, settingsFile, true);
            } catch (IOException e) {
                Logger.log(e);
                return;
            }
        }
        try {
            SETTINGS = JSONManager.readJSON(settingsFile);
            fixJSONSettings();
        } catch (IOException | JSONException e) {
            Logger.log(e);
        }
    }

    // Settings Checker
    private static void fixJSONSettings() throws JSONException, IOException {
        try {
            SETTINGS.getBoolean(Defs.SettingsKeys.ENABLE_DARK_MODE);
        } catch (JSONException e) {
            SETTINGS.remove(Defs.SettingsKeys.ENABLE_DARK_MODE);
            SETTINGS.put(Defs.SettingsKeys.ENABLE_DARK_MODE, true);
        }
        try {
            int releaseChannel = SETTINGS.getInt(Defs.SettingsKeys.RELEASE_CHANNEL);
            if (releaseChannel < 0 || releaseChannel >= ReleaseType.values().length) throw new JSONException("Release channel out of bounds");
        } catch (JSONException e) {
            SETTINGS.remove(Defs.SettingsKeys.RELEASE_CHANNEL);
            SETTINGS.put(Defs.SettingsKeys.RELEASE_CHANNEL, ReleaseType.RELEASE.ordinal());
        }
        writeJSONSettings();
    }

    // Settings Writer
    public static void writeJSONSettings() throws IOException {
        JSONManager.writeJSON(SETTINGS, new File(new File(Defs.JAR_POSITION).getParent() + File.separator + Defs.Resources.JSON.JSON_LAUNCHER_SETTINGS));
    }

    // Settings Getter
    @NotNull
    public static JSONObject getSettings() {
        return SETTINGS;
    }
}
