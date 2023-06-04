package team.floracore.common.locale.data;

import team.floracore.common.plugin.FloraCorePlugin;
import team.floracore.common.util.MoreFiles;

import java.io.IOException;
import java.nio.file.Path;

public class DataManager {
	private final FloraCorePlugin plugin;
	private final Path dataDirectory;
	private final NamesRepository namesRepository;

	public DataManager(FloraCorePlugin plugin) {
		this.plugin = plugin;
		this.dataDirectory = this.plugin.getBootstrap().getConfigDirectory().resolve("data");

		try {
			MoreFiles.createDirectoriesIfNotExists(this.dataDirectory);
		} catch (IOException e) {
			// ignore
		}

		this.namesRepository = new NamesRepository(plugin);
	}

	public Path getDataDirectory() {
		return dataDirectory;
	}

	public NamesRepository getNamesRepository() {
		return namesRepository;
	}

	public void reload() {

	}
}
