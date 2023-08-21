package team.floracore.common.dependencies;

import team.floracore.common.storage.StorageType;

import java.util.Set;

/**
 * Loads and manages runtime dependencies for the plugin.
 */
public interface DependencyManager extends AutoCloseable {

	/**
	 * Loads dependencies.
	 *
	 * @param dependencies the dependencies to load
	 */
	void loadDependencies(Set<Dependency> dependencies);

	/**
	 * Loads storage dependencies.
	 *
	 * @param storageTypes the storage types in use
	 * @param redis        if redis is being used
	 */
	void loadStorageDependencies(Set<StorageType> storageTypes, boolean redis);

	/**
	 * Obtains an isolated classloader containing the given dependencies.
	 *
	 * @param dependencies the dependencies
	 * @return the classloader
	 */
	ClassLoader obtainClassLoaderWith(Set<Dependency> dependencies);

	@Override
	void close();
}
