package team.floracore.bukkit.util.wrappednms;

import io.github.karlatemp.unsafeaccessor.*;
import team.floracore.bukkit.util.*;
import team.floracore.bukkit.util.wrapper.*;
import team.floracore.common.util.*;
import team.floracore.common.util.wrapper.*;

import java.util.*;

@WrappedBukkitClass({@VersionName(value = "nms.LocaleLanguage", maxVer = 17), @VersionName(value = "net.minecraft.locale.LocaleLanguage", minVer = 17)})
public interface NmsLocaleLanguage extends WrappedBukkitObject {
    static NmsLocaleLanguage newInstanceV_13(Map<String, String> map) {
        NmsLocaleLanguage r;
        try {
            r = WrappedObject.wrap(NmsLocaleLanguage.class, Root.getUnsafe().allocateInstance(WrappedObject.getRawClass(NmsLocaleLanguage.class)));
        } catch (Throwable e) {
            throw TypeUtil.throwException(e);
        }
        return r.setMapV_13(map);
    }

    @WrappedBukkitFieldAccessor(@VersionName(maxVer = 13, value = "d"))
    Map<String, String> getMapV_13();

    @WrappedBukkitFieldAccessor(@VersionName(maxVer = 13, value = "d"))
    NmsLocaleLanguage setMapV_13(Map<String, String> map);
}
