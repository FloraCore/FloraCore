package team.floracore.common.util;


import team.floracore.common.util.wrapper.*;

@WrappedClass("java.lang.ClassLoader")
public interface WrappedClassLoader extends WrappedObject {
	@WrappedMethod(value = "findLoadedClass")
	<T> Class<T> findLoadedClass(String name);

	@WrappedMethod(value = "findClass")
	<T> Class<T> findClass(String name);

	@Override
	ClassLoader getRaw();
}
