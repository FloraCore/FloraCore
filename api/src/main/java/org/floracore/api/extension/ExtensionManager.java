package org.floracore.api.extension;

import org.checkerframework.checker.nullness.qual.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Manages extensions.
 */
public interface ExtensionManager {

    /**
     * Loads the given extension.
     *
     * @param extension the extension to load
     */
    void loadExtension(Extension extension);

    /**
     * Loads the extension at the given path.
     *
     * @param path the path to the extension
     * @return the extension
     * @throws java.io.IOException if the extension could not be loaded
     */
    @NonNull Extension loadExtension(Path path) throws IOException;

    /**
     * Gets a collection of all loaded extensions.
     *
     * @return the loaded extensions
     */
    @NonNull @Unmodifiable Collection<Extension> getLoadedExtensions();

}
