package team.floracore.common.script;

import lombok.Getter;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.NativeJavaClass;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.tools.shell.Environment;
import org.mozilla.javascript.tools.shell.Global;
import team.floracore.common.plugin.FloraCorePlugin;
import team.floracore.common.util.MoreFiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Locale;

@Getter
public class ScriptLoader {
	private static boolean sawSecurityException;
	private final FloraCorePlugin plugin;
	public Context context;
	public Scriptable scope;

	public ScriptLoader(FloraCorePlugin plugin) {
		this.plugin = plugin;
		init();
	}

	public static void loadJsFiles(final Context ctx, final Scriptable scope, final File dir) throws IOException {
		if (!dir.isDirectory()) {
			dir.getAbsoluteFile().mkdirs();
		}
		for (final File i : dir.listFiles()) {
			if (!i.isFile()) {
				if (i.isDirectory()) {
					loadJsFiles(ctx, scope, i);
				}
			} else {
				if (i.getName().toLowerCase(Locale.ENGLISH).endsWith(".js".toLowerCase(Locale.ENGLISH))) {
					loadJsFile(ctx, scope, i);
				}
			}
		}
	}

	public static void loadJsFile(final Context ctx, final Scriptable scope, final File f) throws IOException {
		if (f.isFile()) {
			ctx.evaluateString(scope, readTextFile(f), f.getAbsoluteFile().toString(), 1, null);
		}
	}

	public static void loadJsString(final Context ctx, final Scriptable scope, final String source, final String js) {
		ctx.evaluateString(scope, js, source, 1, null);
	}

	public static byte[] readFile(final File f) throws IOException {
		try (FileInputStream i = new FileInputStream(f)) {
			final byte[] buf = new byte[i.available()];
			i.read(buf);
			return buf;
		}
	}

	public static String readTextFile(final File f) throws IOException {
		return new String(readFile(f), StandardCharsets.UTF_8);
	}

	static Method[] getMethodList(final Class<?> clazz) {
		Method[] methods = null;
		try {
			if (!sawSecurityException) {
				methods = clazz.getDeclaredMethods();
			}
		} catch (final SecurityException e) {
			sawSecurityException = true;
		}
		if (methods == null) {
			methods = clazz.getMethods();
		}
		int count = 0;
		for (int i = 0; i < methods.length; i++) {
			if (sawSecurityException ? methods[i].getDeclaringClass() != clazz
					: !Modifier.isPublic(methods[i].getModifiers())) {
				methods[i] = null;
			} else {
				count++;
			}
		}
		final Method[] result = new Method[count];
		int j = 0;
		for (final Method method : methods) {
			if (method != null) {
				result[(j++)] = method;
			}
		}
		return result;
	}

	static Method findSingleMethod(final Method[] methods, final String name) {
		Method found = null;
		int i = 0;
		for (final int N = methods.length; i != N; i++) {
			final Method method = methods[i];
			if ((method != null) && (name.equals(method.getName()))) {
				if (found != null) {
					throw new RuntimeException("overload methof found! dir: " + name + " class: " + method

							.getDeclaringClass().getName());
				}
				found = method;
			}
		}
		return found;
	}

	public static Object invokeJsFunction(final Function f, final Context ctx, final Scriptable scope,
	                                      final Object... args) {
		return f.call(ctx, f, scope, args);
	}

	public void init() {
		try {
			this.context = Context.enter();
			this.context.setOptimizationLevel(-1);
			this.context.setLanguageVersion(Context.VERSION_ES6);
			this.scope = new ImporterTopLevel(this.context);
			this.scope.put("FloraCorePlugin", this.scope, new NativeJavaClass(this.scope, FloraCorePlugin.class));
			final String[] names = new String[]{"defineClass", "deserialize", "doctest", "gc", "help", "load", "loadClass",
					"print", "quit", "readline", "readFile", "readUrl", "runCommand", "seal", "serialize", "spawn", "sync",
					"toint32", "version", "write"};
			this.defineFunctionProperties(names, Global.class, 2);
			this.scope.put("environment", this.scope, new Environment((ScriptableObject) this.scope));
		} catch (final Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public void loadPluginScript(Path directory) {
		try {
			MoreFiles.createDirectoriesIfNotExists(directory);
			loadJsFiles(this.context, this.scope, directory.toFile());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void defineFunctionProperties(final String[] names, final Class<?> clazz, final int attributes) {
		final Method[] methods = getMethodList(clazz);
		for (final String name : names) {
			final Method m = findSingleMethod(methods, name);
			if (m == null) {
				throw new RuntimeException("method \"" + name + "\" cannot be found in class\"" + clazz.getName() + "\"");
			}
			final FunctionObject f = new FunctionObject(name, m, this.scope);
			this.scope.put(name, this.scope, f);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T tryInvokeFunction(final String name, final Object... args) {
		final Object f = this.scope.get(name, this.scope);
		if (f instanceof Function) {
			return (T) invokeJsFunction((Function) f, this.context, this.scope, args);
		}
		return null;
	}

}

