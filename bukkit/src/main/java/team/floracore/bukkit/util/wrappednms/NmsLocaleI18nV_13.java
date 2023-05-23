package team.floracore.bukkit.util.wrappednms;


import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrapper.*;
import team.floracore.common.util.wrapper.*;

@WrappedBukkitClass({@VersionName(value = "nms.LocaleI18n", maxVer = 13)})
public interface NmsLocaleI18nV_13 extends WrappedBukkitObject {
    static NmsLocaleLanguage getLang() {
        return WrappedObject.getStatic(NmsLocaleI18nV_13.class).staticGetLang();
    }

    static void setLang(NmsLocaleLanguage lang) {
        WrappedObject.getStatic(NmsLocaleI18nV_13.class).staticSetLang(lang);
    }

    @WrappedBukkitFieldAccessor(value = @VersionName(maxVer = 13, value = "a"))
    NmsLocaleI18nV_13 staticSetLang(NmsLocaleLanguage lang);

    @WrappedBukkitFieldAccessor(value = @VersionName(maxVer = 13, value = "a"))
    NmsLocaleLanguage staticGetLang();
}
