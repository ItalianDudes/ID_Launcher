package it.italiandudes.id_launcher;

import it.italiandudes.id_launcher.javafx.Client;
import it.italiandudes.id_launcher.utils.Defs;
import it.italiandudes.idl.common.InfoFlags;
import it.italiandudes.idl.common.JarHandler;
import it.italiandudes.idl.common.Logger;
import it.italiandudes.idl.common.TargetPlatform;
import org.apache.commons.lang3.SystemUtils;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.jar.Attributes;

public final class ID_Launcher {

    // Main Method
    public static void main(String[] args) {

        // Setting Charset to UTF-8
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));

        // Initializing the logger
        try {
            Logger.init();
            Logger.log("Logger initialized!");
        } catch (IOException e) {
            Logger.log("An error has occurred during Logger initialization, exit...");
            return;
        }

        // Configure the shutdown hooks
        Logger.log("Configuring Shutdown Hooks...");
        Runtime.getRuntime().addShutdownHook(new Thread(Logger::close));
        Logger.log("Shutdown Hooks configured!");

        // Check Java Version
        Logger.log("Verifying for Java 21...");
        if (!SystemUtils.IS_JAVA_21) {
            Logger.log("The current java version is wrong, to run this jar you need Java 21!", new InfoFlags(true, true, true, true));
            Logger.close();
            System.exit(-1);
            return;
        }
        Logger.log("Java 21 Verified!");

        // Check OS
        Logger.log("Verifying OS...");
        Logger.log("OS Name: " + SystemUtils.OS_NAME);
        Logger.log("OS Arch: " + SystemUtils.OS_ARCH);
        Logger.log("Current OS Platform: " + (Defs.CURRENT_PLATFORM != null ? Defs.CURRENT_PLATFORM.getName() : "NOT RECOGNIZED"));
        if (Defs.CURRENT_PLATFORM == null) {
            Logger.log("WARNING: Current OS Platform not recognized! An attempt to start the app will be done anyway.", new InfoFlags(true, false, false, true));
        }
        try {
            Attributes manifestAttributes = JarHandler.ManifestReader.readJarManifest(Defs.JAR_POSITION);
            @Nullable String manifestTargetPlatform = JarHandler.ManifestReader.getValue(manifestAttributes, "Target-Platform");
            if (manifestTargetPlatform == null) {
                Logger.log("Target-Platform not specified in jar manifest, this jar shouldn't be used for release.", new InfoFlags(false, false, false, true));
            } else {
                @Nullable TargetPlatform targetPlatform = TargetPlatform.fromManifestTargetPlatform(manifestTargetPlatform);
                if (targetPlatform == null) {
                    Logger.log("Target-Platform provided \"" + manifestTargetPlatform + "\" not recognized, this jar shouldn't be used for release.", new InfoFlags(false, false, false, true));
                } else {
                    Logger.log("Jar Target-Platform: " + targetPlatform.getName());
                    if (Defs.CURRENT_PLATFORM != null && !targetPlatform.isCurrentOS()) {
                        Logger.log("Target-Platform \"" + targetPlatform.getName() + "\" incompatible with the current OS Platform!", new InfoFlags(true, true, true, true));
                        Logger.close();
                        System.exit(-1);
                        return;
                    }
                }
            }
        } catch (IOException e) {
            Logger.log("An error has occurred while attempting to read jar manifest!", new InfoFlags(true, true, true, true));
            Logger.close();
            System.exit(-1);
            return;
        }

        // Start the client
        try {
            Logger.log("Starting UI...");
            Client.start(args);
        } catch (NoClassDefFoundError e) {
            Logger.log("ERROR: JAVAFX NOT FOUND!", new InfoFlags(true, true, true, true));
            Logger.log(e);
            Logger.close();
            System.exit(-1);
        }
    }
}
