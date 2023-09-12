package team.floracore.common.locale.data;

import com.ice.tar.TarEntry;
import com.ice.tar.TarInputStream;
import com.maxmind.geoip2.DatabaseReader;
import lombok.Getter;
import team.floracore.common.config.impl.geoip.GeoIPConfiguration;
import team.floracore.common.config.impl.geoip.GeoIPKeys;
import team.floracore.common.plugin.FloraCorePlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.zip.GZIPInputStream;

@Getter
public class GeoIPManager {
	private final FloraCorePlugin plugin;
	private File databaseFile;
	// initialize maxmind geoip2 reader
	@Getter
	private DatabaseReader databaseReader = null;

	public GeoIPManager(FloraCorePlugin plugin) {
		this.plugin = plugin;
		reload();
	}

	public void reload() {
		GeoIPConfiguration geoIPConfiguration = plugin.getGeoIPConfiguration();
		if (geoIPConfiguration.get(GeoIPKeys.DATABASE_SHOW_CITIES)) {
			databaseFile = new File(plugin.getDataManager().getDataDirectory().toFile(), "GeoIP2-City.mmdb");
		} else {
			databaseFile = new File(plugin.getDataManager().getDataDirectory().toFile(), "GeoIP2-Country.mmdb");
		}
		if (!databaseFile.exists()) {
			if (geoIPConfiguration.get(GeoIPKeys.DATABASE_DOWNLOAD_IF_MISSING)) {
				downloadDatabase();
			} else {
				plugin.getLogger().warn("Can't find GeoIP database!");
				return;
			}
		} else if (geoIPConfiguration.get(GeoIPKeys.DATABASE_UPDATE_ENABLE)) {
			// try to update expired mmdb files
			final long diff = new Date().getTime() - databaseFile.lastModified();
			if (diff / 24 / 3600 / 1000 > geoIPConfiguration.get(GeoIPKeys.DATABASE_UPDATE_BY_EVERY_X_DAYS)) {
				downloadDatabase();
			}
		}
		try {
			// locale setting
			if (geoIPConfiguration.get(GeoIPKeys.ENABLE_LOCALE)) {
				// Get geolocation based on Essentials' locale. If the locale is not available, use "en".
				String locale = Locale.getDefault().toString().replace('_', '-');
				// This fixes an inconsistency where Essentials uses "zh" but MaxMind expects "zh-CN".
				if ("zh".equalsIgnoreCase(locale)) {
					locale = "zh-CN";
				}
				databaseReader = new DatabaseReader.Builder(databaseFile).locales(Arrays.asList(locale, "en")).build();
			} else {
				databaseReader = new DatabaseReader.Builder(databaseFile).build();
			}
		} catch (final IOException ex) {
			plugin.getLogger().warn("Failed to read GeoIP database!", ex);
		}
	}

	private void downloadDatabase() {
		try {
			GeoIPConfiguration geoIPConfiguration = plugin.getGeoIPConfiguration();
			String url;
			if (geoIPConfiguration.get(GeoIPKeys.DATABASE_SHOW_CITIES)) {
				url = geoIPConfiguration.get(GeoIPKeys.DATABASE_DOWNLOAD_URL_CITY);
			} else {
				url = geoIPConfiguration.get(GeoIPKeys.DATABASE_DOWNLOAD_URL);
			}
			if (url == null || url.isEmpty()) {
				plugin.getLogger().warn("The GeoIP download link is empty.");
				return;
			}
			final String licenseKey = geoIPConfiguration.get(GeoIPKeys.DATABASE_LICENSE_KEY);
			if (licenseKey == null || licenseKey.isEmpty()) {
				plugin.getLogger().warn("License key not found!");
				return;
			}
			url = url.replace("{LICENSEKEY}", licenseKey);
			plugin.getLogger().info("Download GeoIP database... this may take a while (country: 1.7MB, city: 30MB)");

			final URL downloadUrl = new URL(url);
			final URLConnection conn = downloadUrl.openConnection();
			conn.setConnectTimeout(10000);
			conn.connect();
			InputStream input = conn.getInputStream();
			final OutputStream output = Files.newOutputStream(databaseFile.toPath());
			final byte[] buffer = new byte[2048];
			if (url.contains("gz")) {
				input = new GZIPInputStream(input);
				if (url.contains("tar.gz")) {
					// The new GeoIP2 uses tar.gz to pack the db file along with some other txt. So it makes things a bit complicated here.
					String filename;
					final TarInputStream tarInputStream = new TarInputStream(input);
					TarEntry entry;
					while ((entry = tarInputStream.getNextEntry()) != null) {
						if (!entry.isDirectory()) {
							filename = entry.getName();
							if (filename.substring(filename.length() - 5).equalsIgnoreCase(".mmdb")) {
								input = tarInputStream;
								break;
							}
						}
					}
				}
			}
			int length = input.read(buffer);
			while (length >= 0) {
				output.write(buffer, 0, length);
				length = input.read(buffer);
			}
			output.close();
			input.close();
		} catch (final MalformedURLException ex) {
			plugin.getLogger().warn("Invalid GeoIP download link.", ex);
		} catch (final IOException ex) {
			plugin.getLogger().warn("The connection could not be established.", ex);
		}
	}

	private boolean checkIfLocal(final InetAddress address) {
		if (address.isAnyLocalAddress() || address.isLoopbackAddress()) {
			return true;
		}

		// Double checks if address is defined on any interface
		try {
			return NetworkInterface.getByInetAddress(address) != null;
		} catch (final SocketException e) {
			return false;
		}
	}
}
