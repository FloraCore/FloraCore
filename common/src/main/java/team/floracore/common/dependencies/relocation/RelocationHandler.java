package team.floracore.common.dependencies.relocation;

import team.floracore.lib.jarrelocator.JarRelocator;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles class runtime relocation of packages in downloaded dependencies
 */
public class RelocationHandler {
    public RelocationHandler() {
    }

    public void remap(Path input, Path output, List<Relocation> relocations) throws Exception {
        Map<String, String> mappings = new HashMap<>();
        for (Relocation relocation : relocations) {
            mappings.put(relocation.getPattern(), relocation.getRelocatedPattern());
        }

        // create and invoke a new relocator
        JarRelocator jarRelocator = new JarRelocator(input.toFile(), output.toFile(), mappings);
        jarRelocator.run();
    }

}
