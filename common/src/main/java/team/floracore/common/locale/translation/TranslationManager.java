package team.floracore.common.locale.translation;

import com.github.benmanes.caffeine.cache.Cache;
import com.google.common.collect.Maps;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import net.kyori.adventure.translation.Translator;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;
import team.floracore.api.FloraCoreProvider;
import team.floracore.api.data.DataType;
import team.floracore.common.plugin.FloraCorePlugin;
import team.floracore.common.util.CaffeineFactory;
import team.floracore.common.util.MoreFiles;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TranslationManager {
    /**
     * The default locale used by FloraCore messages
     */
    public static final Locale DEFAULT_LOCALE = Locale.SIMPLIFIED_CHINESE;
    public static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder()
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();
    private static final Cache<UUID, String> languageCache = CaffeineFactory.newBuilder()
            .expireAfterWrite(1, TimeUnit.SECONDS)
            .build();
    private final FloraCorePlugin plugin;
    private final Set<Locale> installed = ConcurrentHashMap.newKeySet();
    private final Path translationsDirectory;
    private final Path configTranslationsDirectory;
    private final Path repositoryTranslationsDirectory;
    private final Path customTranslationsDirectory;
    private TranslationRegistry registry;

    public TranslationManager(FloraCorePlugin plugin) {
        this.plugin = plugin;
        this.translationsDirectory = this.plugin.getBootstrap().getConfigDirectory().resolve("translations");
        Path pluginTranslationsDirectory = this.translationsDirectory.resolve("plugin");
        this.repositoryTranslationsDirectory = pluginTranslationsDirectory.resolve("repository");
        this.customTranslationsDirectory = pluginTranslationsDirectory.resolve("custom");
        this.configTranslationsDirectory = this.translationsDirectory.resolve("config");

        try {
            MoreFiles.createDirectoriesIfNotExists(this.repositoryTranslationsDirectory);
            MoreFiles.createDirectoriesIfNotExists(this.customTranslationsDirectory);
            MoreFiles.createDirectoriesIfNotExists(this.configTranslationsDirectory);
        } catch (IOException e) {
            // ignore
        }
    }

    public static Component render(Component component, @Nullable String locale) {
        return render(component, parseLocale(locale));
    }

    public static Component render(Component component, @Nullable Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
            if (locale == null) {
                locale = DEFAULT_LOCALE;
            }
        }
        return GlobalTranslator.render(component, locale);
    }

    public static @Nullable Locale parseLocale(@Nullable String locale) {
        return locale == null ? null : Translator.parseLocale(locale);
    }

    public static Component render(Component component, @NotNull UUID uuid) {
        Locale locale = null;
        String cache = languageCache.getIfPresent(uuid);
        String value = cache;
        if (cache == null) {
            value = FloraCoreProvider.get().getDataAPI().getSpecifiedDataValue(uuid, DataType.FUNCTION, "language");
            if (value != null) {
                languageCache.put(uuid, value);
            }
        }
        if (value != null) {
            locale = parseLocale(value);
        }
        if (locale != null) {
            return render(component, locale);
        }
        return render(component);
    }

    public static Component render(Component component) {
        return render(component, Locale.getDefault());
    }

    public static String localeDisplayName(Locale locale) {
        if (locale.getLanguage().equals("zh")) {
            if (locale.getCountry().equals("CN")) {
                return "简体中文"; // Chinese (Simplified)
            } else if (locale.getCountry().equals("TW")) {
                return "繁體中文"; // Chinese (Traditional)
            }
            return locale.getDisplayCountry(locale) + locale.getDisplayLanguage(locale);
        }

        if (locale.getLanguage().equals("en") && locale.getCountry().equals("PT")) {
            return "Pirate";
        }

        return locale.getDisplayLanguage(locale);
    }

    public static boolean isTranslationFile(Path path) {
        return path.getFileName().toString().endsWith(".properties");
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean isAdventureDuplicatesException(Exception e) {
        return e instanceof IllegalArgumentException && (e.getMessage().startsWith("Invalid key") || e.getMessage()
                .startsWith("Translation already exists"));
    }

    public Path getTranslationsDirectory() {
        return this.translationsDirectory;
    }

    public Path getRepositoryTranslationsDirectory() {
        return this.repositoryTranslationsDirectory;
    }

    public Path getRepositoryStatusFile() {
        return this.repositoryTranslationsDirectory.resolve("status.json");
    }

    public Set<Locale> getInstalledLocales() {
        return Collections.unmodifiableSet(this.installed);
    }

    public void reload() {
        // remove any previous registry
        if (this.registry != null) {
            GlobalTranslator.translator().removeSource(this.registry);
            this.installed.clear();
        }

        // create a translation registry
        this.registry = TranslationRegistry.create(Key.key("floracore", "main"));
        this.registry.defaultLocale(DEFAULT_LOCALE);

        // load custom translations first, then the base (built-in) translations after.
        loadFromFileSystem(this.customTranslationsDirectory, false);
        loadFromFileSystem(this.repositoryTranslationsDirectory, true);
        loadFromResourceBundle();
        loadFromFileSystem(this.configTranslationsDirectory, false);

        // register it to the global source, so our translations can be picked up by adventure-platform
        GlobalTranslator.translator().addSource(this.registry);
    }

    /**
     * Loads custom translations (in any language) from the plugin configuration folder.
     */
    public void loadFromFileSystem(Path directory, boolean suppressDuplicatesError) {
        List<Path> translationFiles;
        try (Stream<Path> stream = Files.list(directory)) {
            translationFiles = stream.filter(TranslationManager::isTranslationFile).collect(Collectors.toList());
        } catch (IOException e) {
            translationFiles = Collections.emptyList();
        }

        if (translationFiles.isEmpty()) {
            return;
        }

        Map<Locale, ResourceBundle> loaded = new HashMap<>();
        for (Path translationFile : translationFiles) {
            try {
                Map.Entry<Locale, ResourceBundle> result = loadTranslationFile(translationFile);
                loaded.put(result.getKey(), result.getValue());
            } catch (Exception e) {
                if (!suppressDuplicatesError || !isAdventureDuplicatesException(e)) {
                    this.plugin.getLogger().warn("Error loading locale file: " + translationFile.getFileName(), e);
                }
            }
        }

        // try registering the locale without a country code - if we don't already have a registration for that
        loaded.forEach((locale, bundle) -> {
            Locale localeWithoutCountry = new Locale(locale.getLanguage());
            if (!locale.equals(localeWithoutCountry) && !localeWithoutCountry.equals(DEFAULT_LOCALE) && this.installed.add(
                    localeWithoutCountry)) {
                try {
                    this.registry.registerAll(localeWithoutCountry, bundle, false);
                } catch (IllegalArgumentException e) {
                    // ignore
                }
            }
        });
    }

    /**
     * Loads the base (English) translations from the jar file.
     */
    private void loadFromResourceBundle() {
        ResourceBundle bundle = ResourceBundle.getBundle("floracore", DEFAULT_LOCALE, UTF8ResourceBundleControl.get());
        loadFromResourceBundle(bundle, DEFAULT_LOCALE);
    }

    public void loadFromResourceBundle(ResourceBundle bundle, Locale locale) {
        try {
            this.registry.registerAll(locale, bundle, false);
        } catch (IllegalArgumentException e) {
            if (!isAdventureDuplicatesException(e)) {
                this.plugin.getLogger().warn("Error loading default locale file", e);
            }
        }
    }

    private Map.Entry<Locale, ResourceBundle> loadTranslationFile(Path translationFile) throws IOException {
        String fileName = translationFile.getFileName().toString();
        String localeString = fileName.substring(0, fileName.length() - ".properties".length());
        Locale locale = parseLocale(localeString);

        if (locale == null) {
            throw new IllegalStateException("Unknown locale '" + localeString + "' - unable to register.");
        }

        PropertyResourceBundle bundle;
        try (BufferedReader reader = Files.newBufferedReader(translationFile, StandardCharsets.UTF_8)) {
            bundle = new PropertyResourceBundle(reader);
        }

        this.registry.registerAll(locale, bundle, false);
        this.installed.add(locale);
        return Maps.immutableEntry(locale, bundle);
    }
}
