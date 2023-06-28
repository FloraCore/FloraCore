package team.floracore.common.util.object;

public class PrintStackTraceExceptionHandler<T extends Throwable> implements IExceptionHandler<T> {
    @Override
    public void accept(T t) {
        t.printStackTrace();
    }
}