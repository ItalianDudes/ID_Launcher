package it.italiandudes.id_launcher.release;

import it.italiandudes.idl.common.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class IDReleaseManager {

    // Methods
    @Nullable
    public static ArrayList<@NotNull IDRelease> getIDReleases(@NotNull final String repositoryName) {
        try {
            URL url = new URL("https://api.github.com/repos/ItalianDudes/" + repositoryName + "/releases");
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
                IDRelease[] releases = new IDRelease[jsonReleases.length()];
                for (int i=0; i<jsonReleases.length(); i++) {
                    releases[i] = new IDRelease(jsonReleases.getJSONObject(i));
                }
                return new ArrayList<>(Arrays.asList(releases));
            } finally {
                connection.disconnect();
            }
        } catch (IOException | JSONException e) {
            Logger.log(e);
            return null;
        }
    }
    public static void downloadRelease(@NotNull final IDRelease release, @NotNull File dest) throws IOException {
        URL url = new URL(release.getDownloadLink());
        InputStream is = url.openConnection().getInputStream();
        if (dest.isDirectory()) {
            dest = new File(dest.getAbsolutePath() + File.separator + release.getFilename());
        }
        Files.copy(is, Paths.get(dest.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);
        is.close();
    }
    @Nullable
    public static IDRelease getLatestReleaseFromRepositoryReleases(@NotNull final ArrayList<@NotNull IDRelease> releases, @NotNull final ReleaseType releaseType) {
        if (releases.isEmpty()) return null;
        List<IDRelease> allowedReleases = releases.stream().filter(release -> release.getReleaseType().ordinal() <= releaseType.ordinal()).sorted().collect(Collectors.toList());
        return allowedReleases.isEmpty()?null:allowedReleases.get(allowedReleases.size()-1);
    }
}
