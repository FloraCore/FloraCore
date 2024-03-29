package team.floracore.bukkit.util.wrappednms;

import com.google.gson.JsonArray;
import team.floracore.bukkit.util.VersionName;
import team.floracore.bukkit.util.wrapper.BukkitWrapper;
import team.floracore.bukkit.util.wrapper.WrappedBukkitClass;
import team.floracore.bukkit.util.wrapper.WrappedBukkitFieldAccessor;
import team.floracore.bukkit.util.wrapper.WrappedBukkitMethod;
import team.floracore.common.util.wrapper.WrappedConstructor;
import team.floracore.common.util.wrapper.WrappedMethod;
import team.floracore.common.util.wrapper.WrappedObject;

import java.util.AbstractList;
import java.util.Collection;
import java.util.List;

@WrappedBukkitClass({@VersionName(value = "nms.NBTTagList", maxVer = 17),
		@VersionName(value = "net.minecraft.nbt.NBTTagList", minVer = 17)})
public interface NmsNBTTagList extends NmsNBTTag {
	static <T extends NmsNBTBase> NmsNBTTagList newInstance(List<T> l) {
		NmsNBTTagList r = newInstance();
		l.forEach(r::add);
		return r;
	}

	static NmsNBTTagList newInstance() {
		return WrappedObject.getStatic(NmsNBTTagList.class).staticNewInstance();
	}

	static <T extends NmsNBTBase> NmsNBTTagList newInstance(T... l) {
		NmsNBTTagList r = newInstance();
		for (T i : l) {
			r.add(i);
		}
		return r;
	}

	static NmsNBTTagList wrapValues(List<?> values) {
		if (values == null) {
			return null;
		}
		NmsNBTTagList r = NmsNBTTagList.newInstance();
		values.forEach(v ->
		{
			r.add(NmsNBTTag.wrapValue(v));
		});
		return r;
	}

	default boolean add(NmsNBTBase nbt) {
		if (BukkitWrapper.version >= 14) {
			addV14(nbt);
		} else {
			addV_14(nbt);
		}
		return true;
	}

	@WrappedConstructor
	NmsNBTTagList staticNewInstance();

	default boolean addV14(NmsNBTBase nbt) {
		addV14(size(), nbt);
		return true;
	}

	@WrappedBukkitMethod(@VersionName(value = "add", maxVer = 14))
	void addV_14(NmsNBTBase nbt);

	@WrappedBukkitMethod({@VersionName(value = "add", minVer = 14), @VersionName(minVer = 14, value = "c")})
	void addV14(int i, NmsNBTBase nbt);

	@WrappedMethod("size")
	int size();

	default <T extends NmsNBTBase> NmsNBTTagList addAll(Collection<T> nbt) {
		nbt.forEach(n -> add(n));
		return this;
	}

	default List<NmsNBTBase> values() {
		return new AbstractList<NmsNBTBase>() {
			@Override
			public boolean add(NmsNBTBase e) {
				return NmsNBTTagList.this.add(e);
			}

			@Override
			public NmsNBTBase get(int index) {
				return NmsNBTTagList.this.get(index);
			}

			@Override
			public NmsNBTBase set(int index, NmsNBTBase element) {
				NmsNBTBase last = get(index);
				NmsNBTTagList.this.set(index, element);
				return last;
			}

			@Override
			public void add(int index, NmsNBTBase element) {
				if (BukkitWrapper.v13) {
					NmsNBTTagList.this.addV14(index, element);
				} else {
					throw new UnsupportedOperationException();
				}
			}

			@Override
			public NmsNBTBase remove(int index) {
				return NmsNBTTagList.this.remove(index);
			}

			@Override
			public int size() {
				return NmsNBTTagList.this.size();
			}

			@Override
			public boolean remove(Object o) {
				return NmsNBTTagList.this.remove((NmsNBTBase) o);
			}
		};
	}

	@WrappedBukkitMethod({@VersionName(minVer = 8, maxVer = 12, value = "g"),
			@VersionName(minVer = 12, maxVer = 13, value = "i"),
			@VersionName(value = "get", minVer = 13, maxVer = 18),
			@VersionName(minVer = 18, value = "k")})
	NmsNBTBase get(int index);

	default void set(int index, NmsNBTBase nbt) {
		if (BukkitWrapper.v13) {
			setV13(index, nbt);
		} else {
			setV_13(index, nbt);
		}
	}

	@WrappedBukkitMethod({@VersionName(minVer = 8, value = "a"),
			@VersionName(minVer = 12, value = "remove"),
			@VersionName(minVer = 18, value = "c")})
	NmsNBTBase remove(int index);

	default boolean remove(NmsNBTBase nbt) {
		return getList().remove(nbt.getRaw());
	}

	@WrappedBukkitMethod({@VersionName(minVer = 13, value = "set"), @VersionName(minVer = 18, value = "d")})
	NmsNBTBase setV13(int index, NmsNBTBase nbt);

	@WrappedBukkitMethod(@VersionName(maxVer = 13, value = "a"))
	void setV_13(int index, NmsNBTBase nbt);

	@Deprecated
	@WrappedBukkitFieldAccessor({@VersionName("list"), @VersionName(minVer = 17, value = "c")})
	List<Object> getList();

	default <T extends NmsNBTBase> List<T> values(Class<T> wrapper) {
		return new AbstractList<T>() {
			@Override
			public boolean add(T e) {
				return NmsNBTTagList.this.add(e);
			}

			@Override
			public T get(int index) {
				return NmsNBTTagList.this.get(index, wrapper);
			}

			@Override
			public T set(int index, T element) {
				T last = get(index);
				NmsNBTTagList.this.set(index, element);
				return last;
			}

			@Override
			public void add(int index, T element) {
				if (BukkitWrapper.v13) {
					NmsNBTTagList.this.addV14(index, element);
				} else if (index == size()) {
					NmsNBTTagList.this.addV_14(element);
				} else {
					throw new UnsupportedOperationException();
				}
			}

			@Override
			public T remove(int index) {
				return NmsNBTTagList.this.remove(index, wrapper);
			}

			@Override
			public int size() {
				return NmsNBTTagList.this.size();
			}

			@Override
			public boolean remove(Object o) {
				return NmsNBTTagList.this.remove((NmsNBTBase) o);
			}
		};
	}

	default <T extends NmsNBTBase> T get(int index, Class<T> type) {
		return get(index).cast(type);
	}

	default <T extends WrappedObject> T remove(int index, Class<T> type) {
		return WrappedObject.wrap(type, remove(index).getRaw());
	}

	default List<String> wrapStringList() {
		return new AbstractList<String>() {
			@Override
			public boolean add(String e) {
				return NmsNBTTagList.this.add(NmsNBTTagString.newInstance(e));
			}

			@Override
			public String get(int index) {
				return NmsNBTTagList.this.get(index, NmsNBTTagString.class).getValue();
			}

			@Override
			public String set(int index, String element) {
				String last = get(index);
				NmsNBTTagList.this.set(index, NmsNBTTagString.newInstance(element));
				return last;
			}

			@Override
			public void add(int index, String element) {
				if (BukkitWrapper.v13) {
					NmsNBTTagList.this.addV14(index, NmsNBTTagString.newInstance(element));
				} else {
					throw new UnsupportedOperationException();
				}
			}

			@Override
			public String remove(int index) {
				return NmsNBTTagList.this.remove(index, NmsNBTTagString.class).getValue();
			}

			@Override
			public int size() {
				return NmsNBTTagList.this.size();
			}

			@Override
			public boolean remove(Object o) {
				return NmsNBTTagList.this.remove(NmsNBTTagString.newInstance((String) o));
			}
		};
	}

	@Override
	default JsonArray toJson() {
		JsonArray r = new JsonArray();
		this.getList().forEach(nmsNbt ->
		{
			r.add(NmsNBTBase.wrap(nmsNbt).toJson());
		});
		return r;
	}
}
