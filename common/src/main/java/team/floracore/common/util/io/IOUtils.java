package team.floracore.common.util.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.floracore.common.util.object.IExceptionHandler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public abstract class IOUtils {
	private IOUtils() {
	}

	/**
	 * 静默关闭可关闭的对象，无视一切异常
	 *
	 * @param acs 可关闭的对象
	 */
	public static void closeQuietly(@Nullable AutoCloseable... acs) {
		for (AutoCloseable ac : acs) {
			if (ac != null) {
				try {
					ac.close();
				} catch (Exception ignored) {
				}
			}
		}
	}

	/**
	 * 将输入流的数据全部写入输出流
	 *
	 * @param in      输入流
	 * @param out     输出流
	 * @param options 选项
	 * @throws IOException 当出现异常时抛出
	 */
	public static void copy(@NotNull InputStream in, @NotNull OutputStream out, @NotNull CopyOption... options) throws IOException {
		Objects.requireNonNull(in);
		Objects.requireNonNull(out);
		List<CopyOption> list = Arrays.asList(options);
		int len;
		byte[] bytes = new byte[1024];
		try {
			while ((len = in.read(bytes)) != -1) {
				out.write(bytes, 0, len);
			}
		} finally {
			if (list.contains(CopyOption.CLOSE_INPUT)) {
				closeQuietly(in);
			}
			if (list.contains(CopyOption.CLOSE_OUTPUT)) {
				closeQuietly(out);
			}
		}
	}

	/**
	 * 将输入流的数据全部写入输出流，并在抛出异常时交由异常处理器处理
	 *
	 * @param in               输入流
	 * @param out              输出流
	 * @param exceptionHandler 异常处理器，可为空
	 * @param options          选项
	 * @return 若本次执行没有出现异常，返回true
	 */
	public static boolean copy(@NotNull InputStream in, @NotNull OutputStream out, @Nullable IExceptionHandler<IOException> exceptionHandler, @NotNull CopyOption... options) {
		Objects.requireNonNull(in);
		Objects.requireNonNull(out);
		try {
			copy(in, out, options);
			return true;
		} catch (IOException e) {
			if (exceptionHandler != null) {
				exceptionHandler.accept(e);
			}
			return false;
		}
	}

	/**
	 * 将输入文件的数据全部写入输出流
	 *
	 * @param in      输入文件
	 * @param out     输出流
	 * @param options 选项
	 * @throws IOException 当出现异常时抛出
	 */
	public static void copy(@NotNull Path in, @NotNull OutputStream out, @NotNull CopyOption... options) throws IOException {
		Objects.requireNonNull(in);
		Objects.requireNonNull(out);
		InputStream i = null;
		try {
			i = Files.newInputStream(in);
			copy(i, out, options);
		} finally {
			closeQuietly(i);
		}
	}

	/**
	 * 将输入文件的数据全部写入输出流，并将异常交由异常处理器处理
	 *
	 * @param in               输入文件
	 * @param out              输出流
	 * @param exceptionHandler 异常处理器
	 * @param options          选项
	 * @return 若本次执行没有出现异常，返回true
	 */
	public static boolean copy(@NotNull Path in, @NotNull OutputStream out, @Nullable IExceptionHandler<IOException> exceptionHandler, @NotNull CopyOption... options) {
		Objects.requireNonNull(in);
		Objects.requireNonNull(out);
		try {
			copy(in, out, options);
			return true;
		} catch (IOException e) {
			if (exceptionHandler != null) {
				exceptionHandler.accept(e);
			}
			return false;
		}
	}

	/**
	 * 将输入文件的数据全部写入输出流
	 *
	 * @param in      输入文件
	 * @param out     输出流
	 * @param options 选项
	 * @throws IOException 当出现异常时抛出
	 */
	public static void copy(@NotNull File in, @NotNull OutputStream out, @NotNull CopyOption... options) throws IOException {
		Objects.requireNonNull(in);
		Objects.requireNonNull(out);
		copy(in.toPath(), out, options);
	}

	/**
	 * 将输入文件的数据全部写入输出流，并将异常交由异常处理器处理
	 *
	 * @param in               输入文件
	 * @param out              输出流
	 * @param exceptionHandler 异常处理器
	 * @param options          选项
	 * @return 若本次执行没有出现异常，返回true
	 */
	public static boolean copy(@NotNull File in, @NotNull OutputStream out, @Nullable IExceptionHandler<IOException> exceptionHandler, @NotNull CopyOption... options) {
		Objects.requireNonNull(in);
		Objects.requireNonNull(out);
		return copy(in.toPath(), out, exceptionHandler, options);
	}

	/**
	 * 将输入流的数据全部写入输出文件
	 * 若目标文件不存在则创建，若目标文件存在则覆盖
	 *
	 * @param in      输入流
	 * @param out     输出文件
	 * @param options 选项
	 * @throws IOException 当出现异常时抛出
	 */
	public static void copy(@NotNull InputStream in, @NotNull Path out, @NotNull CopyOption... options) throws IOException {
		Objects.requireNonNull(in);
		Objects.requireNonNull(out);
		OutputStream o = null;
		try {
			createFileIfNotExists(out);
			o = Files.newOutputStream(out, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
			copy(in, o, options);
		} finally {
			closeQuietly(o);
		}
	}

	/**
	 * 将输入文件的数据全部写入输出流，并将异常交由异常处理器处理
	 *
	 * @param in               输入流
	 * @param out              输出文件
	 * @param exceptionHandler 异常处理器，可为空
	 * @param options          选项
	 * @return 若本次执行没有出现异常，返回true
	 */
	public static boolean copy(@NotNull InputStream in, @NotNull Path out, @Nullable IExceptionHandler<IOException> exceptionHandler, @NotNull CopyOption... options) {
		Objects.requireNonNull(in);
		Objects.requireNonNull(out);
		try {
			copy(in, out, options);
			return true;
		} catch (IOException e) {
			if (exceptionHandler != null) {
				exceptionHandler.accept(e);
			}
			return false;
		}
	}

	/**
	 * 将输入流的数据全部写入输出文件
	 * 若目标文件不存在则创建，若目标文件存在则覆盖
	 *
	 * @param in      输入流
	 * @param out     输出文件
	 * @param options 选项
	 * @throws IOException 当出现异常时抛出
	 */
	public static void copy(@NotNull InputStream in, @NotNull File out, @NotNull CopyOption... options) throws IOException {
		Objects.requireNonNull(in);
		Objects.requireNonNull(out);
		copy(in, out.toPath(), options);
	}

	/**
	 * 将输入文件的数据全部写入输出流，并将异常交由异常处理器处理
	 *
	 * @param in               输入流
	 * @param out              输出文件
	 * @param exceptionHandler 异常处理器，可为空
	 * @param options          选项
	 * @return 若本次执行没有出现异常，返回true
	 */
	public static boolean copy(@NotNull InputStream in, @NotNull File out, @Nullable IExceptionHandler<IOException> exceptionHandler, @NotNull CopyOption... options) {
		Objects.requireNonNull(in);
		Objects.requireNonNull(out);
		return copy(in, out.toPath(), exceptionHandler, options);
	}

	/**
	 * 将输入流的数据全部写入输出文件
	 * 若目标文件不存在则创建，若目标文件存在则覆盖
	 *
	 * @param in      输入流
	 * @param out     输出文件
	 * @param options 选项
	 * @throws IOException 当出现异常时抛出
	 */
	public static void copy(@NotNull Path in, @NotNull Path out, @NotNull CopyOption... options) throws IOException {
		Objects.requireNonNull(in);
		Objects.requireNonNull(out);
		InputStream i = null;
		OutputStream o = null;
		try {
			i = Files.newInputStream(in);
			createFileIfNotExists(out);
			o = Files.newOutputStream(out, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
			copy(i, o, options);
		} finally {
			IOUtils.closeQuietly(o, i);
		}
	}

	/**
	 * 将输入文件的数据全部写入输出文件，并将异常交由异常处理器处理
	 * 若目标文件不存在则创建，若目标文件存在则覆盖
	 *
	 * @param in               输入文件
	 * @param out              输出文件
	 * @param exceptionHandler 异常处理器，可为空
	 * @param options          选项
	 * @return 若本次执行没有出现异常，返回true
	 */
	public static boolean copy(@NotNull Path in, @NotNull Path out, @Nullable IExceptionHandler<IOException> exceptionHandler, @NotNull CopyOption... options) {
		Objects.requireNonNull(in);
		Objects.requireNonNull(out);
		try {
			copy(in, out, options);
			return true;
		} catch (IOException e) {
			if (exceptionHandler != null) {
				exceptionHandler.accept(e);
			}
			return false;
		}
	}

	/**
	 * 将输入流的数据全部写入输出文件
	 * 若目标文件不存在则创建，若目标文件存在则覆盖
	 *
	 * @param in      输入流
	 * @param out     输出文件
	 * @param options 选项
	 * @throws IOException 当出现异常时抛出
	 */
	public static void copy(@NotNull Path in, @NotNull File out, @NotNull CopyOption... options) throws IOException {
		Objects.requireNonNull(in);
		Objects.requireNonNull(out);
		copy(in, out.toPath(), options);
	}

	/**
	 * 将输入文件的数据全部写入输出文件，并将异常交由异常处理器处理
	 * 若目标文件不存在则创建，若目标文件存在则覆盖
	 *
	 * @param in               输入文件
	 * @param out              输出文件
	 * @param exceptionHandler 异常处理器，可为空
	 * @param options          选项
	 * @return 若本次执行没有出现异常，返回true
	 */
	public static boolean copy(@NotNull Path in, @NotNull File out, @Nullable IExceptionHandler<IOException> exceptionHandler, @NotNull CopyOption... options) {
		Objects.requireNonNull(in);
		Objects.requireNonNull(out);
		try {
			copy(in, out, options);
			return true;
		} catch (IOException e) {
			if (exceptionHandler != null) {
				exceptionHandler.accept(e);
			}
			return false;
		}
	}

	/**
	 * 将输入流的数据全部写入输出文件
	 * 若目标文件不存在则创建，若目标文件存在则覆盖
	 *
	 * @param in      输入流
	 * @param out     输出文件
	 * @param options 选项
	 * @throws IOException 当出现异常时抛出
	 */
	public static void copy(@NotNull File in, @NotNull Path out, @NotNull CopyOption... options) throws IOException {
		Objects.requireNonNull(in);
		Objects.requireNonNull(out);
		copy(in.toPath(), out, options);
	}

	/**
	 * 将输入文件的数据全部写入输出文件，并将异常交由异常处理器处理
	 * 若目标文件不存在则创建，若目标文件存在则覆盖
	 *
	 * @param in               输入文件
	 * @param out              输出文件
	 * @param exceptionHandler 异常处理器，可为空
	 * @param options          选项
	 * @return 若本次执行没有出现异常，返回true
	 */
	public static boolean copy(@NotNull File in, @NotNull Path out, @Nullable IExceptionHandler<IOException> exceptionHandler, @NotNull CopyOption... options) {
		Objects.requireNonNull(in);
		Objects.requireNonNull(out);
		try {
			copy(in, out, options);
			return true;
		} catch (IOException e) {
			if (exceptionHandler != null) {
				exceptionHandler.accept(e);
			}
			return false;
		}
	}

	/**
	 * 将输入流的数据全部写入输出文件
	 * 若目标文件不存在则创建，若目标文件存在则覆盖
	 *
	 * @param in      输入流
	 * @param out     输出文件
	 * @param options 选项
	 * @throws IOException 当出现异常时抛出
	 */
	public static void copy(@NotNull File in, @NotNull File out, @NotNull CopyOption... options) throws IOException {
		Objects.requireNonNull(in);
		Objects.requireNonNull(out);
		copy(in.toPath(), out.toPath(), options);
	}

	/**
	 * 将输入文件的数据全部写入输出文件，并将异常交由异常处理器处理
	 * 若目标文件不存在则创建，若目标文件存在则覆盖
	 *
	 * @param in               输入文件
	 * @param out              输出文件
	 * @param exceptionHandler 异常处理器，可为空
	 * @param options          选项
	 * @return 若本次执行没有出现异常，返回true
	 */
	public static boolean copy(@NotNull File in, @NotNull File out, @Nullable IExceptionHandler<IOException> exceptionHandler, @NotNull CopyOption... options) {
		Objects.requireNonNull(in);
		Objects.requireNonNull(out);
		try {
			copy(in.toPath(), out.toPath(), options);
			return true;
		} catch (IOException e) {
			if (exceptionHandler != null) {
				exceptionHandler.accept(e);
			}
			return false;
		}
	}

	/**
	 * 如果文件不存在，则创建
	 *
	 * @param path 文件
	 * @return 若创建完成，返回true
	 * @throws IOException 当出现异常时抛出
	 */
	public static boolean createFileIfNotExists(@NotNull Path path) throws IOException {
		Objects.requireNonNull(path);
		return createFileIfNotExists(path.toFile());
	}

	/**
	 * 如果文件不存在，则创建
	 *
	 * @param file 文件
	 * @return 若创建完成，返回true
	 * @throws IOException 当出现异常时抛出
	 */
	public static boolean createFileIfNotExists(@NotNull File file) throws IOException {
		Objects.requireNonNull(file);
		File parent = file.getParentFile();
		if (parent != null && !parent.exists()) {
			//noinspection ResultOfMethodCallIgnored
			parent.mkdirs();
		}
		if (!file.exists()) {
			return file.createNewFile();
		} else {
			return false;
		}
	}

	/**
	 * 如果文件不存在，则创建，并将异常交给异常处理器处理
	 *
	 * @param path             文件
	 * @param exceptionHandler 异常处理器
	 * @return 若创建完成，返回true
	 */
	public static boolean createFileIfNotExists(@NotNull Path path, @Nullable IExceptionHandler<IOException> exceptionHandler) {
		Objects.requireNonNull(path);
		try {
			return createFileIfNotExists(path);
		} catch (IOException e) {
			if (exceptionHandler != null) {
				exceptionHandler.accept(e);
			}
			return false;
		}
	}

	/**
	 * 如果文件不存在，则创建，并将异常交给异常处理器处理
	 *
	 * @param file             文件
	 * @param exceptionHandler 异常处理器
	 * @return 若创建完成，返回true
	 */
	public static boolean createFileIfNotExists(@NotNull File file, @Nullable IExceptionHandler<IOException> exceptionHandler) {
		Objects.requireNonNull(file);
		try {
			return createFileIfNotExists(file);
		} catch (IOException e) {
			if (exceptionHandler != null) {
				exceptionHandler.accept(e);
			}
			return false;
		}
	}

	/**
	 * 递归删除文件或目录
	 *
	 * @param file 文件或目录
	 */
	@SuppressWarnings("ResultOfMethodCallIgnored")
	public static void delete(@NotNull File file) {
		Objects.requireNonNull(file);
		if (file.isFile()) {
			file.delete();
		} else if (file.isDirectory()) {
			//noinspection ConstantConditions
			for (File f : file.listFiles()) {
				delete(f);
			}
			file.delete();
		}
	}

	/**
	 * 递归删除文件或目录
	 *
	 * @param path 文件或目录
	 */
	public static void delete(@NotNull Path path) {
		Objects.requireNonNull(path);
		delete(path.toFile());
	}
}