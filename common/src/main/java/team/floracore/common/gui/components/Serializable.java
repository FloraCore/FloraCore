package team.floracore.common.gui.components;

import org.jetbrains.annotations.*;

import java.util.*;

public interface Serializable {

    List<String> encodeGui();

    void decodeGui(@NotNull final List<String> gui);

}
