package team.floracore.common.util;

import java.util.*;
import java.util.function.*;

public final class Iterators {

    public static <E> boolean tryIterate(Iterable<E> iterable, Throwing.Consumer<E> action) {
        boolean success = true;
        for (E element : iterable) {
            try {
                action.accept(element);
            } catch (Exception e) {
                e.printStackTrace();
                success = false;
            }
        }
        return success;
    }

    public static <I, O> boolean tryIterate(Iterable<I> iterable, Function<I, O> mapping, Consumer<O> action) {
        boolean success = true;
        for (I element : iterable) {
            try {
                action.accept(mapping.apply(element));
            } catch (Exception e) {
                e.printStackTrace();
                success = false;
            }
        }
        return success;
    }

    public static <E> boolean tryIterate(E[] array, Consumer<E> action) {
        boolean success = true;
        for (E element : array) {
            try {
                action.accept(element);
            } catch (Exception e) {
                e.printStackTrace();
                success = false;
            }
        }
        return success;
    }

    public static <I, O> boolean tryIterate(I[] array, Function<I, O> mapping, Consumer<O> action) {
        boolean success = true;
        for (I element : array) {
            try {
                action.accept(mapping.apply(element));
            } catch (Exception e) {
                e.printStackTrace();
                success = false;
            }
        }
        return success;
    }

    public static <E> List<List<E>> divideIterable(Iterable<E> source, int size) {
        List<List<E>> lists = new ArrayList<>();
        Iterator<E> it = source.iterator();
        while (it.hasNext()) {
            List<E> subList = new ArrayList<>();
            for (int i = 0; it.hasNext() && i < size; i++) {
                subList.add(it.next());
            }
            lists.add(subList);
        }
        return lists;
    }

}
