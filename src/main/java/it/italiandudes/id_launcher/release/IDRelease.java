package it.italiandudes.id_launcher.release;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

public final class IDRelease implements Comparator<IDRelease>, Comparable<IDRelease> {

    // Attributes
    @NotNull private final String filename;
    @NotNull private final String releaseTitle;
    @NotNull private final String patchNotes;
    @NotNull private final String version;
    @NotNull private final ReleaseType releaseType;
    @NotNull private final String downloadLink;

    // Constructors
    public IDRelease(@NotNull final JSONObject release) throws IOException {
        try {
            this.releaseTitle = release.getString("name");
            this.patchNotes = release.getString("body");
            this.version = release.getString("tag_name");
            ReleaseType releaseType = ReleaseType.fromVersionToReleaseType(version);
            if (releaseType == null) throw new IOException("Provided JSONObject contains invalid version marker");
            this.releaseType = releaseType;
            JSONArray assets = release.getJSONArray("assets");
            String downloadLink = null;
            String filename = null;
            for (int i = 0; i < assets.length(); i++) {
                JSONObject asset = assets.getJSONObject(i);
                String assetName = asset.getString("name");
                if (assetName.contains("jar")) {
                    filename = assetName;
                    downloadLink = asset.getString("browser_download_url");
                    break;
                }
            }
            if (downloadLink == null) throw new IOException("Provided JSONObject doesn't contains download link");
            this.downloadLink = downloadLink;
            this.filename = filename;
        } catch (JSONException e) {
            throw new IOException("Provided JSONObject is invalid", e);
        }
    }

    // Methods
    public @NotNull String getFilename() {
        return filename;
    }
    public @NotNull String getReleaseTitle() {
        return releaseTitle;
    }
    public @NotNull String getPatchNotes() {
        return patchNotes;
    }
    public @NotNull String getVersion() {
        return version;
    }
    public @NotNull ReleaseType getReleaseType() {
        return releaseType;
    }
    public @NotNull String getDownloadLink() {
        return downloadLink;
    }
    @Override
    public int compare(IDRelease o1, IDRelease o2) {
        if (o1 == null && o2 == null) return 0;
        if (o1 == null) return -1;
        if (o2 == null) return 1;

        String versionA = o1.version;
        String versionB = o2.version;

        String numberOnlyVersionA = versionA.substring(0, versionA.length()-1);
        String numberOnlyVersionB = versionB.substring(0, versionB.length()-1);

        String[] rawSplitVersionA = numberOnlyVersionA.split("\\.");
        int[] splitVersionA = new int[rawSplitVersionA.length];
        for (int i=0; i<splitVersionA.length; i++) {
            splitVersionA[i] = Integer.parseInt(rawSplitVersionA[i]);
        }
        String[] rawSplitVersionB = numberOnlyVersionB.split("\\.");
        int [] splitVersionB = new int[rawSplitVersionB.length];
        for (int i=0; i<splitVersionB.length; i++) {
            splitVersionB[i] = Integer.parseInt(rawSplitVersionB[i]);
        }

        if (Arrays.equals(splitVersionA, splitVersionB)) return 1;

        for (int i=0; i<splitVersionA.length; i++) {
            if (splitVersionA[i] > splitVersionB[i]) {
                return 1;
            } else if (splitVersionA[i] < splitVersionB[i]) {
                return -1;
            } else if (i+1 >= splitVersionA.length && i+1 < splitVersionB.length) {
                return -1;
            } else if (i+1 >= splitVersionB.length && i+1 < splitVersionA.length) {
                return 1;
            }
        }
        return 0;
    }
    @Override
    public int compareTo(@NotNull IDRelease o) {
        return compare(this, o);
    }
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof IDRelease)) return false;

        IDRelease release = (IDRelease) o;
        return getFilename().equals(release.getFilename()) && getReleaseTitle().equals(release.getReleaseTitle()) && getPatchNotes().equals(release.getPatchNotes()) && getVersion().equals(release.getVersion()) && getReleaseType() == release.getReleaseType() && getDownloadLink().equals(release.getDownloadLink());
    }
    @Override
    public int hashCode() {
        int result = getFilename().hashCode();
        result = 31 * result + getReleaseTitle().hashCode();
        result = 31 * result + getPatchNotes().hashCode();
        result = 31 * result + getVersion().hashCode();
        result = 31 * result + getReleaseType().hashCode();
        result = 31 * result + getDownloadLink().hashCode();
        return result;
    }
    @Override @NotNull
    public String toString() {
        return getReleaseTitle();
    }
}
