package team.floracore.common.locale.data;

import lombok.Getter;
import team.floracore.common.config.impl.geoip.GeoIPKeys;
import team.floracore.common.plugin.FloraCorePlugin;
import team.floracore.common.util.MoreFiles;

import java.io.IOException;
import java.nio.file.Path;

public class DataManager {
	private final FloraCorePlugin plugin;
	@Getter
	private final Path dataDirectory;
	@Getter
	private GeoIPManager geoIPManager;

	public DataManager(FloraCorePlugin plugin) {
		this.plugin = plugin;
		this.dataDirectory = this.plugin.getBootstrap().getConfigDirectory().resolve("data");

		try {
			MoreFiles.createDirectoriesIfNotExists(this.dataDirectory);
		} catch (IOException e) {
			// ignore
		}
	}

	public void onLoad() {

	}

	public void onEnable() {
		if (plugin.getGeoIPConfiguration().get(GeoIPKeys.ENABLE)) {
			this.geoIPManager = new GeoIPManager(plugin);
		}
	}

	public void onDisable() {

	}
}
