package it.italiandudes.id_launcher.release;

import it.italiandudes.id_launcher.enums.ReleaseType;
import it.italiandudes.id_launcher.exception.DeprecatedReleaseException;
import it.italiandudes.id_launcher.utils.Defs;
import it.italiandudes.idl.common.JarHandler;
import it.italiandudes.idl.common.Logger;
import it.italiandudes.idl.common.TargetPlatform;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;

public final class IDReleaseManager {

    // Methods
    @Nullable
    public static ArrayList<@NotNull IDRelease> getIDReleases(@NotNull final String repositoryName) {
        try {
            URL url = new URI("https://api.github.com/repos/ItalianDudes/" + repositoryName + "/releases").toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            try (InputStream inputStream = connection.getInputStream()) {
                StringBuilder response = new StringBuilder();
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    response.append(new String(buffer, 0, bytesRead, StandardCharsets.UTF_8));
                }
                JSONArray jsonReleases = new JSONArray(response.toString());
                ArrayList<@NotNull IDRelease> releases = new ArrayList<>();
                for (int i=0; i<jsonReleases.length(); i++) {
                    try {
                        releases.add(new IDRelease(jsonReleases.getJSONObject(i)));
                    } catch (DeprecatedReleaseException ignored) {}
                }
                return releases;
            } finally {
                connection.disconnect();
            }
        } catch (IOException | JSONException | URISyntaxException e) {
            Logger.log(e);
            return null;
        }
    }
    public static boolean downloadRelease(@NotNull final IDRelease release, @NotNull File dest) throws IOException, URISyntaxException {
        URL url = new URI(release.getDownloadLink()).toURL();
        InputStream is = url.openConnection().getInputStream();
        String releaseDestination = dest.getAbsolutePath() + File.separator + release.getVersion() + File.separator + release.getFilename();
        if (!new File(releaseDestination).mkdirs()) {
            throw new IOException("An error has occurred while creating path for release " + releaseDestination);
        }
        Files.copy(is, Paths.get(releaseDestination), StandardCopyOption.REPLACE_EXISTING);
        is.close();
        if (Defs.CURRENT_PLATFORM != null) return validateReleaseManifestTargetPlatform(new File(releaseDestination));
        else return true;
    }
    private static boolean validateReleaseManifestTargetPlatform(@NotNull final File releaseFile) {
        try {
            Attributes attributes = JarHandler.ManifestReader.readJarManifest(releaseFile);
            String manifestTargetPlatform = JarHandler.ManifestReader.getValue(attributes, "Target-Platform");
            if (manifestTargetPlatform == null) return false;
            TargetPlatform targetPlatform = TargetPlatform.fromManifestTargetPlatform(manifestTargetPlatform);
            if (targetPlatform == null) return false;
            return Defs.CURRENT_PLATFORM == targetPlatform;
        } catch (Exception e) {
            Logger.log(e);
            return false;
        }
    }
    @Nullable
    public static IDRelease getLatestReleaseFromRepositoryReleases(@NotNull final ArrayList<@NotNull IDRelease> releases, @NotNull final ReleaseType releaseType) {
        if (releases.isEmpty()) return null;
        List<IDRelease> allowedReleases = releases.stream().filter(release -> release.getReleaseType().ordinal() <= releaseType.ordinal()).sorted().toList();
        return allowedReleases.isEmpty()?null:allowedReleases.getLast();
    }
}
