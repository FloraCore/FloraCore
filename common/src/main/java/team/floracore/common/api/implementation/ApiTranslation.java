package team.floracore.common.api.implementation;

import org.floracore.api.translation.*;
import team.floracore.common.plugin.*;

import java.nio.file.*;

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
}
