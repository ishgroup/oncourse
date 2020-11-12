/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api

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
