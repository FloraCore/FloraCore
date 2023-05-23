package team.floracore.bukkit.util.module;

import com.google.common.collect.*;
import team.floracore.bukkit.*;

public class RegistrarRegistrar extends AbsModule implements IRegistrar<IRegistrar> {
	public static RegistrarRegistrar instance = new RegistrarRegistrar();

	public RegistrarRegistrar() {
		super(FCBukkitBootstrap.loader);
	}

	@Override
	public Class<IRegistrar> getType() {
		return IRegistrar.class;
	}

	@Override
	public boolean register(IRegistrar obj) {
		IRegistrar.registers.add(obj);
		return true;
	}

	@Override
	public void unregister(IRegistrar obj) {
		IRegistrar.registers.remove(obj);
	}

	@Override
	public void onEnable() {
		register(this);
		this.getRegisteredObjects().put(RegistrarRegistrar.instance, Lists.newLinkedList(Lists.newArrayList(this)));
	}

	@Override
	public void reg(Object obj) {
		if (obj == this)
			return;
		super.reg(obj);
	}
}
