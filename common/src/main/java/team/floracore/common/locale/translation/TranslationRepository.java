package team.floracore.common.locale.translation;

import com.crowdin.client.Client;
import com.crowdin.client.core.model.Credentials;
import com.crowdin.client.core.model.DownloadLink;
import com.crowdin.client.core.model.ResponseObject;
import com.crowdin.client.languages.model.Language;
import com.crowdin.client.reports.model.ReportStatus;
import com.crowdin.client.reports.model.ReportsFormat;
import com.crowdin.client.reports.model.TopMembersGenerateReportRequest;
import com.crowdin.client.reports.model.Unit;
import com.crowdin.client.translations.model.ExportProjectTranslationRequest;
import com.crowdin.client.translationstatus.model.LanguageProgress;
import com.github.benmanes.caffeine.cache.Cache;
import com.google.errorprone.annotations.Keep;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import lombok.Data;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;
import team.floracore.common.config.ConfigKeys;
import team.floracore.common.http.AbstractHttpClient;
import team.floracore.common.http.UnsuccessfulRequestException;
import team.floracore.common.locale.message.MiscMessage;
import team.floracore.common.plugin.FloraCorePlugin;
import team.floracore.common.sender.Sender;
import team.floracore.common.util.CaffeineFactory;
import team.floracore.common.util.gson.GsonProvider;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TranslationRepository {
	private static final long MAX_BUNDLE_SIZE = 1048576L; // 1mb
	private static final long CACHE_MAX_AGE = TimeUnit.HOURS.toMillis(1);
	private static final UUID uuid = UUID.randomUUID();
	private static final Cache<UUID, MetadataResponse> metadataResponseCache = CaffeineFactory.newBuilder()
			.expireAfterWrite(1, TimeUnit.HOURS)
			.build();
	private final Long projectId = 582143L;
	private final Long pluginId = 5L;
	private final FloraCorePlugin plugin;
	private final AbstractHttpClient abstractHttpClient;

	public TranslationRepository(FloraCorePlugin plugin) {
		this.plugin = plugin;
		this.abstractHttpClient = new AbstractHttpClient(plugin.getHttpClient());
	}

	/**
	 * Gets a list of available languages.
	 *
	 * @return a list of languages
	 * @throws java.io.IOException          if an i/o error occurs
	 * @throws UnsuccessfulRequestException if the http request fails
	 */
	public List<LanguageInfo> getAvailableLanguages() throws IOException, UnsuccessfulRequestException {
		return getTranslationsMetadata().languages;
	}

	public Client getClient() {
		String token = "121272462818cddeacd56214e4f9669decde75e0730cc39dc0167c3e723a54aaf6ad7785c1444b47";
		Credentials credentials = new Credentials(token, null);
		return new Client(credentials);
	}


	public List<Contributor> getContributors() throws IOException {
		OkHttpClient client = new OkHttpClient();
		String reportUrl = downloadReport().getUrl();
		Request request = new Request.Builder()
				.url(reportUrl)
				.build();
		try (Response response = client.newCall(request).execute()) {
			if (response.isSuccessful()) {
				ResponseBody responseBody = response.body();
				String responseData = responseBody.string();
				// 处理响应数据
				Gson gson = new Gson();
				JsonObject jsonObject = gson.fromJson(responseData, JsonObject.class);
				JsonArray dataArray = jsonObject.getAsJsonArray("data");
				List<Contributor> contributors = new ArrayList<>();

				if (dataArray != null) {
					for (JsonElement element : dataArray) {
						JsonObject contributorObject = element.getAsJsonObject();

						// 重用现有的JsonObject和Contributor对象
						contributorObject = gson.fromJson(contributorObject, JsonObject.class);
						Contributor contributor = gson.fromJson(contributorObject, Contributor.class);

						contributors.add(contributor);
					}
				}

				// 使用List.addAll()一次性添加所有Contributor对象
				contributors.addAll(Objects.requireNonNull(gson.fromJson(dataArray, new TypeToken<List<Contributor>>() {
				}.getType())));
				return contributors;
			} else {
				throw new RuntimeException("Request failed:" + response.code() + " " + response.message());
			}
		} catch (IOException e) {
			throw new IOException("Request exception:" + e.getMessage());
		}
	}

	public DownloadLink downloadReport() {
		ReportStatus reportStatus = generateReport();
		String identifier = reportStatus.getIdentifier();
		return getClient().getReportsApi().downloadReport(projectId, identifier).getData();
	}

	public List<ResponseObject<LanguageProgress>> getLanguageProgress() {
		return getClient().getTranslationStatusApi().getFileProgress(projectId, pluginId, 500, 0).getData();
	}

	public ReportStatus generateReport() {
		TopMembersGenerateReportRequest topMembersGenerateReportRequest = new TopMembersGenerateReportRequest();
		topMembersGenerateReportRequest.setName("top-members");
		TopMembersGenerateReportRequest.Schema schema = new TopMembersGenerateReportRequest.Schema();
		schema.setUnit(Unit.STRINGS);
		schema.setFormat(ReportsFormat.JSON);
		topMembersGenerateReportRequest.setSchema(schema);
		return getClient().getReportsApi().generateReport(projectId, topMembersGenerateReportRequest).getData();
	}

	public Language getLanguage(String languageId) {
		return getClient().getLanguagesApi().getLanguage(languageId).getData();
	}

	// "Keep"注解避免反复进行网络调用
	@Keep
	private MetadataResponse getTranslationsMetadata() throws IOException {
		MetadataResponse metadataResponse = metadataResponseCache.getIfPresent(uuid);
		if (metadataResponse == null) {
			List<LanguageInfo> languages = new ArrayList<>();
			List<Contributor> contributors = getContributors();
			for (ResponseObject<LanguageProgress> response : getLanguageProgress()) {
				LanguageProgress languageProgress = response.getData();
				String id = languageProgress.getLanguageId();
				int percent = languageProgress.getTranslationProgress();
				LanguageInfo languageInfo = new LanguageInfo();
				languageInfo.setId(id);
				languageInfo.setProgress(percent);
				Language language = getLanguage(id);
				languageInfo.setName(language.getName());
				languageInfo.setLocale(Locale.forLanguageTag(language.getLocale()));
				List<Contributor> lcs = new ArrayList<>();
				for (Contributor contributor : contributors) {
					if (contributor.getTranslated() >= 30) {
						for (Contributor.Language cl : contributor.getLanguages()) {
							if (cl.getId().equalsIgnoreCase(id)) {
								lcs.add(contributor);
							}
						}
					}
				}
				languageInfo.setContributors(lcs);
				languages.add(languageInfo);
			}
			languages.removeIf(language -> language.progress <= 0);

			if (languages.size() >= 100) {
				// just a precaution: if more than 100 languages have been
				// returned then the metadata server is doing something silly
				throw new IOException("More than 100 languages - cancelling download");
			}

			long cacheMaxAge = 600000;

			metadataResponse = new MetadataResponse(cacheMaxAge, languages);
			metadataResponseCache.put(uuid, metadataResponse);
		}
		return metadataResponse;
	}

	public void scheduleRefreshRepeating() {
		this.plugin.getBootstrap().getScheduler().asyncRepeating(this::scheduleRefresh, 1, TimeUnit.HOURS);
	}

	/**
	 * Schedules a refresh of the current translations if necessary.
	 */
	public void scheduleRefresh() {
		if (!this.plugin.getConfiguration().get(ConfigKeys.AUTO_INSTALL_TRANSLATIONS)) {
			return; // skip
		}

		this.plugin.getBootstrap().getScheduler().executeAsync(() -> {
			// cleanup old translation files
			clearDirectory(this.plugin.getTranslationManager().getTranslationsDirectory(), Files::isRegularFile);

			try {
				refresh();
			} catch (Exception e) {
				// ignore
			}
		});
	}

	private void clearDirectory(Path directory, Predicate<Path> predicate) {
		// try-with-resource
		try (Stream<Path> stream = Files.list(directory)) {
			stream.filter(predicate).forEach(p -> {
				try {
					Files.delete(p);
				} catch (IOException e) {
					// ignore
				}
			});
		} catch (IOException e) {
			// ignore
		}
	}

	private void refresh() throws Exception {
		long lastRefresh = readLastRefreshTime();
		long timeSinceLastRefresh = System.currentTimeMillis() - lastRefresh;

		if (timeSinceLastRefresh <= CACHE_MAX_AGE) {
			return;
		}

		MetadataResponse metadata = getTranslationsMetadata();

		// perform a refresh!
		downloadAndInstallTranslations(metadata.languages, null, true);
	}

	private long readLastRefreshTime() {
		Path statusFile = this.plugin.getTranslationManager().getRepositoryStatusFile();

		if (Files.exists(statusFile)) {
			try (BufferedReader reader = Files.newBufferedReader(statusFile, StandardCharsets.UTF_8)) {
				JsonObject status = GsonProvider.normal().fromJson(reader, JsonObject.class);
				if (status.has("lastRefresh")) {
					return status.get("lastRefresh").getAsLong();
				}
			} catch (Exception e) {
				// ignore
			}
		}

		return 0L;
	}

	/**
	 * Downloads and installs translations for the given languages.
	 *
	 * @param languages    the languages to install translations for
	 * @param sender       the sender to report progress to
	 * @param updateStatus if the status file should be updated
	 */
	public void downloadAndInstallTranslations(List<LanguageInfo> languages,
	                                           @Nullable Sender sender,
	                                           boolean updateStatus) {
		TranslationManager manager = this.plugin.getTranslationManager();
		Path translationsDirectory = manager.getRepositoryTranslationsDirectory();

		// clear existing translations
		clearDirectory(translationsDirectory, TranslationManager::isTranslationFile);

		for (LanguageInfo language : languages) {
			if (sender != null) {
				MiscMessage.TRANSLATIONS_INSTALLING_SPECIFIC.send(sender, language.getLocale().toString());
			}

			String DOWNLOAD_URL = getTranslationFileDownloadLink(language.getId()).getUrl();

			Request request = new Request.Builder().header("User-Agent", "floracore")
					.url(DOWNLOAD_URL)
					.build();

			Path file = translationsDirectory.resolve(language.getLocale().toString() + ".properties");

			try (Response response = abstractHttpClient.makeHttpRequest(request)) {
				try (ResponseBody responseBody = response.body()) {
					if (responseBody == null) {
						throw new IOException("No response");
					}

					try (InputStream inputStream = new LimitedInputStream(responseBody.byteStream(),
							MAX_BUNDLE_SIZE)) {
						Files.copy(inputStream, file, StandardCopyOption.REPLACE_EXISTING);
					}
				}
			} catch (UnsuccessfulRequestException | IOException e) {
				if (sender != null) {
					MiscMessage.TRANSLATIONS_DOWNLOAD_ERROR.send(sender, language.getLocale().toString());
					this.plugin.getLogger().warn("Unable to download translations", e);
				}
			}
		}

		if (updateStatus) {
			writeLastRefreshTime();
		}

		manager.reload();
	}

	public DownloadLink getTranslationFileDownloadLink(String targetLanguageId) {
		ExportProjectTranslationRequest exportProjectTranslationRequest = new ExportProjectTranslationRequest();
		exportProjectTranslationRequest.setTargetLanguageId(targetLanguageId);
		exportProjectTranslationRequest.setFileIds(Collections.singletonList(pluginId));
		exportProjectTranslationRequest.setSkipUntranslatedStrings(true);
		return getClient().getTranslationsApi().exportProjectTranslation(projectId, exportProjectTranslationRequest).getData();
	}

	private void writeLastRefreshTime() {
		Path statusFile = this.plugin.getTranslationManager().getRepositoryStatusFile();

		try (BufferedWriter writer = Files.newBufferedWriter(statusFile, StandardCharsets.UTF_8)) {
			JsonObject status = new JsonObject();
			status.add("lastRefresh", new JsonPrimitive(System.currentTimeMillis()));
			GsonProvider.prettyPrinting().toJson(status, writer);
		} catch (IOException e) {
			// ignore
		}
	}

	@Data
	private static final class MetadataResponse {
		private final long cacheMaxAge;
		private final List<LanguageInfo> languages;
	}


	@Data
	private static final class Contributor {
		private User user;
		private Language[] languages;
		private int translated;
		private int approved;
		private int voted;
		private int positiveVotes;
		private int negativeVotes;
		private int winning;

		@Data
		public static class User {
			private String id;
			private String username;
			private String fullName;
			private String avatarUrl;
			private String joined;
		}

		@Data
		public static class Language {
			private String id;
			private String name;
		}
	}

	@Data
	public static final class LanguageInfo {
		private String id;
		private String name;
		private Locale locale;
		private int progress;
		private List<TranslationRepository.Contributor> contributors;

		public List<String> getContributorsString() {
			return contributors.stream().map(i -> i.user.fullName).collect(Collectors.toList());
		}
	}

	private static final class LimitedInputStream extends FilterInputStream implements Closeable {
		private final long limit;
		private long count;

		public LimitedInputStream(InputStream inputStream, long limit) {
			super(inputStream);
			this.limit = limit;
		}

		@Override
		public int read() throws IOException {
			int res = super.read();
			if (res != -1) {
				this.count++;
				checkLimit();
			}
			return res;
		}

		private void checkLimit() throws IOException {
			if (this.count > this.limit) {
				throw new IOException("Limit exceeded");
			}
		}

		@Override
		public int read(byte @NotNull [] b, int off, int len) throws IOException {
			int res = super.read(b, off, len);
			if (res > 0) {
				this.count += res;
				checkLimit();
			}
			return res;
		}
	}
}
