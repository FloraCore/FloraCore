package team.floracore.bukkit.util.wrapper;

import team.floracore.common.util.wrapper.WrappedClass;
import team.floracore.common.util.wrapper.WrappedConstructor;
import team.floracore.common.util.wrapper.WrappedFieldAccessor;
import team.floracore.common.util.wrapper.WrappedMethod;
import team.floracore.common.util.wrapper.WrappedObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

@WrappedClass("java.lang.Object")
public interface WrappedBukkitObject extends WrappedObject {
	@Override
	default Member getRawMember(Class<?> rawClass, Method m, Annotation a) {
		if (a instanceof WrappedBukkitFieldAccessor) {
			String[] name = BukkitWrapper.inVersion(((WrappedBukkitFieldAccessor) a).value());
			if (name.length > 0) {
				return WrappedObject.super.getRawMember(rawClass, m, new WrappedFieldAccessor() {
					public Class<? extends Annotation> annotationType() {
						return WrappedFieldAccessor.class;
					}

					public String[] value() {
						return name;
					}
				});
			} else {
				return null;
			}
		} else if (a instanceof WrappedBukkitConstructor) {
			if (BukkitWrapper.inVersion((WrappedBukkitConstructor) a)) {
				return WrappedObject.super.getRawMember(rawClass, m, new WrappedConstructor() {
					public Class<? extends Annotation> annotationType() {
						return WrappedConstructor.class;
					}
				});
			} else {
				return null;
			}
		} else if (a instanceof WrappedBukkitMethod) {
			String[] name = BukkitWrapper.inVersion(((WrappedBukkitMethod) a).value());
			if (name.length > 0) {
				return WrappedObject.super.getRawMember(rawClass, m, new WrappedMethod() {
					public Class<? extends Annotation> annotationType() {
						return WrappedMethod.class;
					}

					public String[] value() {
						return name;
					}
				});
			} else {
				return null;
			}
		}
		return WrappedObject.super.getRawMember(rawClass, m, a);
	}

	@Override
	default Class<?> getAnnotationClass(Class<? extends WrappedObject> wrapper) {
		WrappedBukkitClass a = wrapper.getDeclaredAnnotation(WrappedBukkitClass.class);
		if (a != null) {
			for (String n : BukkitWrapper.cov(BukkitWrapper.inVersion(a.value()))) {
				try {
					return Class.forName(n, false, this.getClass().getClassLoader());
				} catch (ClassNotFoundException ignored) {
				}
			}
		}
		return WrappedObject.super.getAnnotationClass(wrapper);
	}
}
