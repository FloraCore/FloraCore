package team.floracore.common.util.object;

import java.util.function.Consumer;

public interface IExceptionHandler<T extends Throwable> extends Consumer<T> {
	@Override
	void accept(T t);
}