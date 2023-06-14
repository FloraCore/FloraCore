package team.floracore.common.api.implementation;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import team.floracore.api.translation.TranslationAPI;
import team.floracore.common.locale.translation.TranslationManager;
import team.floracore.common.plugin.FloraCorePlugin;
import team.floracore.common.sender.Sender;

import java.nio.file.Path;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.UUID;

/**
 * 国际化多语言API的实现类
 *
 * @author xLikeWATCHDOG
 */
public class ApiTranslation implements TranslationAPI {

    private final FloraCorePlugin plugin;

    public ApiTranslation(FloraCorePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void loadCustomLanguageFile(Path directory, boolean suppressDuplicatesError) {
        plugin.getTranslationManager().loadFromFileSystem(directory, suppressDuplicatesError);
    }

    @Override
    public void loadFromResourceBundle(ResourceBundle bundle, Locale locale) {
        plugin.getTranslationManager().loadFromResourceBundle(bundle, locale);
    }

    @Override
    public void sendMessage(@NotNull Object sender, @NotNull Component component) {
        Sender s = (Sender) sender;
        s.sendMessage(component);
    }

    @Override
    public void sendConsoleMessage(@NotNull Component component) {
        plugin.getConsoleSender().sendMessage(component);
    }

    @Override
    public Component render(Component component, @NotNull UUID uuid) {
        return TranslationManager.render(component, uuid);
    }
}
