package team.floracore.common.util;

import com.google.common.base.*;

import java.util.Map.*;

public class MapEntry<K, V> implements Entry<K, V> {
	K key;
	V value;

	public MapEntry(K key, V value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public K getKey() {
		return key;
	}

	@Override
	public V getValue() {
		return value;
	}

	@Override
	public V setValue(V value) {
		return this.value = value;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Entry) {
			Entry<?, ?> o = (Entry<?, ?>) obj;
			return Objects.equal(this.getKey(), o.getKey()) && Objects.equal(this.getValue(), o.getValue());
		} else
			return false;
	}

	@Override
	public String toString() {
		return key + ":" + value;
	}

	@Override
	public int hashCode() {
		int keyHash = key == null ? 0 : key.hashCode();
		int valueHash = value == null ? 0 : value.hashCode();
		return keyHash + valueHash;
	}
}
