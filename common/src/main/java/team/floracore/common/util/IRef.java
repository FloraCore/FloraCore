package team.floracore.common.util;

public interface IRef<T> {
    void set(T obj);

    T get();

    @Override
    int hashCode();

    @Override
    boolean equals(Object obj);
}
