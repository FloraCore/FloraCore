package team.floracore.common.util.github;

import com.google.gson.JsonObject;
import team.floracore.common.util.gson.GsonProvider;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class GithubUtil {
    private static final String GITHUB_API_BASE_URL = "https://api.github.com/repos/FloraCore/FloraCore";
    private static final String LATEST_RELEASE_ENDPOINT = "/releases/latest";
    private static final String ACCEPT_HEADER = "application/vnd.github.v3+json";

    public static String getLeastReleaseTagVersion() throws IOException {
        JsonObject latestRelease = getLatestRelease();
        String tagName = latestRelease.get("tag_name").getAsString();
        return tagName.startsWith("v") ? tagName.substring(1) : tagName;
    }

    public static JsonObject getLatestRelease() throws IOException {
        URL url = new URL(GITHUB_API_BASE_URL + LATEST_RELEASE_ENDPOINT);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", ACCEPT_HEADER);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            return GsonProvider.normal().fromJson(reader, JsonObject.class);
        }
    }

    public static boolean isLatestVersion(String latestVersionString, String pluginVersionString) {
        String[] latestVersion = latestVersionString.split("\\.");
        String[] pluginVersion = pluginVersionString.split("\\.");
        for (int i = 0; i < latestVersion.length; i++) {
            int latestVersionPart = Integer.parseInt(latestVersion[i]);
            int pluginVersionPart = i < pluginVersion.length ? Integer.parseInt(pluginVersion[i]) : 0;
            if (latestVersionPart > pluginVersionPart) {
                return false;
            } else if (latestVersionPart < pluginVersionPart) {
                return true;
            }
        }
        return true;
    }
}
