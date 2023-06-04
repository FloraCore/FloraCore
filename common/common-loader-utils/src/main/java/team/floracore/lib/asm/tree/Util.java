package team.floracore.lib.asm.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Utility methods to convert an array of primitive or object values to a mutable ArrayList, not
 * baked by the array (unlike {@link java.util.Arrays#asList}).
 *
 * @author Eric Bruneton
 */
final class Util {

	private Util() {
	}

	static <T> List<T> add(final List<T> list, final T element) {
		List<T> newList = list == null ? new ArrayList<>(1) : list;
		newList.add(element);
		return newList;
	}

	static <T> List<T> asArrayList(final int length) {
		List<T> list = new ArrayList<>(length);
		for (int i = 0; i < length; ++i) {
			list.add(null);
		}
		return list;
	}

	static <T> List<T> asArrayList(final T[] array) {
		if (array == null) {
			return new ArrayList<>();
		}
		ArrayList<T> list = new ArrayList<>(array.length);
		Collections.addAll(list, array);
		return list;
	}

	static List<Byte> asArrayList(final byte[] byteArray) {
		if (byteArray == null) {
			return new ArrayList<>();
		}
		ArrayList<Byte> byteList = new ArrayList<>(byteArray.length);
		for (byte b : byteArray) {
			byteList.add(b); // NOPMD(UseArraysAsList): we want a modifiable list.
		}
		return byteList;
	}

	static List<Boolean> asArrayList(final boolean[] booleanArray) {
		if (booleanArray == null) {
			return new ArrayList<>();
		}
		ArrayList<Boolean> booleanList = new ArrayList<>(booleanArray.length);
		for (boolean b : booleanArray) {
			booleanList.add(b); // NOPMD(UseArraysAsList): we want a modifiable list.
		}
		return booleanList;
	}

	static List<Short> asArrayList(final short[] shortArray) {
		if (shortArray == null) {
			return new ArrayList<>();
		}
		ArrayList<Short> shortList = new ArrayList<>(shortArray.length);
		for (short s : shortArray) {
			shortList.add(s); // NOPMD(UseArraysAsList): we want a modifiable list.
		}
		return shortList;
	}

	static List<Character> asArrayList(final char[] charArray) {
		if (charArray == null) {
			return new ArrayList<>();
		}
		ArrayList<Character> charList = new ArrayList<>(charArray.length);
		for (char c : charArray) {
			charList.add(c); // NOPMD(UseArraysAsList): we want a modifiable list.
		}
		return charList;
	}

	static List<Integer> asArrayList(final int[] intArray) {
		if (intArray == null) {
			return new ArrayList<>();
		}
		ArrayList<Integer> intList = new ArrayList<>(intArray.length);
		for (int i : intArray) {
			intList.add(i); // NOPMD(UseArraysAsList): we want a modifiable list.
		}
		return intList;
	}

	static List<Float> asArrayList(final float[] floatArray) {
		if (floatArray == null) {
			return new ArrayList<>();
		}
		ArrayList<Float> floatList = new ArrayList<>(floatArray.length);
		for (float f : floatArray) {
			floatList.add(f); // NOPMD(UseArraysAsList): we want a modifiable list.
		}
		return floatList;
	}

	static List<Long> asArrayList(final long[] longArray) {
		if (longArray == null) {
			return new ArrayList<>();
		}
		ArrayList<Long> longList = new ArrayList<>(longArray.length);
		for (long l : longArray) {
			longList.add(l); // NOPMD(UseArraysAsList): we want a modifiable list.
		}
		return longList;
	}

	static List<Double> asArrayList(final double[] doubleArray) {
		if (doubleArray == null) {
			return new ArrayList<>();
		}
		ArrayList<Double> doubleList = new ArrayList<>(doubleArray.length);
		for (double d : doubleArray) {
			doubleList.add(d); // NOPMD(UseArraysAsList): we want a modifiable list.
		}
		return doubleList;
	}

	static <T> List<T> asArrayList(final int length, final T[] array) {
		List<T> list = new ArrayList<>(length);
		list.addAll(Arrays.asList(array).subList(0, length));
		return list;
	}
}
