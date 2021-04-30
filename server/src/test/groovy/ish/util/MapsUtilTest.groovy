/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.util


import org.junit.jupiter.api.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNull

class MapsUtilTest {

	@Test
    void testGetKeyForValue() throws Exception {
		Map<Object, Object> testMap1 = new HashMap<>()

        Object k_1 = new Object()
        Object v_1 = new Object()
        Object k_2 = new Object()
        Object v_2 = null
        Object k_3 = null
        Object v_3 = new Object()

        testMap1.put(k_1, v_1)
        testMap1.put(k_2, v_2)
        testMap1.put(k_3, v_3)

        assertEquals(k_1, MapsUtil.getKeyForValue(v_1, testMap1))
        assertEquals(k_2, MapsUtil.getKeyForValue(null, testMap1))
        assertNull(MapsUtil.getKeyForValue(v_3, testMap1))

        Map<Object, Object> testMap2 = new HashMap<>()
        assertNull(MapsUtil.getKeyForValue(v_1, testMap2))
        assertNull(MapsUtil.getKeyForValue(null, testMap2))
        assertNull(MapsUtil.getKeyForValue(v_3, testMap2))
    }
}
