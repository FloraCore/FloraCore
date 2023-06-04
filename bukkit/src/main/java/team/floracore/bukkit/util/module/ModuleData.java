package team.floracore.bukkit.util.module;

import org.bukkit.plugin.Plugin;

public class ModuleData extends AbsModule {
	public ModuleData(Plugin plugin, IModule... depends) {
		super(plugin, depends);
	}
}
