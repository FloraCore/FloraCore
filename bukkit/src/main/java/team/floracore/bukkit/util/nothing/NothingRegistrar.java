package team.floracore.bukkit.util.nothing;

import team.floracore.bukkit.FCBukkitBootstrap;
import team.floracore.bukkit.util.module.AbsModule;
import team.floracore.bukkit.util.module.IRegistrar;
import team.floracore.bukkit.util.module.RegistrarRegistrar;
import team.floracore.bukkit.util.wrappednms.NmsEntity;
import team.floracore.bukkit.util.wrappednms.NmsNetworkManager;
import team.floracore.common.util.TypeUtil;
import team.floracore.common.util.nothing.Nothing;

public class NothingRegistrar extends AbsModule implements IRegistrar<Class<? extends Nothing>> {
	public static NothingRegistrar instance = new NothingRegistrar();

	public NothingRegistrar() {
		super(FCBukkitBootstrap.loader, RegistrarRegistrar.instance);
	}

	@Override
	public Class<Class<? extends Nothing>> getType() {
		return TypeUtil.cast(Class.class);
	}

	@Override
	public boolean register(Class<? extends Nothing> obj) {
		if (Nothing.class.isAssignableFrom(obj)) {
			Nothing.install(TypeUtil.cast(obj));
			return true;
		}
		return false;
	}

	@Override
	public void unregister(Class<? extends Nothing> obj) {
		Nothing.uninstall(TypeUtil.cast(obj));
	}

	@Override
	public void onEnable() {
		Nothing.init();

		reg(NmsEntity.class);
		// reg(NmsEntityFishingHook.class);
		reg(NmsNetworkManager.class);
		// reg(NmsRecipeItemStack.class);
	}

	@Override
	public void onDisable() {
		Nothing.uninstallAll();
	}
}
