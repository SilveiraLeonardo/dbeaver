
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Map with int key.
 */

public class IntKeyMap<VALUE> extends ConcurrentHashMap<Integer, VALUE> {

    /**
     * Add a new mapping with the specified key and value to the IntKeyMap.
     *
     * @param key   key with which the specified value is to be associated.
     * @param value value to be associated with the specified key.
     * @return previous value associated with specified key, or {@code null}
     * if there was no mapping for key.
     */
    public VALUE put(int key, VALUE value) {
        return super.put(key, value);
    }

    /**
     * Returns the value to which the specified key is mapped in this IntKeyMap,
     * or {@code null} if the map contains no mapping for this key.
     *
     * @param key the key whose associated value is to be returned.
     * @return the value to which this map maps the specified key, or
     * {@code null} if the map contains no mapping for this key.
     */
    public VALUE get(int key) {
        return super.get(key);
    }

    /**
     * Removes the mapping for this key in the IntKeyMap if present.
     *
     * @param key key whose mapping is to be removed from the map.
     * @return previous value associated with specified key, or {@code null}
     * if there was no mapping for key.
     */
    public VALUE remove(int key) {
        return super.remove(key);
    }

    /**
     * Returns {@code true} if this map contains a mapping for the specified key.
     *
     * @param key key whose presence in this map is to be tested.
     * @return {@code true} if this map contains a mapping for the specified key.
     */
    public boolean containsKey(int key) {
        return super.containsKey(key);
    }
}
