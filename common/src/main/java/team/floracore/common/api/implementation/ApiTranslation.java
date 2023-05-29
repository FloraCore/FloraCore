package team.floracore.common.api.implementation;

import net.kyori.adventure.text.*;
import org.floracore.api.translation.*;
import org.jetbrains.annotations.*;
import team.floracore.common.locale.translation.*;
import team.floracore.common.plugin.*;
import team.floracore.common.sender.*;

import java.nio.file.*;
import java.util.*;

/**
 * 国际化多语言API的实现类
 *
 * @author xLikeWATCHDOG
 * @date 2023/5/29 18:07
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
