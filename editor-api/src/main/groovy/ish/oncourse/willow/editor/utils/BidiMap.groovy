package ish.oncourse.willow.editor.utils

class BidiMap<K, V> extends HashMap<K, V> {

    private HashMap<V,K> valueKeyMap = new HashMap<V, K>()

    BidiMap() {
        super()
    }

    @Override
    V put(K key, V value) {
        valueKeyMap.put(value, key)
        super.put(key, value)
    }

    K getByValue(V value) {
        valueKeyMap.get(value)
    }
}
